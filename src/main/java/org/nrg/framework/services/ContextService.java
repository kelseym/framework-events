/*
 * ContextService
 * (C) 2016 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 */
package org.nrg.framework.services;

import com.google.common.base.Joiner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ContextService implements NrgService, ApplicationContextAware, ServletContextAware {
    /**
     * Returns the existing instance of the ContextService.
     *
     * @return The existing instance of the ContextService.
     */
    public static ContextService getInstance() {
        if (_instances.containsKey("contextService")) {
            return _instances.get("contextService");
        }
        if (_instances.containsKey("rootContextService")) {
            return _instances.get("rootContextService");
        }
        return new ContextService();
    }

    /**
     * Provides the setter for the application context.
     *
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        synchronized (_instances) {
            _instances.putAll(context.getBeansOfType(ContextService.class));
        }
        _contexts.add(context);
    }

    /**
     * Indicates whether the context service instance has an application context.
     *
     * @return <b>true</b> if the service object has an application context.
     */
    public boolean hasApplicationContext() {
        return _contexts.size() > 0;
    }

    /**
     * Provides the setter for the servlet context.
     *
     * @param servletContext The servlet context being set.
     */
    @Override
    public void setServletContext(final ServletContext servletContext) {
        _servletContext = servletContext;
    }

    /**
     * Handles updates to the application context.
     *
     * @param event The context refreshed event. This adds the new context to the contexts available to this service.
     */
    @EventListener
    public void handleContextRefreshedEvent(final ContextRefreshedEvent event) {
        setApplicationContext(event.getApplicationContext());
    }

    /**
     * Gets a bean of the indicated type. If no bean of that type is found, this method throws {@link
     * NoSuchBeanDefinitionException}.
     *
     * @param <T>  The type of the bean to be retrieved.
     * @param type The class of the bean to be retrieved.
     *
     * @return An object of the type.
     *
     * @throws NoSuchBeanDefinitionException When a bean of the indicated type can't be found.
     */
    public <T> T getBean(final Class<T> type) throws NoSuchBeanDefinitionException {
        for (final ApplicationContext context : _contexts) {
            try {
                return context.getBean(type);
            } catch (NoSuchBeanDefinitionException ignored) {
                // This is OK, just means the bean doesn't exist in the current context. Carry on.
            }
        }
        // If we didn't find a valid bean of the type, return null.
        throw new NoSuchBeanDefinitionException(type);
    }

    /**
     * Gets a bean of the indicated type. If no bean of that type is found, null is returned.
     *
     * @param <T>  The type of the bean to be retrieved.
     * @param type The class of the bean to be retrieved.
     *
     * @return An object of the type.
     */
    public <T> T getBeanSafely(final Class<T> type) {
        for (final ApplicationContext context : _contexts) {
            try {
                return context.getBean(type);
            } catch (NoSuchBeanDefinitionException ignored) {
                // This is OK, just means the bean doesn't exist in the current context. Carry on.
            }
        }
        // If we didn't find a valid bean of the type, return null.
        return null;
    }

    /**
     * Gets the bean with the indicated name and type.
     *
     * @param <T>  The type of the bean to be retrieved.
     * @param name The name of the bean to be retrieved.
     * @param type The class of the bean to be retrieved.
     *
     * @return An object of the type.
     *
     * @throws NoSuchBeanDefinitionException When a bean of the indicated type can't be found.
     */
    public <T> T getBean(final String name, final Class<T> type) throws NoSuchBeanDefinitionException {
        for (final ApplicationContext context : _contexts) {
            try {
                return context.getBean(name, type);
            } catch (NoSuchBeanDefinitionException ignored) {
                // This is OK, just means the bean doesn't exist in the current context. Carry on.
            }
        }
        // If we didn't find a valid bean of the name and type, return null.
        throw new NoSuchBeanDefinitionException(type, name);
    }

    /**
     * Gets the bean with the indicated name and type. If no bean with that name and type is found, null is returned.
     *
     * @param <T>  The type of the bean to be retrieved.
     * @param name The name of the bean to be retrieved.
     * @param type The class of the bean to be retrieved.
     *
     * @return An object of the type.
     */
    public <T> T getBeanSafely(final String name, final Class<T> type) {
        for (final ApplicationContext context : _contexts) {
            try {
                return context.getBean(name, type);
            } catch (NoSuchBeanDefinitionException ignored) {
                // This is OK, just means the bean doesn't exist in the current context. Carry on.
            }
        }
        // If we didn't find a valid bean of the name and type, return null.
        throw new NoSuchBeanDefinitionException(type, name);
    }

    /**
     * Gets all beans with the indicated type.
     *
     * @param type The class of the bean to be retrieved.
     * @param <T>  The parameterized class of the bean to be retrieved.
     *
     * @return An object of the type.
     */
    @SuppressWarnings("unused")
    public <T> Map<String, T> getBeansOfType(final Class<T> type) {
        for (final ApplicationContext context : _contexts) {
            final Map<String, T> candidate = context.getBeansOfType(type);
            if (candidate.size() > 0) {
                return candidate;
            }
        }
        return new HashMap<>();
    }

    @SuppressWarnings("unused")
    public URI getConfigurationLocation(final String configuration) {
        return getAppRelativeLocation("WEB-INF", "conf", configuration);
    }

    @SuppressWarnings("unused")
    public InputStream getConfigurationStream(final String configuration) {
        return getAppRelativeStream("WEB-INF", "conf", configuration);
    }

    public URI getAppRelativeLocation(final String... relativePaths) {
        try {
            return _servletContext.getResource(joinPaths(relativePaths)).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            return null;
        }
    }

    private InputStream getAppRelativeStream(final String... relativePaths) {
        return _servletContext.getResourceAsStream(joinPaths(relativePaths));
    }

    private Set<String> getAppRelativeLocationContents(final String... relativePaths) {
        return getAppRelativeLocationContents(null, relativePaths);
    }

    private Set<String> getAppRelativeLocationContents(final FilenameFilter filter, final String... relativePaths) {
        final Set<String> paths = _servletContext.getResourcePaths(joinPaths(relativePaths));
        if (filter == null) {
            return paths;
        }
        final Set<String> accepted = new HashSet<>();
        for (final String path : paths) {
            if (filter.accept(null, getFileName(path))) {
                accepted.add(path);
            }
        }
        return accepted;
    }

    private Set<String> getAppRelativeLocationChildren(final String... relativePaths) {
        return getAppRelativeLocationChildren(null, relativePaths);
    }

    private Set<String> getAppRelativeLocationChildren(final FilenameFilter filter, final String... relativePaths) {
        final Set<String> found = getAppRelativeLocationContents(relativePaths);
        final Set<String> children = new HashSet<>();
        for (final String current : found) {
            if (!current.endsWith("/")) {
                if ((filter == null) || (filter.accept(null, getFileName(current)))) {
                    children.add(current);
                }
            } else {
                children.addAll(getAppRelativeLocationChildren(current));
            }
        }
        return children;
    }

    private static String joinPaths(final String... elements) {
        return Joiner.on("/").join(elements);
    }

    private static String getFileName(final String path) {
        if (path.contains("/")) {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        return path;
    }

    private final static Map<String, ContextService> _instances = new ConcurrentHashMap<>();
    private final        Set<ApplicationContext>     _contexts  = new HashSet<>();
    private ServletContext _servletContext;
}
