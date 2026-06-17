package br.gov.goiania.saude.paciente.api.infrastructure.adapter.in.web.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String MDC_REQUEST_ID = "requestId";
    private static final Set<String> SENSITIVE_QUERY_KEYS = Set.of("authorization", "bearer", "password", "token");
    private static final Pattern CPF_PATTERN = Pattern.compile("\\b\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}\\b");
    public static final String HTTP = "[HTTP][OUT] method={} uri=\"{}\" status={} durationMs={} clientIp={} userAgent=\"{}\"";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startedAt = System.currentTimeMillis();
        String requestId = resolveRequestId(request.getHeader(REQUEST_ID_HEADER));
        String method = request.getMethod();
        String uri = sanitizeUriWithQuery(request);
        String clientIp = resolveClientIp(request);
        String userAgent = sanitizeHeaderValue("User-Agent", request.getHeader("User-Agent"));

        MDC.put(MDC_REQUEST_ID, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        log.info("[HTTP][IN ] method={} uri=\"{}\" clientIp={} userAgent=\"{}\"",
                method, uri, clientIp, userAgent);

        try {
            filterChain.doFilter(request, response);
            long elapsedMs = System.currentTimeMillis() - startedAt;
            int status = response.getStatus();
            logByStatus(status, method, uri, elapsedMs, clientIp, userAgent);
        } catch (Exception ex) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            int status = response.getStatus();
            log.error("[HTTP][ERR] method={} uri=\"{}\" status={} durationMs={} clientIp={} userAgent=\"{}\" message=\"{}\"",
                    method, uri, status, elapsedMs, clientIp, userAgent, ex.getMessage(), ex);
            throw ex;
        } finally {
            MDC.remove(MDC_REQUEST_ID);
        }
    }

    private void logByStatus(
            int status,
            String method,
            String uri,
            long elapsedMs,
            String clientIp,
            String userAgent
    ) {
        if (status >= 500) {
            log.error(HTTP,
                    method, uri, status, elapsedMs, clientIp, userAgent);
            return;
        }

        if (status >= 400) {
            log.warn(HTTP,
                    method, uri, status, elapsedMs, clientIp, userAgent);
            return;
        }

        log.info(HTTP,
                method, uri, status, elapsedMs, clientIp, userAgent);
    }

    private String resolveRequestId(String requestIdHeader) {
        if (requestIdHeader == null || requestIdHeader.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return requestIdHeader.trim();
    }

    private String sanitizeUriWithQuery(HttpServletRequest request) {
        StringBuilder uri = new StringBuilder(request.getRequestURI());
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isBlank()) {
            uri.append('?').append(maskSensitiveQueryParams(queryString));
        }
        return maskCpf(uri.toString());
    }

    private String maskSensitiveQueryParams(String queryString) {
        String[] pairs = queryString.split("&");
        StringBuilder sanitized = new StringBuilder();

        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            String[] kv = pair.split("=", 2);
            String key = kv[0];
            String value = kv.length > 1 ? kv[1] : "";

            String safeValue = value;
            if (isSensitiveKey(key)) {
                safeValue = "***";
            } else if ("cpf".equalsIgnoreCase(key)) {
                safeValue = maskCpfValue(value);
            }

            sanitized.append(key).append('=').append(safeValue);
            if (i < pairs.length - 1) {
                sanitized.append('&');
            }
        }

        return sanitized.toString();
    }

    private boolean isSensitiveKey(String key) {
        return SENSITIVE_QUERY_KEYS.contains(key.toLowerCase());
    }

    private String maskCpf(String text) {
        Matcher matcher = CPF_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String cpf = matcher.group();
            matcher.appendReplacement(result, Matcher.quoteReplacement(maskCpfValue(cpf)));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String maskCpfValue(String cpf) {
        String digitsOnly = cpf.replaceAll("\\D", "");
        if (digitsOnly.length() < 4) {
            return "***";
        }
        String last4 = digitsOnly.substring(digitsOnly.length() - 4);
        return "***" + last4;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private String sanitizeHeaderValue(String headerName, String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }

        if ("Authorization".equalsIgnoreCase(headerName) || "password".equalsIgnoreCase(headerName)) {
            return "***";
        }

        return maskCpf(value);
    }
}

