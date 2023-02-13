/**
 * 
 */
package com.seda.data.dao;

/**
 * @author f.ricci
 *
 */
public class DAORuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DAORuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DAORuntimeException(String message) {
		super(message);
	}

}
