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
