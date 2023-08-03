package com.core.zyter.email.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.core.zyter.email.util.Constants;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@ControllerAdvice
@Slf4j
public class OmniChannelEmailExceptionHandler {
	
	@ExceptionHandler(NullPointerException.class)
    public @ResponseBody ErrorResponse handleException(Exception ex, HttpServletResponse response) {
		log.error("" ,ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        var errorResponse = new ErrorResponse(Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler
    public @ResponseBody ErrorResponse handleMethodNotAllowedException(HttpRequestMethodNotSupportedException ex, HttpServletResponse response) {
    	log.error("" ,ex);
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        var errorResponse = new ErrorResponse(Constants.FAILURE, HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(value = Exception.class)
    public @ResponseBody ErrorResponse handleAllException(Exception ex, HttpServletResponse response) {
    	log.error("" ,ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        var errorResponse = new ErrorResponse(Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(value = OmnichannelException.class)
    public @ResponseBody ErrorResponse handleOmnichannelException(OmnichannelException ex, HttpServletResponse response) {
    	log.error("" ,ex);
        response.setStatus(ex.getHttpStatus().value());
        var errorResponse = new ErrorResponse(ex.getStatus(), ex.getHttpStatus().value(), ex.getMessage());
        return errorResponse;
    }
}
