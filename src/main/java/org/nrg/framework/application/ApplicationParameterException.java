/**
 * ApplicationParameterException
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/16/12 by rherri01
 */
package org.nrg.framework.application;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ApplicationParameterException extends Exception {
    public ApplicationParameterException(final Type type) {
        super();
        _type = type;
        _parameter = null;
    }

    public ApplicationParameterException(final Type type, final String message) {
        super(message);
        _type = type;
        _parameter = null;
    }

    public ApplicationParameterException(final Type type, final String parameter, final String message) {
        super(message);
        _type = type;
        _parameter = parameter;
    }

    public ApplicationParameterException(final Type type, final String parameter, final String message, final Exception exception) {
        super(message, exception);
        _type = type;
        _parameter = parameter;
    }

    public ApplicationParameterException(final Type type, final String message, final URISyntaxException exception) {
        super(message, exception);
        _type = type;
        _parameter = null;
    }

    public String getParameter() {
        return _parameter;
    }

    public Type getType() {
        return _type;
    }

    @Override
    public String toString() {
        return "Error on parameter " + _parameter + " (" + _type + ")\\n" + getMessage();
    }

    private final Type _type;
    private final String _parameter;

    public enum Type {
        HelpRequested(0),
        UnknownParameter(1),
        SyntaxFormat(2),
        UnsupportedFeature(3),
        DuplicateParameter(4),
        Configuration(5),
        UnknownParameterTypes(6);

        Type(int code) {
            _code = code;
        }

        public int getCode() {
            return _code;
        }

        public static Type code(String code) {
            return code(Integer.parseInt(code));
        }

        public static Type code(int code) {
            if (_codes.isEmpty()) {
                synchronized (Type.class) {
                    for (Type typeCode : values()) {
                        _codes.put(typeCode.getCode(), typeCode);
                    }
                }
            }
            return _codes.get(code);
        }

        @Override
        public String toString() {
            return this.name();
        }
        private static final Map<Integer, Type> _codes = new HashMap<Integer, Type>();
        private final int _code;
    }
}
