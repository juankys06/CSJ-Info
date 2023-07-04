package com.solicitud.solicitud.payload.request;

import java.util.Optional;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.Email;

public class SolicitudesRequest {
    
	@Column(length = 75)
	@Email
    private String correo_solicitante;
	
    @Column
	@Lob
    private String correo_asignacion;
    
    @Column
    @Lob
    private String telefono_asignacion;
	
	private String asunto;
	
	@Lob
	@Column
	private String contenido;
	
	private Long formulario;
	
    private Long entidad;
    
	private String edm;

	
	private Set<String> url;

	public String getCorreo_solicitante() {
		return correo_solicitante;
	}
	
    public String getCorreo_asignacion() {
        return correo_asignacion;
    }
    
    public String getTelefono_asignacion() {
        return telefono_asignacion;
    }

	public String getContenido() {
		return contenido;
	}
	
	public String getAsunto() {
		return asunto;
	}

	public Set<String> getUrl() {
		return url;
	}
	
	public Long getFormulario() {
		return formulario;
	}
	
	public Long getEntidad() {
		return entidad;
	}

	public String getEdm() {
		return edm;
	}

	public void setEdm(String edm) {
		this.edm = edm;
	}

	
	
}
