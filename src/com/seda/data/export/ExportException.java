/**
 * 
 */
package com.seda.data.export;

/**
 * @author f.ricci
 *
 */
public class ExportException extends RuntimeException {

	public ExportException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExportException(String message) {
		super(message);
	}

}
