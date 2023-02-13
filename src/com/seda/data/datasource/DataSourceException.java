/**
 * 
 */
package com.seda.data.datasource;

/**
 * @author f.ricci
 *
 */
public class DataSourceException extends RuntimeException {

	private static final long serialVersionUID = -6399016719319566243L;

	public DataSourceException(String message) {
		super(message);
	}

	public DataSourceException(Throwable cause) {
		super(cause);
	}	
	
	public DataSourceException(String message, Throwable cause) {
		super(message, cause);
	}

}
