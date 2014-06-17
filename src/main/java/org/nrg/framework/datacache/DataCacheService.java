/*
 * org.nrg.framework.datacache.DataCacheService
 * TIP is developed by the Neuroinformatics Research Group
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 6/11/14 10:46 AM
 */

package org.nrg.framework.datacache;

import org.nrg.framework.orm.hibernate.BaseHibernateService;

import java.io.Serializable;

public interface DataCacheService extends BaseHibernateService<DataCacheItem> {
    public abstract <T extends Serializable> long put(String key, T value);
    public abstract <T extends Serializable> T get(String key);
    public abstract <T extends Serializable> T remove(String key);
    public abstract void clean();
}
