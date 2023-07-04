package com.solicitud.solicitud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ExpiredAuthorizationCodeException extends RuntimeException {
	String message;
	public String redirectUrl;
	
	public ExpiredAuthorizationCodeException() {
		super("El código de autorización ha caducado.");
		this.redirectUrl = "https://login.microsoftonline.com/organizations/oauth2/v2.0/authorize?client_id=05ffc59f-06b9-4d55-9521-dc43d97b5365&response_type=code&redirect_uri=https%3A%2F%2Fcsjinfo.ramajudicial.gov.co%2F&scope=offline_access%20user.read%20mail.read&response_mode=query";
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
}
