package br.gov.goiania.saude.paciente.api.infrastructure.adapter.in.web;

import br.gov.goiania.saude.paciente.api.application.dto.ProntuarioEstruturadoResponse;
import br.gov.goiania.saude.paciente.api.application.port.in.ProntuarioPortIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/prontuario")
public class ProntuarioController {
    private final ProntuarioPortIn prontuarioPortIn;

    public ProntuarioController(ProntuarioPortIn prontuarioPortIn) {
        this.prontuarioPortIn = prontuarioPortIn;
    }

    @GetMapping("/{pacienteId}")
    public ResponseEntity<ProntuarioEstruturadoResponse> buscarProntuarioEstruturado(@PathVariable Long pacienteId) {
        long startedAt = System.currentTimeMillis();
        log.trace("[HTTP] GET /api/prontuario/{pacienteId} recebido");
        log.debug("[HTTP] Parametros: pacienteId={}", pacienteId);

        try {
            ProntuarioEstruturadoResponse response = prontuarioPortIn.buscarProntuarioEstruturado(pacienteId);
            long elapsedMs = System.currentTimeMillis() - startedAt;

            if (response == null) {
                log.warn("[HTTP] Prontuario nao encontrado. pacienteId={} tempoMs={}", pacienteId, elapsedMs);
            }

            log.info("[HTTP] Prontuario consultado. pacienteId={} status=200 tempoMs={}", pacienteId, elapsedMs);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.error("[HTTP] Falha ao buscar prontuario. pacienteId={} tempoMs={}", pacienteId, elapsedMs, ex);
            throw ex;
        }
    }
}
