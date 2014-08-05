/*
 * HibernatePacsEntityService
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 9/5/13 12:55 PM
 */
package org.nrg.framework.orm.pacs;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.springframework.stereotype.Service;

@Service
public class HibernatePacsEntityService extends AbstractHibernateEntityService<Pacs, PacsDAO> implements PacsEntityService {
    // Nothing here, only default behavior.
}
