package br.gov.goiania.saude.paciente.api.shared.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FormatadorUtil {

    private static final Locale LOCALE_PT_BR = Locale.forLanguageTag("pt-BR");

    public static final DateTimeFormatter FORMATTER_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE_PT_BR);

    public static final DateTimeFormatter FORMATTER_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", LOCALE_PT_BR);

    public static final DateTimeFormatter FORMATTER_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss", LOCALE_PT_BR);

    private FormatadorUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String formatarData(LocalDate data) {
        if (data == null) {
            return null;
        }
        return FORMATTER_DATA.format(data);
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            return null;
        }
        return FORMATTER_DATA_HORA.format(dataHora);
    }

    public static String formatarHora(LocalTime hora) {
        if (hora == null) {
            return null;
        }
        return FORMATTER_HORA.format(hora);
    }

    public static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public static String formatarTelefone(String telefone) {
        if (telefone == null) {
            return null;
        }
        String digits = telefone.replaceAll("\\D", "");
        if (digits.length() == 11) {
            return digits.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (digits.length() == 10) {
            return digits.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return telefone;
    }

    public static String formatarCep(String cep) {
        if (cep == null || cep.length() != 8) {
            return cep;
        }
        return cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }
}
