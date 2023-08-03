/*
 * @Oauth2ClaimsValidationFailedException.java@
 * Created on 10Mar2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.exceptions;

public class Oauth2ClaimsValidationFailedException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public Oauth2ClaimsValidationFailedException(String message) {
		super(message);
		this.message = message;
	}

}