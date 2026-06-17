package br.gov.goiania.saude.paciente.api.infrastructure.adapter.in.web;

import br.gov.goiania.saude.paciente.api.application.dto.VacinaDetalheResponse;
import br.gov.goiania.saude.paciente.api.application.dto.VacinaResumoResponse;
import br.gov.goiania.saude.paciente.api.application.port.in.VacinaPortIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
public class VacinaController {

    private final VacinaPortIn vacinaPortIn;

    public VacinaController(VacinaPortIn vacinaPortIn) {
        this.vacinaPortIn = vacinaPortIn;
    }

    @GetMapping("/pacientes/{id}/vacinas")
    public ResponseEntity<List<VacinaResumoResponse>> listarVacinasPorPaciente(@PathVariable("id") Long pacienteId) {
        long startedAt = System.currentTimeMillis();
        log.trace("[HTTP] GET /api/pacientes/{id}/vacinas recebido");
        log.debug("[HTTP] Parametros: pacienteId={}", pacienteId);

        try {
            List<VacinaResumoResponse> response = vacinaPortIn.listarPorPacienteId(pacienteId);
            long elapsedMs = System.currentTimeMillis() - startedAt;

            if (response == null || response.isEmpty()) {
                log.warn("[HTTP] Nenhuma vacina encontrada para o paciente. pacienteId={} tempoMs={}", pacienteId, elapsedMs);
            }

            log.info("[HTTP] Vacinas listadas para paciente. pacienteId={} status=200 qtd={} tempoMs={}",
                    pacienteId, response == null ? 0 : response.size(), elapsedMs);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.error("[HTTP] Falha ao listar vacinas por paciente. pacienteId={} tempoMs={}", pacienteId, elapsedMs, ex);
            throw ex;
        }
    }

    @GetMapping("/vacinas/aplicacoes/{idAplicacao}")
    public ResponseEntity<VacinaDetalheResponse> detalharAplicacao(@PathVariable Long idAplicacao) {
        long startedAt = System.currentTimeMillis();
        log.trace("[HTTP] GET /api/vacinas/aplicacoes/{idAplicacao} recebido");
        log.debug("[HTTP] Parametros: idAplicacao={}", idAplicacao);

        try {
            VacinaDetalheResponse response = vacinaPortIn.buscarDetalhePorAplicacaoId(idAplicacao);
            long elapsedMs = System.currentTimeMillis() - startedAt;

            if (response == null) {
                log.warn("[HTTP] Aplicacao de vacina nao encontrada. idAplicacao={} tempoMs={}", idAplicacao, elapsedMs);
            }

            log.info("[HTTP] Detalhe da aplicacao consultado. idAplicacao={} status=200 tempoMs={}", idAplicacao, elapsedMs);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.error("[HTTP] Falha ao detalhar aplicacao de vacina. idAplicacao={} tempoMs={}", idAplicacao, elapsedMs, ex);
            throw ex;
        }
    }
}
