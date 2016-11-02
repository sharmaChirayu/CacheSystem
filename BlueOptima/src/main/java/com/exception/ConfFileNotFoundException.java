package com.exception;

/**
 * The Class ConfFileNotFoundException.
 */
public class ConfFileNotFoundException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2701532010143274049L;

	/**
	 * Instantiates a new conf file not found exception.
	 *
	 * @param message the message
	 */
	public ConfFileNotFoundException(String message) {
		super(message);
	}
}
