package com.solicitud.solicitud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class ExpiredTokenException extends RuntimeException {
	String message;
	
	public ExpiredTokenException() {
		super("El token de acceso ha caducado.");
	}
}
