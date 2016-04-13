package org.nrg.framework.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.nrg.framework.datacache.SerializerRegistry;
import org.nrg.framework.orm.hibernate.HibernateEntityPackageList;
import org.nrg.framework.services.SerializerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
    public PrettyPrinter prettyPrinter() {
        final DefaultIndenter indenter = new DefaultIndenter("    ", DefaultIndenter.SYS_LF);
        final DefaultPrettyPrinter printer  = new DefaultPrettyPrinter();
        printer.indentObjectsWith(indenter);
        printer.indentArraysWith(indenter);
        return printer;
    }

    @Bean
    public ObjectMapper jsonObjectMapper() {
        final PrettyPrinter printer = prettyPrinter();
        final ObjectMapper  mapper  = new ObjectMapper().setDefaultPrettyPrinter(printer);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return mapper;
    }

    @Bean
    public ObjectMapper yamlObjectMapper() {
        final PrettyPrinter printer = prettyPrinter();
        final ObjectMapper  mapper  = new ObjectMapper(new YAMLFactory()).setDefaultPrettyPrinter(printer);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return mapper;
    }

    @Bean
    public SerializerService serializerService() {
        return new SerializerService();
    }
}
