/*
 * @OmniChannelSMSExceptionHandler.java@
 * Created on 08Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.exceptions;

import com.core.zyter.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class OmniChannelSMSExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    public @ResponseBody ResponseEntity<ErrorResponse> handleException(Exception ex) {
    	log.error("" ,ex);
        var response = new ErrorResponse(Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
    }

    @ExceptionHandler
    public @ResponseBody ResponseEntity<ErrorResponse> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException ex) {
    	log.error("" ,ex);
        var response = new ErrorResponse(Constants.FAILURE, HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public @ResponseBody ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
    	log.error("" ,ex);
        var response = new ErrorResponse(Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = OmnichannelException.class)
    public @ResponseBody ResponseEntity<ErrorResponse> handleOmnichannelException(OmnichannelException ex) {
    	log.error("" ,ex);
        var response = new ErrorResponse(ex.getStatus(), ex.getHttpStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

}
