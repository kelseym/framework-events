/*
 * framework: org.nrg.framework.beans.XnatPluginBeanManager
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.beans;

import com.google.common.collect.Maps;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Service
public class XnatPluginBeanManager {
    public XnatPluginBeanManager() {
        _pluginBeans.putAll(scanForXnatPluginBeans());
    }

    public Set<String> getPluginIds() {
        return _pluginBeans.keySet();
    }

    public XnatPluginBean getPlugin(final String pluginId) {
        return _pluginBeans.get(pluginId);
    }

    public static Map<String, XnatPluginBean> scanForXnatPluginBeans() {
        final Map<String, XnatPluginBean> pluginBeans = Maps.newHashMap();
        try {
            for (final Resource resource : BasicXnatResourceLocator.getResources("classpath*:META-INF/xnat/**/*-plugin.properties")) {
                final Properties properties;
                try {
                    properties = PropertiesLoaderUtils.loadProperties(resource);
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

    private static final Logger _log = LoggerFactory.getLogger(XnatPluginBeanManager.class);

    private final Map<String, XnatPluginBean> _pluginBeans = Maps.newHashMap();
}
