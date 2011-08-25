/**
 * BaseEntityImpl
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 25, 2011
 */
package org.nrg.framework.orm.hibernate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

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
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
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
    }

    /**
     * Sets the timestamp of the last update to the data entity.
     * @param timestamp The timestamp of the last update to the data entity.
     */
    @Override
    public void setTimestamp(Date timestamp) {
        _timestamp = timestamp;
    }

    /**
     * Returns the timestamp of the last update to the data entity.
     * @return The timestamp of the last update to the data entity.
     */
    @Override
    public Date getTimestamp() {
        return _timestamp;
    }
    /**
     * Sets the timestamp of the data entity's creation.
     * @param timestamp The timestamp of the data entity's creation.
     */
    @Override
    public void setCreated(Date created) {
        _created = created;
    }

    /**
     * Returns the timestamp of the data entity's creation.
     * @return The timestamp of the data entity's creation.
     */
    @Override
    public Date getCreated() {
        return _created;
    }

    /**
     * Indicates whether this entity class is deletable. If so, the {@link BaseHibernateDAO#delete(Object)}
     * method will actually delete the entity from the database. Otherwise, it calls the
     * {@link #setEnabled(boolean)} method, setting the enabled state to <b>false</b>. This returns
     * the value of the protected static member {@link #DELETABLE}. You can change the deletable
     * state of an entity class by overriding that value or by overriding this method. 
     * @see BaseHibernateEntity#isDeletable()
     */
    @Override
    @Transient
    public boolean isDeletable() {
        return DELETABLE;
    }
    protected static boolean DELETABLE = true;
    private long _id = 0;
    private boolean _enabled = true;
    private Date _created = new Date();
    private Date _timestamp = new Date();
}
