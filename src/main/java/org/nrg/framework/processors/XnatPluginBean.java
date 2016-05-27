package org.nrg.framework.processors;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XnatPlugin;

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
             properties.getProperty(XnatPlugin.PLUGIN_ENTITY_PACKAGES));
    }

    public XnatPluginBean(final String pluginClass, final String id, final String namespace, final String name, final String description, final String beanName, final String entityPackages) {
        _pluginClass = pluginClass;
        _id = id;
        _namespace = namespace;
        _name = name;
        _description = description;
        _beanName = StringUtils.isNotBlank(beanName) ? beanName : _id;
        _entityPackages = parsePackages(entityPackages);
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

    private final String       _pluginClass;
    private final String       _id;
    private final String       _namespace;
    private final String       _name;
    private final String       _description;
    private final String       _beanName;
    private final List<String> _entityPackages;
}
