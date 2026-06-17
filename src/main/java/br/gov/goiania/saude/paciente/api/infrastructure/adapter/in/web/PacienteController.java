package br.gov.goiania.saude.paciente.api.infrastructure.adapter.in.web;

import br.gov.goiania.saude.paciente.api.application.dto.PacienteResponse;
import br.gov.goiania.saude.paciente.api.application.port.in.PacientePortIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacientePortIn paciente;

    public PacienteController(PacientePortIn paciente) {
        this.paciente = paciente;
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PacienteResponse> buscarPorCpf(@PathVariable("cpf") String cpf) {
        long startedAt = System.currentTimeMillis();
        log.trace("[HTTP] GET /api/pacientes/cpf/{cpf} recebido");
        log.debug("[HTTP] Parametros: cpf={}", cpf);

        try {
            PacienteResponse response = this.paciente.buscarPorCpf(cpf);
            long elapsedMs = System.currentTimeMillis() - startedAt;

            if (response == null) {
                log.warn("[HTTP] Paciente nao encontrado por cpf. cpf={} tempoMs={}", cpf, elapsedMs);
            }

            log.info("[HTTP] Paciente encontrado por cpf. cpf={} status=200 tempoMs={}", cpf, elapsedMs);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.error("[HTTP] Falha ao buscar paciente por cpf. cpf={} tempoMs={}", cpf, elapsedMs, ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> buscarPorId(@PathVariable Long id) {
        long startedAt = System.currentTimeMillis();
        log.trace("[HTTP] GET /api/pacientes/{id} recebido");
        log.debug("[HTTP] Parametros: id={}", id);

        try {
            PacienteResponse response = paciente.buscarPorId(id);
            long elapsedMs = System.currentTimeMillis() - startedAt;

            if (response == null) {
                log.warn("[HTTP] Paciente nao encontrado por id. id={} tempoMs={}", id, elapsedMs);
            }

            log.info("[HTTP] Paciente encontrado por id. id={} status=200 tempoMs={}", id, elapsedMs);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            long elapsedMs = System.currentTimeMillis() - startedAt;
            log.error("[HTTP] Falha ao buscar paciente por id. id={} tempoMs={}", id, elapsedMs, ex);
            throw ex;
        }
    }
}
