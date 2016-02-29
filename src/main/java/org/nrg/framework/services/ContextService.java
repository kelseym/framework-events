/*
 * ContextService
 * (C) 2016 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 */
package org.nrg.framework.services;

import com.google.common.base.Joiner;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ContextService implements NrgService, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, ServletContextAware {
    /**
     * Public constructor for use by Spring to initialize the ContextService within the application context. Other
     * classes or applications should not call this constructor and should only call the
     *
     * @throws NrgServiceException Thrown when the ContextService is already initialized.
     */
    protected ContextService() throws NrgServiceException {
        if (_instance != null) {
            throw new NrgServiceException(NrgServiceError.AlreadyInitialized, "The ContextService is already initialized, try calling getInstance() instead.");
        }
        _instance = this;
    }

    /**
     * Returns the existing instance of the ContextService.
     *
     * @return The existing instance of the ContextService.
     */
    public static ContextService getInstance() {
        if (_instance == null) {
            try {
                _instance = new ContextService();
            } catch (NrgServiceException e) {
                // Do nothing. This should never happen, since the exception is only thrown when the service is already initialized.
            }
        }
        return _instance;
    }

    /**
     * Provides the setter for the application context.
     *
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        _context = context;
    }

    /**
     * Indicates whether the context service instance has an application context.
     *
     * @return <b>true</b> if the service object has an application context.
     */
    public boolean hasApplicationContext() {
        return _context != null;
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
     * @param event The application event. This is checked to see if it's a <b>ContextRefreshedEvent</b> and, if so, the
     *              application context will be refreshed.
     */
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        ContextService.getInstance().setApplicationContext(event.getApplicationContext());
    }

    /**
     * Gets a bean of the indicated type.
     *
     * @param <T>  The type of the bean to be retrieved.
     * @param type The class of the bean to be retrieved.
     * @return An object of the type.
     */
    public <T> T getBean(final Class<T> type) {
        return _context.getBean(type);
    }

    /**
     * Gets the bean with the indicated name and type.
     *
     * @param <T>  The type of the bean to be retrieved.
     * @param name The name of the bean to be retrieved.
     * @param type The class of the bean to be retrieved.
     * @return An object of the type.
     */
    public <T> T getBean(final String name, final Class<T> type) {
        return _context.getBean(name, type);
    }

    /**
     * Gets all beans with the indicated type.
     *
     * @param type The class of the bean to be retrieved.
     * @param <T>  The parameterized class of the bean to be retrieved.
     * @return An object of the type.
     */
    @SuppressWarnings("unused")
    public <T> Map<String, T> getBeansOfType(final Class<T> type) {
        return _context.getBeansOfType(type);
    }

    public URI getAppRelativeLocation(final String... relativePaths) {
        try {
            return _servletContext.getResource(joinPaths(relativePaths)).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            return null;
        }
    }

    public InputStream getAppRelativeStream(final String... relativePaths) {
        return _servletContext.getResourceAsStream(joinPaths(relativePaths));
    }

    public Set<String> getAppRelativeLocationContents(final String... relativePaths) {
        return getAppRelativeLocationContents(null, relativePaths);
    }

    public Set<String> getAppRelativeLocationContents(final FilenameFilter filter, final String... relativePaths) {
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

    public Set<String> getAppRelativeLocationChildren(final String... relativePaths) {
        return getAppRelativeLocationChildren(null, relativePaths);
    }

    public Set<String> getAppRelativeLocationChildren(final FilenameFilter filter, final String... relativePaths) {
        final Set<String> found    = getAppRelativeLocationContents(relativePaths);
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

    @SuppressWarnings("unused")
    public URI getConfigurationLocation(final String configuration) {
        return getAppRelativeLocation("WEB-INF", "conf", configuration);
    }

    @SuppressWarnings("unused")
    public InputStream getConfigurationStream(final String configuration) {
        return getAppRelativeStream("WEB-INF", "conf", configuration);
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

    private static ContextService     _instance;
    private        ApplicationContext _context;
    private        ServletContext     _servletContext;
}
