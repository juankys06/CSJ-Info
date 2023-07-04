package com.solicitud.solicitud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
public class MSQueryException extends RuntimeException {
	public MSQueryException() {
		super("No se pudo establecer conexión con la API de Microsoft Graph. Por favor revise su conexión o intente de nuevo más tarde.");
	}
}
