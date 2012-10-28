/**
 * ApplicationParameterException
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/16/12 by rherri01
 */
package org.nrg.framework.pinto;

import java.net.URISyntaxException;

public class PintoException extends Exception {
    public PintoException(final PintoExceptionType type) {
        super();
        _type = type;
        _parameter = null;
    }

    public PintoException(final PintoExceptionType type, final String message) {
        super(message);
        _type = type;
        _parameter = null;
    }

    public PintoException(final PintoExceptionType type, final String parameter, final String message) {
        super(message);
        _type = type;
        _parameter = parameter;
    }

    public PintoException(final PintoExceptionType type, final String parameter, final String message, final Exception exception) {
        super(message, exception);
        _type = type;
        _parameter = parameter;
    }

    public PintoException(final PintoExceptionType type, final String message, final URISyntaxException exception) {
        super(message, exception);
        _type = type;
        _parameter = null;
    }

    public String getParameter() {
        return _parameter;
    }

    public PintoExceptionType getType() {
        return _type;
    }

    @Override
    public String toString() {
        return "Error on parameter " + _parameter + " (" + _type + ")\\n" + getMessage();
    }

    private final PintoExceptionType _type;
    private final String _parameter;

}
