package org.nrg.framework.beans;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XnatDataModel;
import org.nrg.framework.annotations.XnatPlugin;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
public class XnatPluginBean {
    public XnatPluginBean(final TypeElement element, final XnatPlugin plugin) {
        _id = plugin.value();
        _name = plugin.name();
        _pluginClass = element.getQualifiedName().toString();
        _namespace = StringUtils.defaultIfBlank(plugin.namespace(), null);
        _description = StringUtils.defaultIfBlank(plugin.description(), null);
        _beanName = StringUtils.defaultIfBlank(plugin.beanName(), StringUtils.uncapitalize(element.getSimpleName().toString()));
        _entityPackages.addAll(Arrays.asList(plugin.entityPackages()));
        for (final XnatDataModel dataModel : Arrays.asList(plugin.dataModels())) {
            _dataModels.add(new XnatDataModelBean(dataModel));
        }
    }

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

    public XnatPluginBean(final String pluginClass, final String id, final String namespace, final String name, final String description, final String beanName, final String entityPackages, final List<XnatDataModelBean> dataModels) {
        _id = id;
        _name = name;
        _pluginClass = pluginClass;
        _namespace = StringUtils.defaultIfBlank(namespace, null);
        _description = StringUtils.defaultIfBlank(description, null);
        _beanName = StringUtils.defaultIfBlank(beanName, getBeanName(pluginClass));
        _entityPackages.addAll(parsePackages(entityPackages));
        _dataModels.addAll(dataModels);
    }

    public static Map<String, XnatPluginBean> getXnatPluginBeans() throws IOException {
        if (_pluginBeans.size() == 0 && !_scanned) {
            synchronized (_pluginBeans) {
                _scanned = true;
                for (final Resource resource : BasicXnatResourceLocator.getResources("classpath*:META-INF/xnat/**/*-plugin.properties")) {
                    final Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                    final XnatPluginBean plugin = new XnatPluginBean(properties);
                    if (_log.isDebugEnabled()) {
                        _log.debug("Found plugin bean {} in file {}", plugin.getId(), resource.getURI().toString());
                    }
                    _pluginBeans.put(plugin.getId(), plugin);
                }

                if (_log.isDebugEnabled()) {
                    if (_pluginBeans.size() == 0) {
                        _log.debug("Found no plugin beans.");
                    } else {
                        _log.debug("Found a total of {} plugin beans", _pluginBeans.size());
                    }
                }
            }
        }
        return ImmutableMap.copyOf(_pluginBeans);
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

    public List<XnatDataModelBean> getDataModelBeans() {
        return new ArrayList<>(_dataModels);
    }

    public Properties asProperties() {
        final Properties properties = new Properties();
        properties.setProperty(XnatPlugin.PLUGIN_ID, _id);
        properties.setProperty(XnatPlugin.PLUGIN_BEAN_NAME, _beanName);
        if (StringUtils.isNotBlank(_namespace)) {
            properties.setProperty(XnatPlugin.PLUGIN_NAMESPACE, _namespace);
        }
        properties.setProperty(XnatPlugin.PLUGIN_CLASS, _pluginClass);
        properties.setProperty(XnatPlugin.PLUGIN_NAME, _name);
        properties.setProperty(XnatPlugin.PLUGIN_DESCRIPTION, _description);
        properties.setProperty(XnatPlugin.PLUGIN_ENTITY_PACKAGES, Joiner.on(", ").join(_entityPackages));
        for (final XnatDataModelBean dataModel : _dataModels) {
            properties.putAll(dataModel.asProperties());
        }
        return properties;
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
            if (property.startsWith(XnatDataModelBean.PLUGIN_DATA_MODEL_PREFIX)) {
                final String[] atoms = property.split("\\.", 4);
                final String dataModelKey = atoms[1] + ":" + atoms[2];
                final XnatDataModelBean bean;
                if (!dataModels.containsKey(dataModelKey)) {
                    bean = new XnatDataModelBean(dataModelKey, properties);
                    dataModels.put(dataModelKey, bean);
                }
            }
        }
        return new ArrayList<>(dataModels.values());
    }

    private static final Logger _log = LoggerFactory.getLogger(XnatPluginBean.class);

    private static final Map<String, XnatPluginBean> _pluginBeans = new HashMap<>();
    private static       boolean                     _scanned     = false;

    private final String _pluginClass;
    private final String _id;
    private final String _namespace;
    private final String _name;
    private final String _description;
    private final String _beanName;
    private final List<String>            _entityPackages = new ArrayList<>();
    private final List<XnatDataModelBean> _dataModels     = new ArrayList<>();
}
