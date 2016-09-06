package org.nrg.framework.beans;

import org.apache.commons.beanutils.BeanUtils;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.springframework.core.env.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Beans {
    public static <T> T getInitializedBean(final Properties properties, final Class<? extends T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        final T bean = clazz.newInstance();
        for (final Object key : properties.keySet()) {
            BeanUtils.setProperty(bean, key.toString(), properties.get(key));
        }
        return bean;
    }

    public static Properties getNamespacedProperties(final Environment environment, final String namespace, final boolean truncate) {
        final String regex = "^" + namespace.replaceAll("\\.", "\\\\.") + (namespace.endsWith(".") ? "" : "\\.") + "[A-z0-9_\\.-]+";
        final Properties properties = new Properties();
        if (environment instanceof AbstractEnvironment) {
            final AbstractEnvironment abstractEnvironment = (AbstractEnvironment) environment;
            for (final PropertySource<?> propertySource : abstractEnvironment.getPropertySources()) {
                properties.putAll(getMatchingProperties(propertySource, regex));
            }
        }
        if (!truncate) {
            return properties;
        }
        final Properties truncated = new Properties();
        final int offset = namespace.length() + 1;
        for (final String key : properties.stringPropertyNames()) {
            truncated.put(key.substring(offset), properties.getProperty(key));
        }
        return truncated;
    }

    public static Map<Class<?>, Class<?>> getMixIns() throws NrgServiceException {
        final Map<Class<?>, Class<?>> mixIns = new HashMap<>();
        try {
            for (final Resource resource : BasicXnatResourceLocator.getResources("classpath*:META-INF/xnat/serializers/*-mixin.properties")) {
                final Properties properties;
                try {
                    properties = PropertiesLoaderUtils.loadProperties(resource);
                } catch (IOException e) {
                    throw new NrgServiceException(NrgServiceError.Unknown, "An error occurred attempting to read in mixin properties file " + resource.getFilename(), e);
                }
                for (final String target : properties.stringPropertyNames()) {
                    final String mixIn = properties.getProperty(target);
                    final Class<?> targetClass;
                    try {
                        targetClass = Class.forName(target);
                    } catch (ClassNotFoundException e) {
                        throw new NrgServiceException(NrgServiceError.ConfigurationError, "Could not find class " + target, e);
                    }
                    final Class<?> mixInClass;
                    try {
                        mixInClass = Class.forName(mixIn);
                    } catch (ClassNotFoundException e) {
                        throw new NrgServiceException(NrgServiceError.ConfigurationError, "Could not find class " + mixIn, e);
                    }
                    mixIns.put(targetClass, mixInClass);
                }
            }
        } catch (IOException e) {
            throw new NrgServiceException(NrgServiceError.Unknown, "An error occurred attempting to discover mixin properties files on the classpath.", e);
        }
        return mixIns;

    }

    private static Properties getMatchingProperties(final PropertySource<?> propertySource, final String regex) {
        return new Properties() {{
            if (propertySource instanceof CompositePropertySource) {
                final CompositePropertySource compositePropertySource = (CompositePropertySource) propertySource;
                for (final PropertySource<?> containedPropertySource : compositePropertySource.getPropertySources()) {
                    putAll(getMatchingProperties(containedPropertySource, regex));
                }
            } else if (propertySource instanceof EnumerablePropertySource<?>) {
                EnumerablePropertySource<?> containedPropertySource = (EnumerablePropertySource<?>) propertySource;
                for (final String propertyName : containedPropertySource.getPropertyNames()) {
                    if (propertyName.matches(regex)) {
                        put(propertyName, containedPropertySource.getProperty(propertyName).toString());
                    }
                }
            }
        }};
    }
}
