/*
 * AuditableEntityDAO
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 8/26/13 6:15 PM
 */
package org.nrg.framework.orm.versioned;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class VersionedEntityDAO extends AbstractHibernateDAO<VersionedEntity> {
    @SuppressWarnings("unchecked")
    public List<VersionedEntity> findByField3(final Date field3) {
        final Criteria criteria = getSession().createCriteria(VersionedEntity.class);
        criteria.add(Restrictions.eq("field3", field3));
        return criteria.list();
    }
}
