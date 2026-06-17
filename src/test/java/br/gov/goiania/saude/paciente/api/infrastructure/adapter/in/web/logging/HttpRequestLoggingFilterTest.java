package br.gov.goiania.saude.paciente.api.infrastructure.adapter.in.web.logging;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HttpRequestLoggingFilterTest {

    private final HttpRequestLoggingFilter filter = new HttpRequestLoggingFilter();

    @Test
    void devePropagarRequestIdQuandoHeaderExistir() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/pacientes/1");
        request.addHeader("X-Request-Id", "req-123");
        request.addHeader("User-Agent", "JUnit");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertEquals("req-123", response.getHeader("X-Request-Id"));
    }

    @Test
    void deveGerarRequestIdQuandoHeaderNaoExistir() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/pacientes/cpf/12345678901");
        request.setQueryString("cpf=12345678901&page=0");
        request.addHeader("User-Agent", "JUnit");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        String generatedRequestId = response.getHeader("X-Request-Id");
        assertFalse(generatedRequestId == null || generatedRequestId.isBlank());
    }
}

