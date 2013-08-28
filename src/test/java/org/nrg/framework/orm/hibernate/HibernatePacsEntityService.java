/*
 * org.nrg.framework.orm.hibernate.HibernatePacsEntityService
 * XNAT http://www.xnat.org
 * Copyright (c) 2013, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 7/1/13 5:30 PM
 */
package org.nrg.framework.orm.hibernate;

import javax.inject.Inject;

import org.springframework.stereotype.Service;


@Service
public class HibernatePacsEntityService extends AbstractHibernateEntityService<Pacs> implements PacsEntityService {

    @Inject
    private PacsDAO _dao;

    @Override
    public Pacs newEntity() {
	return new Pacs();
    }

    @Override
    protected PacsDAO getDao() {
	return _dao;
    }
}
