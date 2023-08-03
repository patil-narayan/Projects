package com.core.zyter.exceptions;

import lombok.AllArgsConstructor;


public class Oauth2ClaimsValidationFailedException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public Oauth2ClaimsValidationFailedException(String message) {
		super(message);
		this.message = message;
	}

}
