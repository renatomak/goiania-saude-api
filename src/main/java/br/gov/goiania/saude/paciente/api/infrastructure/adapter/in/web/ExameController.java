package br.gov.goiania.saude.paciente.api.infrastructure.adapter.in.web;

import br.gov.goiania.saude.paciente.api.application.dto.ExameResponse;
import br.gov.goiania.saude.paciente.api.application.usecase.ExameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/atendimentos/{nrAtendimento}/exames")
public class ExameController {
    private final ExameService exameService;

    public ExameController(ExameService exameService) {
        this.exameService = exameService;
    }

    @GetMapping
    public ResponseEntity<List<ExameResponse>> buscarExames(@PathVariable Long nrAtendimento) {
        long startedAt = System.currentTimeMillis();
        log.trace("[HTTP] GET /api/atendimentos/{nrAtendimento}/exames recebido");
        log.debug("[HTTP] Parametros: nrAtendimento={}", nrAtendimento);

        try {
            List<ExameResponse> exames = exameService.buscarExamesPorAtendimento(nrAtendimento);
            long elapsedMs = System.currentTimeMillis() - startedAt;

            if (exames.isEmpty()) {
                log.warn("[HTTP] Nenhum exame encontrado para o atendimento. nrAtendimento={} status=204 tempoMs={}",
                        nrAtendimento, elapsedMs);
                return ResponseEntity.noContent().build();
            }

            log.info("[HTTP] Exames listados para atendimento. nrAtendimento={} status=200 qtd={} tempoMs={}",
                    nrAtendimento, exames.size(), elapsedMs);
            return ResponseEntity.ok(exames);
        } catch (RuntimeException ex) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.error("[HTTP] Falha ao buscar exames por atendimento. nrAtendimento={} tempoMs={}",
                    nrAtendimento, elapsedMs, ex);
            throw ex;
        }
    }
}
