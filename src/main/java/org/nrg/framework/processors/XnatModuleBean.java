package org.nrg.framework.processors;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

public class XnatModuleBean {
    public XnatModuleBean(final Properties properties) throws ClassNotFoundException {
        this(properties.getProperty("id"), properties.getProperty("namespace"), properties.getProperty("name"), properties.getProperty("description"), properties.getProperty("beanName"), properties.getProperty("config"), properties.getProperty("target.type"));
    }

    public XnatModuleBean(final String id, final String namespace, final String name, final String description, final String beanName, final String config, final String targetType) throws ClassNotFoundException {
        _id = id;
        _namespace = namespace;
        _name = name;
        _description = description;
        _beanName = StringUtils.isNotBlank(beanName) ? beanName : StringUtils.isBlank(config) ? "" : getBeanName(config);
        _config = config;
        _targetType = targetType;
    }

    public XnatModuleBean(final String id, final String namespace, final String name, final String description, final String beanName, final Class<?> configClass, final Class<?> targetTypeClass) {
        _id = id;
        _namespace = namespace;
        _name = name;
        _description = description;
        _beanName = StringUtils.isNotBlank(beanName) ? beanName : configClass == null ? "" : getBeanName(configClass.getName());
        if (configClass == null) {
            _config = null;
        } else {
            _config = configClass.getName();
            _configClass = configClass;
        }
        if (targetTypeClass == null) {
            _targetType = null;
        } else {
            _targetType = targetTypeClass.getName();
            _targetTypeClass = targetTypeClass;
        }
    }

    public String getId() {
        return _id;
    }

    public String getNamespace() {
        return _namespace;
    }

    public String getName() {
        return _name;
    }

    public String getDescription() {
        return _description;
    }

    public String getBeanName() {
        return _beanName;
    }

    public String getConfig() {
        return _config;
    }

    public String getTargetType() {
        return _targetType;
    }

    public Class<?> getConfigClass() throws ClassNotFoundException {
        if (_configClass != null) {
            return _configClass;
        }
        if (StringUtils.isBlank(_config)) {
            return null;
        }
        return Class.forName(_config);
    }

    public Class<?> getTargetTypeClass() throws ClassNotFoundException {
        if (_targetTypeClass != null) {
            return _targetTypeClass;
        }
        if (StringUtils.isBlank(_targetType)) {
            return null;
        }
        return Class.forName(_targetType);
    }

    private String getBeanName(final String config) {
        final int lastToken = config.lastIndexOf(".");
        return StringUtils.uncapitalize(lastToken == -1 ? config : config.substring(lastToken + 1));
    }

    private final String   _id;
    private final String   _namespace;
    private final String   _name;
    private final String   _description;
    private final String   _beanName;
    private final String   _config;
    private final String   _targetType;
    private       Class<?> _configClass;
    private       Class<?> _targetTypeClass;
}
