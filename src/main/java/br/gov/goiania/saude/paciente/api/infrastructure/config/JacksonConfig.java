package br.gov.goiania.saude.paciente.api.infrastructure.config;

import br.gov.goiania.saude.paciente.api.shared.util.FormatadorUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(FormatadorUtil.FORMATTER_DATA));
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(FormatadorUtil.FORMATTER_DATA_HORA));
        javaTimeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(FormatadorUtil.FORMATTER_HORA));

        javaTimeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(FormatadorUtil.FORMATTER_DATA));
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(FormatadorUtil.FORMATTER_DATA_HORA));
        javaTimeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(FormatadorUtil.FORMATTER_HORA));

        return builder
                .modules(javaTimeModule)
                .timeZone(TimeZone.getTimeZone("America/Sao_Paulo"))
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}
