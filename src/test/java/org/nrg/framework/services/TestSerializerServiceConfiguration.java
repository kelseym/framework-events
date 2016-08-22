package org.nrg.framework.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class TestSerializerServiceConfiguration {
    @Bean
    public SerializerService serializerService() {
        return new SerializerService();
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .failOnEmptyBeans(false)
                .featuresToEnable(JsonParser.Feature.ALLOW_SINGLE_QUOTES, JsonParser.Feature.ALLOW_YAML_COMMENTS)
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS, SerializationFeature.WRITE_NULL_MAP_VALUES)
                .modulesToInstall(new Hibernate4Module());
    }
}
