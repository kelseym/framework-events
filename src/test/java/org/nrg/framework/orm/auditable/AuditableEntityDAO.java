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
package org.nrg.framework.orm.auditable;

import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.springframework.stereotype.Repository;

@Repository
public class AuditableEntityDAO extends AbstractHibernateDAO<AuditableEntity> {

}
