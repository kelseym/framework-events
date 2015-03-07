/**
 * AggregatedAnnotationSessionFactoryBean
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 2/29/12 by rherri01
 */
package org.nrg.framework.orm.hibernate;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import java.io.IOException;
import java.util.*;

public class AggregatedAnnotationSessionFactoryBean extends LocalSessionFactoryBean implements ApplicationContextAware {
    @Override
    public void setPackagesToScan(String[] packagesToScan) {
        _packagesToScan.addAll(Arrays.asList(packagesToScan));
        super.setPackagesToScan(_packagesToScan.toArray(new String[_packagesToScan.size()]));
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        _context = context;
    }

    public void afterPropertiesSet() throws IOException {
        String[] packages = getHibernateEntityPackages();
        setPackagesToScan(packages);
        super.afterPropertiesSet();
    }

    private String[] getHibernateEntityPackages() {
        Map<String, ?> beans = _context.getBeansOfType(HibernateEntityPackageList.class);
        List<String> packages = new ArrayList<>();
        if (beans != null) {
            for (Object item : beans.values()) {
                if (item instanceof HibernateEntityPackageList) {
                    packages.addAll((HibernateEntityPackageList) item);
                }
            }
        }
        return packages.toArray(new String[packages.size()]);
    }

    private ApplicationContext _context;
    private final Set<String> _packagesToScan = new HashSet<>();
}
