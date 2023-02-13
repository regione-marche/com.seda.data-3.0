/**
 * 
 */
package com.seda.data.dao;

/**
 * DAOException is thrown by a DAO component when there is 
 * some irrecoverable error (like SQLException)
 * @author Seda Lab
 *
 */
public class DAOSysException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public DAOSysException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DAOSysException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DAOSysException(String message, Throwable cause) {
		super(message, cause);
	}

}
