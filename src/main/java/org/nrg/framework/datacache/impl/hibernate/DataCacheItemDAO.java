/*
 * org.nrg.framework.datacache.impl.hibernate.DataCacheItemDAO
 * TIP is developed by the Neuroinformatics Research Group
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 6/11/14 3:19 PM
 */

package org.nrg.framework.datacache.impl.hibernate;

import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.framework.datacache.DataCacheItem;
import org.springframework.stereotype.Repository;

@Repository
public class DataCacheItemDAO extends AbstractHibernateDAO<DataCacheItem> {
    public DataCacheItem getByKey(final String key) {
        return findByUniqueProperty("key", key);
    }
}
