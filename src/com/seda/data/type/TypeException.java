package com.seda.data.type;

public class TypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TypeException(String message) {
		super(message);
	}

	public TypeException(String message, Throwable cause) {
		super(message, cause);
	}

}
