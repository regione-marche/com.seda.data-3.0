/**
 * 
 */
package com.seda.data.procedure.parameter;

/**
 * @author f.ricci
 *
 */
public class ParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ParameterException(String message) {
		super(message);
	}

	public ParameterException(String message, Throwable cause) {
		super(message, cause);
	}

}
