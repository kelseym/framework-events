/**
 * ContextService
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Sep 6, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.services;

import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * This provides a static initializer and means of access to the Spring context
 * for classes that may not otherwise have access to them, e.g. from Turbine or
 * restlet code.
 *
 * @author Rick Herrick <rick.herrick@wustl.edu>
 */
@Service
public class ContextService implements NrgService, ApplicationContextAware, ApplicationListener {
    public static String SERVICE_NAME = "ContextService";

    /**
     * Public constructor for use by Spring to initialize the ContextService within
     * the application context. Other classes or applications should not call this
     * constructor and should only call the
     *
     * @throws NrgServiceException Thrown when the ContextService is already initialized.
     */
    public ContextService() throws NrgServiceException {
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
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        _context = context;
    }

    /**
     * Handles updates to the application context.
     *
     * @param event    The application event. This is checked to see if it's a <b>ContextRefreshedEvent</b>
     *                 and, if so, the application context will be refreshed.
     */
    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ContextService.getInstance().setApplicationContext(((ContextRefreshedEvent) event).getApplicationContext());
        }
    }

    /**
     * Gets a bean of the indicated type.
     *
     * @param <T>  The type of the bean to be retrieved.
     * @param type The class of the bean to be retrieved.
     * @return An object of the type.
     */
    public <T> T getBean(Class<T> type) {
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
    public <T> T getBean(String name, Class<T> type) {
        return _context.getBean(name, type);
    }

    private static ContextService _instance;
    private ApplicationContext _context;
}
