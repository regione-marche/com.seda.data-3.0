package com.seda.data.helper;

/**
 * User defined exception for data helper
 * @author SEDA Lab
 */
public class HelperException extends Exception {
	/**
	 * Compiler creates it.
	 */
	private static final long serialVersionUID = 4287317993340759349L;
	/**
	 * Constructor of HelperException.
	 * @param message - The message that we pass in case of this exception.
	 * @param cause - The {@link Throwable} class is the superclass of all errors and exceptions in the Java language.
	 */
	public HelperException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * Constructor of HelperException.
	 * @param message - The message that we pass in case of this exception.
	 */
	public HelperException(String message) {
		super(message);
	}
	/**
	 * Constructor of HelperException.
	 * @param cause - The {@link Throwable} class is the superclass of all errors and exceptions in the Java language.
	 */
	public HelperException(Throwable cause) {
		super(cause);
	}

}
