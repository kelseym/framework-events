/*
 * org.nrg.framework.orm.hibernate.HibernatePacsEntityService
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 9/5/13 12:55 PM
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
