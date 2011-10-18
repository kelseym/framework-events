/**
 * NrgException
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.exceptions;

/**
 * This is the base class for run-time exceptions in the NRG framework.
 * @author rherri01
 */
public class NrgRuntimeException extends RuntimeException {

    /**
	 * Default constructor.
	 */
	public NrgRuntimeException() {
		super();
	}

	/**
	 * Default message constructor.
	 */
	public NrgRuntimeException(String message) {
		super(message);
	}

	/**
	 * Default wrapper constructor.
	 */
	public NrgRuntimeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Default message and wrapper constructor.
	 */
	public NrgRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

    private static final long serialVersionUID = -836449451507257629L;
}