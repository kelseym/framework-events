/*
 * PacsEntityService
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 8/26/13 6:15 PM
 */
package org.nrg.framework.orm.versioned;


import org.nrg.framework.orm.hibernate.BaseHibernateService;

import java.util.Date;
import java.util.List;

public interface VersionedEntityService extends BaseHibernateService<VersionedEntity> {
    List<VersionedEntity> findByDate(final Date date);
}
