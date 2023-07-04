package com.solicitud.solicitud.payload.request;

import java.util.Set;

public class UserRoleRequest {
	private Set<Long> roles;
	
	public Set<Long> getRoles(){
		return roles;
	}
}
