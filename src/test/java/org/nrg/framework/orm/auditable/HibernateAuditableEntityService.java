/**
 * HibernateAuditableEntityService
 * (C) 2014 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 8/5/2014 by Rick Herrick
 */
package org.nrg.framework.orm.auditable;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * HibernateAuditableEntityService class.
 *
 * @author Rick Herrick
 */
@Service
public class HibernateAuditableEntityService extends AbstractHibernateEntityService<AuditableEntity, AuditableEntityDAO> implements AuditableEntityService {
    private static final Logger _log = LoggerFactory.getLogger(HibernateAuditableEntityService.class);

    public HibernateAuditableEntityService() {
        _log.debug("Created a new instance of the HibernateAuditableEntityService class.");
    }
}
