package org.nrg.framework.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.nrg.framework.datacache.SerializerRegistry;
import org.nrg.framework.orm.hibernate.HibernateEntityPackageList;
import org.nrg.framework.services.SerializerService;
import org.nrg.framework.services.YamlObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@ComponentScan({"org.nrg.framework.datacache.impl.hibernate","org.nrg.framework.services.impl"})
public class FrameworkConfig {

    @Bean
    public HibernateEntityPackageList frameworkEntityPackageList(){
        return new HibernateEntityPackageList("org.nrg.framework.datacache");
    }

    @Bean
    public SerializerRegistry serializerRegistry(){
        return new SerializerRegistry();
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

    @Bean
    public YamlObjectMapper yamlObjectMapper() {
        return new YamlObjectMapper();
    }

    @Bean
    public SerializerService serializerService() {
        return new SerializerService();
    }
}
