package com.solicitud.solicitud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class EmailExistsException extends RuntimeException {
	String message;
	
	public EmailExistsException() {
		super("El Email ya se encuentra asignado a un formulario.");
	}
}
