/**
 * RemoteEvent
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 2/9/12 by rherri01
 */
package org.nrg.framework.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.restlet.data.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RemoteEvent extends HashMap<String, String> {
    /**
     * This is used to retrieve the remote logging from configuration.
     */
    @SuppressWarnings("unused")
    public static final String REMOTE_LOG = "org.nrg.xnat.remote";

    public RemoteEvent() {
        _log.debug("Creating default remote event instance");
    }

    @SuppressWarnings("unused")
    public RemoteEvent(Properties properties) throws IOException {
        _log.debug("Creating remote event instance from properties");
        addProperties(properties);
    }

    public RemoteEvent(final Map<String, Object> eventMap) {
        _log.debug("Creating remote event instance from map");
        if (eventMap.containsKey("level")) {
            Object level = eventMap.get("level");
            if (level != null) {
                if (level instanceof Level) {
                    setLevel((Level) level);
                } else {
                    setLevel(Level.toLevel(level.toString()));
                }
            }
        }
        if (eventMap.containsKey("throwableInformation")) {
            final Object throwableInformation = eventMap.get("throwableInformation");
            if (throwableInformation != null) {
                put("throwableInformation", throwableInformation.toString());
            }
        }
        if (eventMap.containsKey("properties")) {
            final Object properties = eventMap.get("properties");
            if (properties != null) {
                if (properties instanceof Properties) {
                    addProperties((Properties) properties);
                } else {
                    put("properties", properties.toString());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public RemoteEvent(Map<String, Object> map, ClientInfo clientInfo) {
        this(map);
        setClientInfo(clientInfo);
        _log.debug("Creating remote event instance from map and client info");
    }

    public Level getLevel() {
        if (containsKey("level")) {
            return Level.toLevel(get("level"));
        }
        if (containsKey("LEVEL")) {
            return Level.toLevel(get("LEVEL"));
        }
        return Level.TRACE;
    }

    public void setLevel(Level level) {
        put("level", level.toString());
    }

    public void setClientInfo(ClientInfo clientInfo) {
        putNotBlank("address", clientInfo.getAddress());
        putNotBlank("port", Integer.toString(clientInfo.getPort()));
        putNotBlank("agent", clientInfo.getAgent());
        putNotBlank("agentName", clientInfo.getAgentName());
        Map<String, String> attributes = clientInfo.getAgentAttributes();
        if (attributes != null && attributes.size() > 0) {
            for (String attribute : attributes.keySet()) {
                putNotBlank(attribute, attributes.get(attribute));
            }
        }
    }

    public Map<String, String> getProperties() {
        return _properties;
    }

    public void setProperties(Map<String, String> properties) {
        _properties.clear();
        _properties.putAll(properties);
    }

    @Override
    public String toString() {
        try {
            // TODO: The replaceAll() call converts single quotes to work properly in escaped SQL queries. This really only needs to be done at the JDBC insert level.
            // There's a modified JDBCAppender which handles this at http://sourceforge.net/projects/jdbcappender, but it hasn't been updated since 2005. That may be OK.
            return _mapper.writeValueAsString(this).replaceAll("'", "\\\\'");
        } catch (IOException exception) {
            return "Error occurred while converting to string: " + exception.getMessage();
        }
    }

    private void putNotBlank(final String key, final String value) {
        if (!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
            put(key, value);
        }
    }

    private void addProperties(final Properties properties) {
        for (String property : properties.stringPropertyNames()) {
            final Object value = properties.get(property);
            if (value instanceof String) {
                put(property, (String) value);
            } else {
                try {
                    put(property, _mapper.writeValueAsString(value));
                } catch (IOException e) {
                    put(property, "Invalid value found for property: " + property);
                }
            }
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(RemoteEvent.class);
    // TODO: This might be the wrong thing to do, since it'll result in a plethora of object mappers.
    private static final ObjectMapper _mapper = new ObjectMapper();
    private Map<String, String> _properties = new HashMap<>();
}
