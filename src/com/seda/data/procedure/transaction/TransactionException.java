/**
 * 
 */
package com.seda.data.procedure.transaction;

/**
 * @author f.ricci
 *
 */
public class TransactionException extends RuntimeException {

	private static final long serialVersionUID = 5265775734370557342L;

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransactionException(String message) {
		super(message);
	}

}
