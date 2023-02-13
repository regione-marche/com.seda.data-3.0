/**
 * 
 */
package com.seda.data.procedure.executor;

/**
 * @author f.ricci
 *
 */
public class ExecutorException extends RuntimeException {

	private static final long serialVersionUID = 2510375238172545756L;

	public ExecutorException(String message) {
		super(message);
	}

	public ExecutorException(String message, Throwable cause) {
		super(message, cause);
	}

	
}
