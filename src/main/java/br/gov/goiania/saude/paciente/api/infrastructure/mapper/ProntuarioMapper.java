package br.gov.goiania.saude.paciente.api.infrastructure.mapper;

import br.gov.goiania.saude.paciente.api.application.dto.AihDetalhesResponse;
import br.gov.goiania.saude.paciente.api.application.dto.AtendimentoResponse;
import br.gov.goiania.saude.paciente.api.application.dto.ConteudoResponse;
import br.gov.goiania.saude.paciente.api.application.dto.ProfissionalResponse;
import br.gov.goiania.saude.paciente.api.application.dto.ProntuarioResponse;
import br.gov.goiania.saude.paciente.api.application.dto.RegistroResponse;
import br.gov.goiania.saude.paciente.api.infrastructure.adapter.out.persistence.projection.ProntuarioRaw;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import br.gov.goiania.saude.paciente.api.shared.util.FormatadorUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ProntuarioMapper {

    @Mapping(target = "numeroAtendimento", source = "nrAtendimento")
    @Mapping(target = "dataChegada", expression = "java(formatarDataIso(raw.getDtChegada()))")
    @Mapping(target = "unidade",
            expression = "java(new UnidadeResponse(raw.getUnidadeNome(), raw.getUnidadeTelefone()))")
    @Mapping(target = "tipoAtendimento", source = "tipoAtendimento")
    @Mapping(target = "profissional", expression = "java(mapProfissional(raw))")
    @Mapping(target = "classificacaoRisco", source = "classificacaoRiscoNome")
    @Mapping(target = "possuiAih", source = "possuiAih")
    @Mapping(target = "aihDetalhes", expression = "java(mapAihDetalhes(raw))")
    @Mapping(target = "registros", ignore = true)
    AtendimentoResponse toAtendimentoResponse(ProntuarioRaw raw);

    @Mapping(target = "data", expression = "java(formatarDataIso(raw.getRegistroData()))")
    @Mapping(target = "tipo", expression = "java(mapTipoRegistro(raw.getRegistroTipoId()))")
    @Mapping(target = "conteudo",
            expression = "java(mapConteudo(raw.getRegistroTipoId(), raw.getRegistroConteudo()))")
    RegistroResponse toRegistroResponse(ProntuarioRaw raw);

    default ProntuarioResponse toProntuarioResponse(Long pacienteId, List<ProntuarioRaw> raws) {
        Map<Long, AtendimentoResponse> atendimentoMap = new LinkedHashMap<>();
        Map<Long, List<RegistroResponse>> registrosMap = new HashMap<>();

        for (ProntuarioRaw raw : raws) {
            Long nrAtendimento = raw.getNrAtendimento();

            if (!atendimentoMap.containsKey(nrAtendimento)) {
                atendimentoMap.put(nrAtendimento, toAtendimentoResponse(raw));
                registrosMap.put(nrAtendimento, new ArrayList<>());
            }

            if (isRegistroValido(raw)) {
                registrosMap.get(nrAtendimento).add(toRegistroResponse(raw));
            }
        }

        List<AtendimentoResponse> atendimentosFinal = atendimentoMap.values().stream()
            .map(atend -> new AtendimentoResponse(
                atend.numeroAtendimento(),
                atend.dataChegada(),
                atend.unidade(),
                atend.tipoAtendimento(),
                atend.profissional(),
                atend.classificacaoRisco(),
                atend.possuiAih(),
                atend.aihDetalhes(),
                registrosMap.get(atend.numeroAtendimento())
            ))
            .toList();

        return new ProntuarioResponse(pacienteId, atendimentosFinal);
    }

    default ProfissionalResponse mapProfissional(ProntuarioRaw raw) {
        return new ProfissionalResponse(
            raw.getProfissionalNome(),
            raw.getProfissionalRegistro(),
            raw.getProfissionalTipoConselho(),
            raw.getProfissionalCbo(),
            raw.getProfissionalCboDescricao()
        );
    }

    default AihDetalhesResponse mapAihDetalhes(ProntuarioRaw raw) {
        if (raw.getPossuiAih() == null || !raw.getPossuiAih()) {
            return null;
        }
        return new AihDetalhesResponse(
            formatarDataIso(raw.getAihDataCadastro()),
            raw.getAihPrincipaisSinais(),
            raw.getAihCondicoesInternacao(),
            raw.getAihPrincipaisResultados(),
            raw.getAihDiagnosticoInicial()
        );
    }

    default String formatarDataIso(java.time.LocalDateTime data) {
        return FormatadorUtil.formatarDataHora(data);
    }

    default String mapTipoRegistro(Integer tipoId) {
        if (tipoId == null) {
            return "Indefinido";
        }
        return switch (tipoId) {
            case 1 -> "Avaliacao";
            case 2 -> "Evolucao";
            case 6 -> "Exame";
            default -> null;
        };
    }

    default ConteudoResponse mapConteudo(Integer tipoId, String conteudo) {
        if (tipoId == null) {
            return new ConteudoResponse(null, null, null);
        }
        return switch (tipoId) {
            case 1 -> new ConteudoResponse(conteudo, null, null);
            case 2 -> new ConteudoResponse(null, conteudo, null);
            case 6 -> new ConteudoResponse(null, null, conteudo);
            default -> null;
        };
    }

    default boolean isRegistroValido(ProntuarioRaw raw) {
        if (raw == null) {
            return false;
        }

        String tipo = mapTipoRegistro(raw.getRegistroTipoId());
        return tipo != null && raw.getRegistroConteudo() != null && !raw.getRegistroConteudo().isBlank();
    }
}
