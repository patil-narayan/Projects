package com.infinite.omniapigateway.securityconfig.exceptions;

public class Oauth2ClaimsValidationFailedException extends Exception {

private static final long serialVersionUID = 1L;
private String message;
	
	public Oauth2ClaimsValidationFailedException(String message) {
		super(message);
		this.message = message;
	}
}
