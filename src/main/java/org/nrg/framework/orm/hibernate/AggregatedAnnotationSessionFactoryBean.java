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

import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AggregatedAnnotationSessionFactoryBean extends LocalSessionFactoryBean {
    public void setEntityPackageLists(final List<HibernateEntityPackageList> packageLists) {
        if (packageLists != null) {
            for (final HibernateEntityPackageList list : packageLists) {
                _packagesToScan.addAll(list);
            }
            setPackagesToScan(_packagesToScan.toArray(new String[_packagesToScan.size()]));
        }
    }

    @Override
    public void setPackagesToScan(final String[] packagesToScan) {
        _packagesToScan.addAll(Arrays.asList(packagesToScan));
        super.setPackagesToScan(_packagesToScan.toArray(new String[_packagesToScan.size()]));
    }

    private final Set<String> _packagesToScan = new HashSet<>();
}
