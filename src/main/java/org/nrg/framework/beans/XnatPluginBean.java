package org.nrg.framework.beans;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XnatPlugin;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
public class XnatPluginBean {
    public XnatPluginBean(final Properties properties) {
        this(properties.getProperty(XnatPlugin.PLUGIN_CLASS),
             properties.getProperty(XnatPlugin.PLUGIN_ID),
             properties.getProperty(XnatPlugin.PLUGIN_NAMESPACE),
             properties.getProperty(XnatPlugin.PLUGIN_NAME),
             properties.getProperty(XnatPlugin.PLUGIN_DESCRIPTION),
             properties.getProperty(XnatPlugin.PLUGIN_BEAN_NAME),
             properties.getProperty(XnatPlugin.PLUGIN_ENTITY_PACKAGES),
             getDataModelBeans(properties));
    }

    public XnatPluginBean(final String pluginClass, final String id, final String namespace, final String name, final String description, final String beanName, final String entityPackages, final List<XnatDataModelBean> dataModelProperties) {
        _pluginClass = pluginClass;
        _id = id;
        _namespace = namespace;
        _name = name;
        _description = description;
        _beanName = StringUtils.isNotBlank(beanName) ? beanName : _id;
        _entityPackages = parsePackages(entityPackages);
        _dataModelProperties = dataModelProperties;
    }

    public static List<XnatPluginBean> findAllXnatPluginBeans() throws IOException {
        final List<XnatPluginBean> beans = new ArrayList<>();
        for (final Resource resource : BasicXnatResourceLocator.getResources("classpath*:META-INF/xnat/**/*-plugin.properties")) {
            final Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            final XnatPluginBean plugin = new XnatPluginBean(properties);
            if (_log.isDebugEnabled()) {
                _log.debug("Found plugin bean {} in file {}", plugin.getId(), resource.getURI().toString());
            }
            beans.add(plugin);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("Found a total of {} plugin beans", beans.size());
        }
        return beans;
    }

    public String getPluginClass() {
        return _pluginClass;
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

    public List<String> getEntityPackages() {
        return new ArrayList<>(_entityPackages);
    }

    public ArrayList<XnatDataModelBean> getDataModelBeans() {
        return new ArrayList<>(_dataModelProperties);
    }

    private String getBeanName(final String config) {
        final int lastToken = config.lastIndexOf(".");
        return StringUtils.uncapitalize(lastToken == -1 ? config : config.substring(lastToken + 1));
    }

    private static List<String> parsePackages(final String entityPackages) {
        if (StringUtils.isBlank(entityPackages)) {
            return Collections.emptyList();
        }
        return Arrays.asList(entityPackages.split("\\s*,\\s*"));
    }

    private static List<XnatDataModelBean> getDataModelBeans(final Properties properties) {
        final Map<String, XnatDataModelBean> dataModels = new HashMap<>();
        for (final String property : properties.stringPropertyNames()) {
            if (property.startsWith(XnatPlugin.PLUGIN_DATA_MODEL_PREFIX)) {
                final String[] atoms = property.split("\\.",  4);
                final String dataModelKey = atoms[1] + ":" + atoms[2];
                final XnatDataModelBean bean;
                if (!dataModels.containsKey(dataModelKey)) {
                    bean = new XnatDataModelBean();
                    bean.setType(dataModelKey);
                    dataModels.put(dataModelKey, bean);
                } else {
                    bean = dataModels.get(dataModelKey);
                }
                try {
                    BeanUtils.setProperty(bean, atoms[3], properties.getProperty(property));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    _log.error("An error occurred trying to set the " + bean.getType() + " property " + atoms[3] + " to the value " + properties.getProperty(property));
                }
            }
        }
        return new ArrayList<>(dataModels.values());
    }

    private static final Logger _log = LoggerFactory.getLogger(XnatPluginBean.class);

    private final String                  _pluginClass;
    private final String                  _id;
    private final String                  _namespace;
    private final String                  _name;
    private final String                  _description;
    private final String                  _beanName;
    private final List<String>            _entityPackages;
    private final List<XnatDataModelBean> _dataModelProperties;
}
