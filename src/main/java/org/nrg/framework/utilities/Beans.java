package org.nrg.framework.utilities;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.core.env.*;

import java.lang.reflect.InvocationTargetException;
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
