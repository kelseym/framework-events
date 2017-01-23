/*
 * framework: org.nrg.framework.utilities.IniImporter
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *  
 * Released under the Simplified BSD.
 */

package org.nrg.framework.utilities;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.nrg.framework.configuration.ConfigPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Provides utility functions for processing ini files.
 */
public class IniImporter {
    private IniImporter() {
        // Default constructor prevents creating instances.
    }

    @Nonnull
    public static Properties getIniProperties(final ConfigPaths configFolderPaths, final String iniFileSpec) {
        return getIniProperties(configFolderPaths, iniFileSpec, new String[0]);
    }

    @Nonnull
    public static Properties getIniProperties(final ConfigPaths configFolderPaths, final String iniFileSpec, final String... requested) {
        final PropertiesIniStore store = new PropertiesIniStore();
        processIniToStore(store, configFolderPaths, iniFileSpec, requested);
        return store.getIniProperties();
    }

    @Nonnull
    public static Map<String, Map<String, String>> getIniMap(final ConfigPaths configFolderPaths, final String iniFileSpec) {
        return getIniMap(configFolderPaths, iniFileSpec, new String[0]);
    }

    @Nonnull
    public static Map<String, Map<String, String>> getIniMap(final ConfigPaths configFolderPaths, final String iniFileSpec, final String... requested) {
        final MapIniStore store = new MapIniStore();
        processIniToStore(store, configFolderPaths, iniFileSpec, requested);
        return store.getIniMap();
    }

    private static void processIniToStore(final IniStore store, final ConfigPaths configFolderPaths, final String iniFileSpec, final String... requested) {
        final File iniFile = configFolderPaths.findFile(iniFileSpec);
        if (iniFile != null && iniFile.exists()) {
            try {
                // If we find the requested ini file, load that.
                final INIConfiguration ini = new INIConfiguration();
                ini.read(new FileReader(iniFile));

                final Set<String> sections = requested.length == 0 ? ini.getSections() : Sets.newHashSet(requested);
                for (final String section : sections) {
                    // So check whether it contains the tool ID...
                    if (ini.getSections().contains(section)) {
                        // If so, we'll use this...
                        final SubnodeConfiguration configuration = ini.getSection(section);
                        final Iterator<String> keys = configuration.getKeys();
                        while (keys.hasNext()) {
                            final String key = keys.next();
                            store.storeIniValue(section, key, configuration.getString(key, ""));
                        }
                    }
                }
            } catch (IOException | ConfigurationException e) {
                _log.error("An error occurred trying to read the ini file " + iniFile.getAbsolutePath(), e);
            }
        }
    }

    private interface IniStore {
        void storeIniValue(final String section, final String key, final String value);
    }

    private static class MapIniStore implements IniStore {
        @Override
        public void storeIniValue(final String section, final String key, final String value) {
            if (!_iniMap.containsKey(section)) {
                final Map<String, String> sectionMap = Maps.newHashMap();
                _iniMap.put(section, sectionMap);
            }
            _iniMap.get(section).put(key, value);
        }

        public Map<String, Map<String, String>> getIniMap() {
            return _iniMap;
        }

        private final Map<String, Map<String, String>> _iniMap = Maps.newHashMap();
    }

    private static class PropertiesIniStore implements IniStore {
        @Override
        public void storeIniValue(final String section, final String key, final String value) {
            _properties.put(section + "." + key, value);
        }

        public Properties getIniProperties() {
            return _properties;
        }

        private final Properties _properties = new Properties();
    }

    private static final Logger _log = LoggerFactory.getLogger(IniImporter.class);

}
