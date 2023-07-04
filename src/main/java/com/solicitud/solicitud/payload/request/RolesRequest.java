package com.solicitud.solicitud.payload.request;

import java.util.Set;

public class RolesRequest {
	private String descripcion;
	private Set<Long> permisos;
	private Set<Long> formularios;
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public Set<Long> getPermisos(){
		return permisos;
	}

	public Set<Long> getFormularios() {
		return formularios;
	}

}
