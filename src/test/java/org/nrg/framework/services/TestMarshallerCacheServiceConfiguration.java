package org.nrg.framework.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan("org.nrg.framework.services.impl")
public class TestMarshallerCacheServiceConfiguration {
    @Bean
    public List<String> marshalablePackages() {
        return Arrays.asList("org.nrg.framework.test.models.containers", "org.nrg.framework.test.models.entities");
    }
}
