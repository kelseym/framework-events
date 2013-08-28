/**
 * NrgServiceException
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.exceptions;

import org.nrg.framework.services.NrgService;

public class NrgServiceRuntimeException extends NrgRuntimeException {

	/**
	 * Default constructor. This sets the {@link #getServiceError() service error}
	 * property to {@link NrgServiceError#Default}.
	 */
	public NrgServiceRuntimeException() {
		super();
		setServiceError(NrgServiceError.Default);
	}

	/**
	 * Message constructor. This sets the {@link #getServiceError() service error}
	 * property to {@link NrgServiceError#Default}.
	 */
	public NrgServiceRuntimeException(String message) {
		super(message);
		setServiceError(NrgServiceError.Default);
	}

	/**
	 * Wrapper constructor. This sets the {@link #getServiceError() service error}
	 * property to {@link NrgServiceError#Default}.
	 */
	public NrgServiceRuntimeException(Throwable cause) {
		super(cause);
		setServiceError(NrgServiceError.Default);
	}

	/**
	 * Message and wrapper constructor. This sets the {@link #getServiceError() service error}
	 * property to {@link NrgServiceError#Default}.
	 */
	public NrgServiceRuntimeException(String message, Throwable cause) {
		super(message, cause);
		setServiceError(NrgServiceError.Default);
	}

	/**
	 * Default error code constructor. This sets the {@link #getServiceError() service error}
	 * property to the submitted {@link NrgServiceError} value.
	 */
	public NrgServiceRuntimeException(NrgServiceError error) {
		super();
		setServiceError(error);
	}
	
	/**
	 * Error code message constructor. This sets the {@link #getServiceError() service error}
	 * property to the submitted {@link NrgServiceError} value.
	 */
	public NrgServiceRuntimeException(NrgServiceError error, String message) {
		super(message);
		setServiceError(error);
	}
	
	/**
	 * Error code wrapper constructor. This sets the {@link #getServiceError() service error}
	 * property to the submitted {@link NrgServiceError} value.
	 */
	public NrgServiceRuntimeException(NrgServiceError error, Throwable cause) {
		super(cause);
		setServiceError(error);
	}
	
	/**
	 * Error code and message wrapper constructor. This sets the {@link #getServiceError() service error}
	 * property to the submitted {@link NrgServiceError} value.
	 */
	public NrgServiceRuntimeException(NrgServiceError error, String message, Throwable cause) {
		super(message, cause);
		setServiceError(error);
	}
	
	/**
	 * Gets the {@link NrgServiceError service error code} for the exception.
	 * @return The service error code.
	 */
	public NrgServiceError getServiceError() {
		return _error;
	}

	/**
	 * Sets the {@link NrgServiceError service error code} for the exception.
	 * @param error The service error code to set.
	 */
	public void setServiceError(NrgServiceError error) {
		_error = error;
	}

	/**
	 * Overrides the base {@link Exception#getMessage()} method to add the {@link #getServiceError() service error code}
	 * to the returned message.
	 */
	@Override
	public String getMessage() {
		return "Error [" + _error + "]: " + super.getMessage();
	}

	/**
	 * Overrides the base {@link Exception#getLocalizedMessage()} method to add the {@link #getServiceError() service error code}
	 * to the returned message.
	 */
	@Override
	public String getLocalizedMessage() {
		return "Error [" + _error + "]: " + super.getLocalizedMessage();
	}
	
	private static final long serialVersionUID = -2682559327708154539L;
	private NrgServiceError _error;
}
