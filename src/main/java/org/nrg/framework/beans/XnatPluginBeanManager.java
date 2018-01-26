/*
 * framework: org.nrg.framework.beans.XnatPluginBeanManager
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.beans;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XnatPlugin;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class XnatPluginBeanManager {
    public XnatPluginBeanManager() {
        _pluginBeans = ImmutableMap.copyOf(scanForXnatPluginBeans());
    }

    public Set<String> getPluginIds() {
        return _pluginBeans.keySet();
    }

    public XnatPluginBean getPlugin(final String pluginId) {
        return _pluginBeans.get(pluginId);
    }

    public Map<String, XnatPluginBean> getPluginBeans() {
        return _pluginBeans;
    }

    public static Map<String, XnatPluginBean> scanForXnatPluginBeans() {
        final Map<String, XnatPluginBean> pluginBeans = new HashMap<>();
        try {
            for (final Resource resource : BasicXnatResourceLocator.getResources("classpath*:META-INF/xnat/**/*-plugin.properties")) {
                try {
                    final Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                    if (!properties.containsKey(XnatPlugin.PLUGIN_VERSION)) {
                        final String version = getVersionFromResource(resource);
                        properties.setProperty(XnatPlugin.PLUGIN_VERSION, StringUtils.defaultIfBlank(version, "unknown"));
                    }
                    final XnatPluginBean plugin = new XnatPluginBean(properties);
                    if (_log.isDebugEnabled()) {
                        _log.debug("Found plugin bean {} in file {}", plugin.getId(), resource.getFilename());
                    }
                    pluginBeans.put(plugin.getId(), plugin);
                } catch (IOException e) {
                    _log.error("An error occurred trying to load properties from the resource " + resource.getFilename(), e);
                }
            }
        } catch (IOException e) {
            _log.error("An error occurred trying to locate XNAT plugin definitions. It's likely that none of them were loaded.", e);
        }
        _log.debug(pluginBeans.size() == 0 ? "Found no plugin beans." : "Found a total of {} plugin beans", pluginBeans.size());
        return pluginBeans;
    }

    private static String getVersionFromResource(final Resource resource) throws IOException {
        final Matcher matcher = EXTRACT_PLUGIN_VERSION.matcher(resource.getURI().toString());
        if (matcher.find()) {
            return matcher.group("version");
        }
        return null;
    }

    private static final Logger _log = LoggerFactory.getLogger(XnatPluginBeanManager.class);

    private static final Pattern EXTRACT_PLUGIN_VERSION = Pattern.compile("^.*/[A-Za-z0-9._-]+-(?<version>\\d.*)\\.jar.*$");

    private final Map<String, XnatPluginBean> _pluginBeans;
}
