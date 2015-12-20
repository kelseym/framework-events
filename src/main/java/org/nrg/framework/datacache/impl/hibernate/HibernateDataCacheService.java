/*
 * org.nrg.framework.datacache.impl.hibernate.HibernateDataCacheService
 * TIP is developed by the Neuroinformatics Research Group
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 6/11/14 10:58 AM
 */

package org.nrg.framework.datacache.impl.hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nrg.framework.datacache.DataCacheItem;
import org.nrg.framework.datacache.DataCacheService;
import org.nrg.framework.datacache.SerializerRegistry;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;

@Service
public class HibernateDataCacheService extends AbstractHibernateEntityService<DataCacheItem, DataCacheItemDAO> implements DataCacheService {

    @Override
    @Transactional
    public <T extends Serializable> long put(final String key, final T value) {
        if (_log.isDebugEnabled()) {
            _log.debug("Putting an entry for the key " + key + ": " + value.getClass());
        }
        DataCacheItem item = getDao().getByKey(key);
        if (item != null) {
            if (!item.getType().equals(value.getClass().getName())) {
                // TODO: I don't know if this is the right thing to do here. Need to manage new inserts vs updates.
                throw new NrgServiceRuntimeException(NrgServiceError.AlreadyInitialized, "The " + key + " cache item has already been initialized with a different data type.");
            }
            if (_log.isDebugEnabled()) {
                _log.debug("Updating the value for entry " + key);
            }
            item.setValue(serialize(value));
            getDao().update(item);
        } else {
            item = newEntity(key, serialize(value), value.getClass().getName());
            if (_log.isDebugEnabled()) {
                _log.debug("Creating new entry for key " + key);
            }
        }
        return item.getId();
    }

    @Override
    @Transactional
    public <T extends Serializable> T get(final String key) {
        DataCacheItem item = getDao().getByKey(key);
        if (item == null) {
            return null;
        }
        return deserialize(item);
    }

    @Override
    @Transactional
    public <T extends Serializable> T remove(final String key) {
        DataCacheItem item = getDao().getByKey(key);
        if (item != null) {
            T deserialized = deserialize(item);
            getDao().delete(item);
            return deserialized;
        }
        return null;
    }

    @Override
    @Transactional
    public void clean() {
        // TODO: Implement cache maintenance settings to drive the cache persistence parameters.
    }

    private <T extends Serializable> String serialize(final T value) throws NrgServiceRuntimeException {
        try {
            JsonSerializer<T> serializer = (JsonSerializer<T>) _serializers.getSerializer(value.getClass());
            // If there's no special serializer for this class...
            if (serializer == null) {
                return MAPPER.writeValueAsString(value);
            }
        } catch (ClassNotFoundException e) {
            throw new NrgServiceRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new NrgServiceRuntimeException(e);
        } catch (InstantiationException e) {
            throw new NrgServiceRuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new NrgServiceRuntimeException(e);
        }
        return null;
    }

    private <T extends Serializable> T deserialize(final DataCacheItem item) {
        try {
            return item == null ? null : (T) MAPPER.readValue(item.getValue(), Class.forName(item.getType()));
        } catch (IOException e) {
            throw new NrgServiceRuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new NrgServiceRuntimeException(e);
        }
    }

    @Inject
    private SerializerRegistry _serializers;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger _log = LoggerFactory.getLogger(HibernateDataCacheService.class);
}
