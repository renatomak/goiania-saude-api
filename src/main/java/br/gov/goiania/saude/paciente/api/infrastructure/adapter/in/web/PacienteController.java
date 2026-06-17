package br.gov.goiania.saude.paciente.api.infrastructure.adapter.in.web;

import br.gov.goiania.saude.paciente.api.application.dto.PacienteResponse;
import br.gov.goiania.saude.paciente.api.application.port.in.PacientePortIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacientePortIn paciente;

    public PacienteController(PacientePortIn paciente) {
        this.paciente = paciente;
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PacienteResponse> buscarPorCpf(@PathVariable("cpf") String cpf) {
        PacienteResponse response = this.paciente.buscarPorCpf(cpf);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(paciente.buscarPorId(id));
    }
}
