/**
 * 
 */
package com.seda.data.procedure;

/**
 * @author f.ricci
 *
 */
public class RuntimeSystemException extends RuntimeException {

	private static final long serialVersionUID = 6938764463282385120L;

	public RuntimeSystemException(String message) {
		super(message);
	}

	public RuntimeSystemException(String message, Throwable cause) {
		super(message, cause);
	}

}
