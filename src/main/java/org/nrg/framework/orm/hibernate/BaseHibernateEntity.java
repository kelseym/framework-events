/**
 * BaseHibernateEntity
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate;

import java.util.Date;

public interface BaseHibernateEntity {
    /**
     * Returns the ID of the data entity. This usually maps to the entity's primary
     * key in the appropriate database table.
     * @return The ID of the data entity.
     */
    abstract public long getId();
    /**
     * Sets the ID of the data entity. This usually maps to the entity's primary
     * key in the appropriate database table and, as such, should rarely be used
     * directly.
     * @param id The ID to set for the data entity.
     */
    abstract public void setId(long id);
    /**
     * Indicates whether this entity is currently enabled. For accountability and
     * auditability purposes, many entities can't actually be deleted from the database
     * but must be disabled instead. If this value is false, the entity should be
     * considered to be effectively deleted from the system for purposes of on-going use.
     * Note that new entities should be enabled by default.
     * @return <b>true</b> if the entity is currently enabled, <b>false</b> otherwise.
     */
    abstract public boolean isEnabled();
    /**
     * Sets the enabled flag for the entity. See {@link #isEnabled()} for more information
     * on the enabled state of data entities.
     * @param enabled The enabled state to set on the entity.
     */
    abstract public void setEnabled(boolean enabled);
    /**
     * Returns the timestamp of the data entity's creation.
     * @return The timestamp of the data entity's creation.
     */
    abstract public Date getCreated();
    /**
     * Sets the timestamp of the data entity's creation.
     * @param created The timestamp of the data entity's creation.
     */
    public abstract void setCreated(Date created);
    /**
     * Returns the timestamp of the last update to the data entity.
     * @return The timestamp of the last update to the data entity.
     */
    abstract public Date getTimestamp();
    /**
     * Sets the timestamp of the last update to the data entity.
     * @param timestamp The timestamp of the last update to the data entity.
     */
    public abstract void setTimestamp(Date timestamp);
    /**
     * Returns the timestamp of the data entity's disabling.
     * @return The timestamp of the data entity's disabling.
     */
    abstract public Date getDisabled();
    /**
     * Sets the timestamp of the data entity's disabling.
     * @param disabled The timestamp of the data entity's disabling.
     */
    public abstract void setDisabled(Date disabled);
}
