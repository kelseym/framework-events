package org.nrg.framework.orm.versioned;

import org.nrg.framework.orm.hibernate.HibernateEntityPackageList;
import org.nrg.framework.test.OrmTestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OrmTestConfiguration.class)
@ComponentScan({"org.nrg.framework.orm.versioned", "org.nrg.framework.orm.utils"})
public class VersionedEntityTestsConfiguration {
    @Bean
    public HibernateEntityPackageList versionedEntitiesPackageList() {
        return new HibernateEntityPackageList("org.nrg.framework.orm.versioned");
    }
}
