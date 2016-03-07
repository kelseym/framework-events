/**
 * HibernateVersionedEntityService
 * (C) 2014 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 8/5/2014 by Rick Herrick
 */
package org.nrg.framework.orm.versioned;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * HibernateVersionedEntityService class.
 *
 * @author Rick Herrick
 */
@Service
public class HibernateVersionedEntityService extends AbstractHibernateEntityService<VersionedEntity, VersionedEntityDAO> implements VersionedEntityService {
    private static final Logger _log = LoggerFactory.getLogger(HibernateVersionedEntityService.class);

    public HibernateVersionedEntityService() {
        _log.debug("Created a new instance of the HibernateVersionedEntityService class.");
    }

    @Transactional
    @Override
    public List<VersionedEntity> findByDate(final Date date) {
        return getDao().findByField3(date);
    }
}
