/*
 * framework: org.nrg.framework.exceptions.NotParameterizedTypeException
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Type;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotParameterizedTypeException extends Throwable {
    public NotParameterizedTypeException(final Type type, final String message) {
        super(message);
        _type = type;
    }

    public Type getNonParameterizedClass() {
        return _type;
    }

    private final Type _type;
}
