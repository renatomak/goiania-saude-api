package br.gov.goiania.saude.paciente.api.infrastructure.adapter.in.web;

import br.gov.goiania.saude.paciente.api.application.dto.ProntuarioEstruturadoResponse;
import br.gov.goiania.saude.paciente.api.application.port.in.ProntuarioPortIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prontuario")
public class ProntuarioController {
    private final ProntuarioPortIn prontuarioPortIn;

    public ProntuarioController(ProntuarioPortIn prontuarioPortIn) {
        this.prontuarioPortIn = prontuarioPortIn;
    }

    @GetMapping("/{pacienteId}")
    public ResponseEntity<ProntuarioEstruturadoResponse> buscarProntuarioEstruturado(@PathVariable Long pacienteId) {
        ProntuarioEstruturadoResponse response = prontuarioPortIn.buscarProntuarioEstruturado(pacienteId);
        return ResponseEntity.ok(response);
    }
}
