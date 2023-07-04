package com.solicitud.solicitud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class ExpiredRefreshTokenException extends RuntimeException {
	String message;
	
	public ExpiredRefreshTokenException() {
		super("El token de renovación ha caducado.");
	}
}
