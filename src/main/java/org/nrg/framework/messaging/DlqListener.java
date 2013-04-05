/*
 * DlqListener
 * Copyright (c) 2013. Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 */

package org.nrg.framework.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * DlqListener
 *
 * @author rherri01
 * @since 4/4/13
 */

/**
 * I couldn't get the ActiveMQ configuration to work for multiple DLQs. So this one will triage the requests and forward
 * appropriately.
 *
 * @author ehaas01
 *
 */
public class DlqListener {

    public void onReceiveDeadLetter(final Object object) throws Exception {
        if (object == null) {
            final String error = "Received null dead letter. That's not OK.";
            log.error(error);
            throw new RuntimeException(error);
        }
        if (_mapping.size() == 0) {
            final String error = "Received dead letter of type: " + object.getClass() + ", however no listeners are currently configured.";
            log.error(error);
            throw new RuntimeException(error);
        }

        boolean found = false;
        for (Map.Entry<Class<?>, ListenerMethod> mapping : _mapping.entrySet()) {
            // Check to see if the dead-letter request is of the same class or a subclass of the request type.
            if (!mapping.getKey().isAssignableFrom(object.getClass())) {
                continue;
            }
            ListenerMethod method = mapping.getValue();
            method.callListenerMethod(object);
            found = true;
            break;
        }
        if (!found) {
            final String error = "Received dead letter of unknown type: " + object.getClass();
            log.error(error);
            throw new RuntimeException(error);
        }
    }

    public void setMessageListenerMapping(Map<String, String> mapping) {
        _mapping.clear();
        String requestTypeName = null;
        String listenerTypeName = null;
        try {
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                requestTypeName = null;
                listenerTypeName = null;
                Class requestType = Class.forName(requestTypeName = entry.getKey());
                ListenerMethod method = new ListenerMethod(entry.getValue());
                _mapping.put(requestType, method);
            }
        } catch (ClassNotFoundException e) {
            String error;
            if (requestTypeName == null) {
                error = "Something weird happened and nothing is initialized.";
            } else if (listenerTypeName == null) {
                error = "The request type " + requestTypeName + " is not a valid class.";
            } else {
                error = "The listener type " + listenerTypeName + " for request type " + requestTypeName + " is not a valid class.";
            }
            log.error(error);
            throw new RuntimeException(error);
        }
    }

    private class ListenerMethod {
        public ListenerMethod(String classAndMethod) throws ClassNotFoundException {
            int methodLocation = (_classAndMethod = classAndMethod).lastIndexOf(".");
            if (methodLocation < 0) {
                throw new RuntimeException("Poorly formed listener specifier " + classAndMethod + ". Must be of the form package.of.ListenerClass.ListenerMethod.");
            }
            final String className = _classAndMethod.substring(0, methodLocation);
            final String methodName = _classAndMethod.substring(methodLocation + 1);
            _listenerClass = Class.forName(className);
            Method method = null;
            Class<?>[] parameterTypes = null;
            for (Method candidate : _listenerClass.getMethods()) {
                if (candidate.getName().equals(methodName)) {
                    method = candidate;
                    parameterTypes = method.getParameterTypes();
                    break;
                }
            }
            if (method == null) {
                throw new RuntimeException("Poorly formed listener specifier " + classAndMethod + ". Must be of the form package.of.ListenerClass.ListenerMethod.");
            }
            _listenerMethod = methodName;
            _listenerMethodParameterTypes = parameterTypes;
        }

        public void callListenerMethod(Object... objects) {
            try {
                Object object = _listenerClass.newInstance();
                Method method = _listenerClass.getMethod(_listenerMethod, _listenerMethodParameterTypes);
                method.invoke(object, objects);
            } catch (NoSuchMethodException exception) {
                throw new RuntimeException("Error retrieving verified method: " + _classAndMethod, exception);
            } catch (InstantiationException exception) {
                throw new RuntimeException("Error creating verified class: " + _listenerClass.getName(), exception);
            } catch (IllegalAccessException exception) {
                throw new RuntimeException("Illegal access to verified method: " + _classAndMethod, exception);
            } catch (InvocationTargetException exception) {
                throw new RuntimeException("Error invoking verified method: " + _classAndMethod, exception);
            }
        }

        private final String _classAndMethod;
        private final Class<?> _listenerClass;
        private final String _listenerMethod;
        private final Class<?>[] _listenerMethodParameterTypes;
    }

    private final static Logger log = LoggerFactory.getLogger(DlqListener.class);
    private final Map<Class<?>, ListenerMethod> _mapping = new HashMap<Class<?>, ListenerMethod>();
}
