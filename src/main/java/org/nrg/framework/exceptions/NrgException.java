/**
 * NrgException
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 23, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.exceptions;

/**
 * This is the base class for exceptions in the NRG framework.
 * @author rherri01
 */
public class NrgException extends Exception {

    /**
	 * Default constructor.
	 */
	public NrgException() {
		super();
	}

	/**
	 * Default message constructor.
	 */
	public NrgException(String message) {
		super(message);
	}

	/**
	 * Default wrapper constructor.
	 */
	public NrgException(Throwable cause) {
		super(cause);
	}

	/**
	 * Default message and wrapper constructor.
	 */
	public NrgException(String message, Throwable cause) {
		super(message, cause);
	}

    private static final long serialVersionUID = -836449451507257629L;
}