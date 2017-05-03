/*
 * framework: org.nrg.framework.beans.XnatPluginBean
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.beans;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XnatDataModel;
import org.nrg.framework.annotations.XnatPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;
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
        _log4jPropertiesFile = StringUtils.defaultIfBlank(plugin.log4jPropertiesFile(), null);
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
             properties.getProperty(XnatPlugin.PLUGIN_LOG4J_PROPERTIES),
             getDataModelBeans(properties));
    }

    public XnatPluginBean(final String pluginClass, final String id, final String namespace, final String name, final String description, final String beanName, final String entityPackages, final String log4jPropertiesFile, final List<XnatDataModelBean> dataModels) {
        _id = id;
        _name = name;
        _pluginClass = pluginClass;
        _namespace = StringUtils.defaultIfBlank(namespace, null);
        _description = StringUtils.defaultIfBlank(description, null);
        _beanName = StringUtils.defaultIfBlank(beanName, getBeanName(pluginClass));
        _entityPackages.addAll(parseCommaSeparatedList(entityPackages));
        _log4jPropertiesFile = StringUtils.defaultIfBlank(log4jPropertiesFile, null);
        _dataModels.addAll(dataModels);
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
        return ImmutableList.copyOf(_entityPackages);
    }

    public List<XnatDataModelBean> getDataModelBeans() {
        return ImmutableList.copyOf(_dataModels);
    }

    /**
     * Gets all of the available extended attributes. Extended attributes aren't set by the plugin directly but can be
     * used by other applications to configure information about a plugin that might be relevant to consumers of the
     * plugin metadata.
     *
     * @return A map containing the available extended attributes.
     */
    public ListMultimap<String, String> getExtendedAttributes() {
        return Multimaps.unmodifiableListMultimap(_extendedAttributes);
    }

    /**
     * Replaces the available extended attributes.
     *
     * @param extendedAttributes The extended attributes to set.
     */
    public void setExtendedAttributes(final ListMultimap<String, String> extendedAttributes) {
        _extendedAttributes.clear();
        _extendedAttributes.putAll(extendedAttributes);
    }

    /**
     * Gets any values set for the specified extended attribute. If the key doesn't exist in the extended attributes,
     * an empty list is returned.
     *
     * @param key The extended attribute to retrieve.
     *
     * @return A list of all values associated with the extended attribute key.
     */
    @Nonnull
    public List<String> getExtendedAttribute(final String key) {
        final List<String> elements = _extendedAttributes.get(key);
        if (elements == null) {
            return Collections.emptyList();
        }
        return ImmutableList.copyOf(elements);
    }

    /**
     * Adds the indicated value to the specified extended attribute. Note that, if one or more values are already set
     * for the attribute, this adds the value to that list.

     * @param key The extended attribute to set.
     * @param value The value to add to the extended attribute values.
     */
    public void setExtendedAttribute(final String key, final String value) {
        _extendedAttributes.put(key, value);
    }

    /**
     * Adds the indicated values to the specified extended attribute. Note that, if one or more values are already set
     * for the attribute, this adds the values to that list.

     * @param key The extended attribute to set.
     * @param values The values to add to the extended attribute values.
     */
    public void setExtendedAttribute(final String key, final List<String> values) {
        _extendedAttributes.putAll(key, values);
    }

    /**
     * Puts the indicated value to the specified extended attribute. If any values are set for the attribute, this
     * removes them then sets the new value for the attribute.

     * @param key The extended attribute to set.
     * @param value The value to set for the extended attribute values.
     */
    public void replaceExtendedAttribute(final String key, final String value) {
        _extendedAttributes.put(key, value);
    }

    /**
     * Puts the indicated values to the specified extended attribute. If any values are set for the attribute, this adds
     * removes them then sets the new values for the attribute.

     * @param key The extended attribute to set.
     * @param values The values to set for the extended attribute values.
     */
    public void replaceExtendedAttribute(final String key, final List<String> values) {
        _extendedAttributes.putAll(key, values);
    }

    /**
     * Removes the indicated value from the specified extended attribute. If any other values are set for the attribute,
     * they will still be set for the attribute.

     * @param key The extended attribute to set.
     * @param value The value to remove from the extended attribute values.
     */
    public void removeExtendedAttribute(final String key, final String value) {
        _extendedAttributes.remove(key, value);
    }

    /**
     * Removes the specified extended attribute.

     * @param key The extended attribute to remove.
     */
    public void removeExtendedAttribute(final String key) {
        _extendedAttributes.removeAll(key);
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
        properties.setProperty(XnatPlugin.PLUGIN_LOG4J_PROPERTIES, _log4jPropertiesFile);
        for (final XnatDataModelBean dataModel : _dataModels) {
            properties.putAll(dataModel.asProperties());
        }
        return properties;
    }

    private String getBeanName(final String config) {
        final int lastToken = config.lastIndexOf(".");
        return StringUtils.uncapitalize(lastToken == -1 ? config : config.substring(lastToken + 1));
    }

    public String getLog4jPropertiesFile() {
        return _log4jPropertiesFile;
    }

    private static List<String> parseCommaSeparatedList(final String entityPackages) {
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

    private final String _pluginClass;
    private final String _id;
    private final String _namespace;
    private final String _name;
    private final String _description;
    private final String _beanName;
    private final String _log4jPropertiesFile;

    private final List<String>                      _entityPackages       = Lists.newArrayList();
    private final List<XnatDataModelBean>           _dataModels           = Lists.newArrayList();
    private final ArrayListMultimap<String, String> _extendedAttributes   = ArrayListMultimap.create();
}
