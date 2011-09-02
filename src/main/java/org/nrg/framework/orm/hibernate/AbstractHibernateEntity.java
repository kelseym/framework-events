/**
 * AbstractHibernateEntity
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 *
 * @author Rick Herrick <rick.herrick@wustl.edu>
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS) 
abstract public class AbstractHibernateEntity implements BaseHibernateEntity {

    /**
     * Returns the ID of the data entity. This usually maps to the entity's primary
     * key in the appropriate database table.
     * @return The ID of the data entity.
     */
    // TODO: @GeneratedValue won't work with H2, but @SequenceGenerator will. Why?
    // @SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Override
    public long getId() {
        return _id;
    }
        
    /**
     * Sets the ID of the data entity. This usually maps to the entity's primary
     * key in the appropriate database table and, as such, should rarely be used
     * directly.
     * @param id The ID to set for the data entity.
     */
    @Override
    public void setId(long id) {
        _id = id;
    }

    /**
     * Indicates whether this entity is currently enabled. For accountability and
     * auditability purposes, many entities can't actually be deleted from the database
     * but must be disabled instead. If this value is false, the entity should be
     * considered to be effectively deleted from the system for purposes of on-going use.
     * Note that new entities should be enabled by default.
     * @return <b>true</b> if the entity is currently enabled, <b>false</b> otherwise.
     * @see BaseHibernateEntity#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return _enabled;
    }

    /**
     * Sets the enabled flag for the entity. See {@link #isEnabled()} for more information
     * on the enabled state of data entities.
     * @param enabled The enabled state to set on the entity.
     * @see BaseHibernateEntity#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        _enabled = enabled;
        _disabled = enabled ? null : new Date();
    }

    /**
     * Returns the timestamp of the data entity's creation.
     * @return The timestamp of the data entity's creation.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Override
    public Date getCreated() {
        return _created;
    }

    /**
     * Sets the timestamp of the data entity's creation.
     * @param timestamp The timestamp of the data entity's creation.
     */
    public void setCreated(Date created) {
        _created = created;
    }

    /**
     * Returns the timestamp of the last update to the data entity.
     * @return The timestamp of the last update to the data entity.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Override
    public Date getTimestamp() {
        return _timestamp;
    }

    /**
     * Sets the timestamp of the last update to the data entity.
     * @param timestamp The timestamp to set for the last update to the data entity.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Override
    public void setTimestamp(Date timestamp) {
        _timestamp = timestamp;
    }
    
    /**
     * Returns the timestamp of the data entity's disabling.
     * @return The timestamp of the data entity's disabling.
     */
    @Temporal(TemporalType.TIMESTAMP)    
    @Override
    public Date getDisabled() {
        return _disabled;
    }
    
    /**
     * Sets the timestamp of the data entity's disabling.
     * @param disabled The timestamp to set for the data entity's disabling.
     */
    @Temporal(TemporalType.TIMESTAMP)    
    @Override
    public void setDisabled(Date disabled) {
        _disabled = disabled;
    }
    
    /**
     * Handles the pre-persist event, which occurs as the object is first persisted
     * to the database, i.e. at creation. This sets the created and timestamp properties
     * to the current date and time.
     */
    // TODO: Need to move to JPA instead of Hibernate to take advantage of persistence lifecycle annotations.
    @PrePersist
    protected void onCreate() {
        _created = _timestamp = new Date();
    }

    /**
     * Handles the pre-update event, which occurs as the object is updated and pushed
     * to the database, i.e. at modification. This sets the timestamp property to the
     * current date and time.
     */
    // TODO: Need to move to JPA instead of Hibernate to take advantage of persistence lifecycle annotations.
    @PreUpdate
    protected void onUpdate() {
        _timestamp = new Date();
    }

    private long _id = 0;
    private boolean _enabled = true;
    private Date _created;
    private Date _timestamp;
    private Date _disabled = new Date(0);
}
