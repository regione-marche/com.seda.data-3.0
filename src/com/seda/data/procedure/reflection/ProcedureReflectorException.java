/**
 * 
 */
package com.seda.data.procedure.reflection;

/**
 * @author f.ricci
 *
 */
public class ProcedureReflectorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public ProcedureReflectorException(String message) {
		super(message);
	}

	public ProcedureReflectorException(String message, Throwable cause) {
		super(message, cause);
	}

}
