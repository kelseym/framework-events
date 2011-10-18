package org.nrg.framework.orm.hibernate.exceptions;

import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;

public class InvalidDirectParameterizedClassUsageException extends NrgServiceRuntimeException {
    /**
     * Default constructor. This sets the {@link #getServiceError() service error}
     * property to {@link NrgServiceError#InvalidDirectParameterizedClassUsage}.
     */
    public InvalidDirectParameterizedClassUsageException() {
        super();
        setServiceError(NrgServiceError.InvalidDirectParameterizedClassUsage);
    }

    /**
     * Message constructor. This sets the {@link #getServiceError() service error}
     * property to {@link NrgServiceError#InvalidDirectParameterizedClassUsage}.
     */
    public InvalidDirectParameterizedClassUsageException(String message) {
        super(message);
        setServiceError(NrgServiceError.InvalidDirectParameterizedClassUsage);
    }

    /**
     * Wrapper constructor. This sets the {@link #getServiceError() service error}
     * property to {@link NrgServiceError#InvalidDirectParameterizedClassUsage}.
     */
    public InvalidDirectParameterizedClassUsageException(Throwable cause) {
        super(cause);
        setServiceError(NrgServiceError.InvalidDirectParameterizedClassUsage);
    }

    /**
     * Message and wrapper constructor. This sets the {@link #getServiceError() service error}
     * property to {@link NrgServiceError#InvalidDirectParameterizedClassUsage}.
     */
    public InvalidDirectParameterizedClassUsageException(String message, Throwable cause) {
        super(message, cause);
        setServiceError(NrgServiceError.InvalidDirectParameterizedClassUsage);
    }

    private static final long serialVersionUID = -5369229024951738808L;
}
