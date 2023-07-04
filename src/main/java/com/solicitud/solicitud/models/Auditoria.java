package com.solicitud.solicitud.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;


import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import org.springframework.data.annotation.LastModifiedBy;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class) 
@Table(name = "csjinfo_auditoria")
public class Auditoria {
	@Id
	public Integer id;
	
	public Integer rev;
	
	@Column
	public Long codigo;
	@Column
	public Long id_roles;
	@Column
	public Long id_permiso;
	@Column
	public Long id_encuesta;
	@Column
	public Long id_formulario;
	@Column
	public Long id_solicitud;
	@Column
	public Long id_user;
	
	@Column(length = 200)
	public String nombre;
	@Column
    public String descripcion_roles;
	@Column
    public String descripcion_permiso;
	@Column
    public Boolean activo_encuesta;
	@Column
    public Boolean activo_formulario;

	@Column
    public String titulo;
	@Column
    public String url_encuesta;

	@Column
    public String contenido_formulario;
	@Column
    public String descripcion_formulario;
	@Column
    public String email;
	public String host;
	public Integer port;	
	public String imap_host;
	public Integer imap_port;
	public String url_formulario;
	
	@Column
	public String tipo;
	
	public String correo_solicitante;
	

    public String correo_asignacion;
    
    public String telefono_asignacion;
    
    public String telefono;
    
	public String telefono_entidad;
	
	public String asunto;
	
	public String contenido_solicitud;
	
    @LastModifiedBy
    @Column(name = "usuario_modified")
    public String usuarioModified;
    
    @Column
    public LocalDateTime fecha;
	
	@CreationTimestamp
    public LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    public LocalDateTime updateDateTime;
    
    @Column
    public short revtype;
    
   
	@Column
	public Long status_id;
    
	@Column
	public Long entidad_id;
	@Column
	public String edm;
    
	@Column
	public Long entidad_codigo;

    public Integer re;
    
    @Column
    public Long parent_id;

	
	public Auditoria() {
		// TODO Auto-generated constructor stub
	}

	public Auditoria(int id, Integer rev, Long codigo, Long id_roles, Long id_permiso, Long id_encuesta,
			Long id_formulario, Long id_solicitud, Long id_user, String nombre, String descripcion_roles,
			String descripcion_permiso, Boolean activo_encuesta, Boolean activo_formulario, String titulo,
			String url_encuesta, String contenido_formulario, String descripcion_formulario, String email, String host,
			Integer port, String imap_host, Integer imap_port, String url_formulario, String correo_solicitante,
			String correo_asignacion, String telefono_asignacion, String asunto, String contenido_solicitud, String usuarioModified,
			LocalDateTime fecha, LocalDateTime create_date_time, LocalDateTime updateDateTime, short revtype, Long status_id,
			Long entidad_id, Integer re, Long parent_id) {
		super();
		this.id = id;
		this.rev = rev;
		this.codigo = codigo;
		this.id_roles = id_roles;
		this.id_permiso = id_permiso;
		this.id_encuesta = id_encuesta;
		this.id_formulario = id_formulario;
		this.id_solicitud = id_solicitud;
		this.id_user = id_user;
		this.nombre = nombre;
		this.descripcion_roles = descripcion_roles;
		this.descripcion_permiso = descripcion_permiso;
		this.activo_encuesta = activo_encuesta;
		this.activo_formulario = activo_formulario;
		this.titulo = titulo;
		this.url_encuesta = url_encuesta;
		this.contenido_formulario = contenido_formulario;
		this.descripcion_formulario = descripcion_formulario;
		this.email = email;
		this.host = host;
		this.port = port;
		this.imap_host = imap_host;
		this.imap_port = imap_port;
		this.url_formulario = url_formulario;
		this.correo_solicitante = correo_solicitante;
		this.correo_asignacion = correo_asignacion;
		this.telefono_asignacion = telefono_asignacion;
		this.asunto = asunto;
		this.contenido_solicitud = contenido_solicitud;
        this.usuarioModified = usuarioModified;
        this.fecha = fecha;
		this.create_date_time = create_date_time;
		this.updateDateTime = updateDateTime;
		this.revtype = revtype;
		this.status_id = status_id;
		this.entidad_id = entidad_id;
        this.re = re;
        this.parent_id = parent_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getRev() {
		return rev;
	}


	public void setRev(Integer rev) {
		this.rev = rev;
	}


	public Long getCodigo() {
		return codigo;
	}


	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}


	public Long getId_roles() {
		return id_roles;
	}


	public void setId_roles(Long id_roles) {
		this.id_roles = id_roles;
	}


	public Long getId_permiso() {
		return id_permiso;
	}


	public void setId_permiso(Long id_permiso) {
		this.id_permiso = id_permiso;
	}


	public Long getId_encuesta() {
		return id_encuesta;
	}


	public void setId_encuesta(Long id_encuesta) {
		this.id_encuesta = id_encuesta;
	}


	public Long getId_formulario() {
		return id_formulario;
	}


	public void setId_formulario(Long id_formulario) {
		this.id_formulario = id_formulario;
	}


	public Long getId_solicitud() {
		return id_solicitud;
	}


	public void setId_solicitud(Long id_solicitud) {
		this.id_solicitud = id_solicitud;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getDescripcion_roles() {
		return descripcion_roles;
	}


	public void setDescripcion_roles(String descripcion_roles) {
		this.descripcion_roles = descripcion_roles;
	}


	public String getDescripcion_permiso() {
		return descripcion_permiso;
	}


	public void setDescripcion_permiso(String descripcion_permiso) {
		this.descripcion_permiso = descripcion_permiso;
	}


	public Boolean getActivo_encuesta() {
		return activo_encuesta;
	}


	public void setActivo_encuesta(Boolean activo_encuesta) {
		this.activo_encuesta = activo_encuesta;
	}


	public Boolean getActivo_formulario() {
		return activo_formulario;
	}


	public void setActivo_formulario(Boolean activo_formulario) {
		this.activo_formulario = activo_formulario;
	}


	public String getTitulo() {
		return titulo;
	}


	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	public String getUrl_encuesta() {
		return url_encuesta;
	}


	public void setUrl_encuesta(String url_encuesta) {
		this.url_encuesta = url_encuesta;
	}


	public String getContenido_formulario() {
		return contenido_formulario;
	}


	public void setContenido_formulario(String contenido_formulario) {
		this.contenido_formulario = contenido_formulario;
	}


	public String getDescripcion_formulario() {
		return descripcion_formulario;
	}


	public void setDescripcion_formulario(String descripcion_formulario) {
		this.descripcion_formulario = descripcion_formulario;
	}


	public String getEmail() {
		return email;
	}


    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelefonoEntidad() {
        return telefono_entidad;
    }

    public void setTelefonoEntidad(String telefono_entidad) {
        this.telefono_entidad = telefono_entidad;
    }


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public Integer getPort() {
		return port;
	}


	public void setPort(Integer port) {
		this.port = port;
	}


	public String getImap_host() {
		return imap_host;
	}


	public void setImap_host(String imap_host) {
		this.imap_host = imap_host;
	}


	public Integer getImap_port() {
		return imap_port;
	}


	public void setImap_port(Integer imap_port) {
		this.imap_port = imap_port;
	}


	public String getUrl_formulario() {
		return url_formulario;
	}


	public void setUrl_formulario(String url_formulario) {
		this.url_formulario = url_formulario;
	}


	public String getCorreo_solicitante() {
		return correo_solicitante;
	}


	public void setCorreo_solicitante(String correo_solicitante) {
		this.correo_solicitante = correo_solicitante;
	}


	public String getCorreo_asignacion() {
		return correo_asignacion;
	}


    public void setCorreo_asignacion(String correo_asignacion) {
        this.correo_asignacion = correo_asignacion;
    }
    
    public String getTelefono_asignacion() {
        return telefono_asignacion;
    }

    public void setTelefono_asignacion(String telefono_asignacion) {
        this.telefono_asignacion = telefono_asignacion;
    }


	public String getAsunto() {
		return asunto;
	}


	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}


	public String getContenido_solicitud() {
		return contenido_solicitud;
	}


	public void setContenido_solicitud(String contenido_solicitud) {
		this.contenido_solicitud = contenido_solicitud;
	}


	public String getUsuarioModified() {
		return usuarioModified;
	}


    public void setUsuarioModified(String usuarioModified) {
        this.usuarioModified = usuarioModified;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }


	public LocalDateTime getCreate_date_time() {
		return create_date_time;
	}


	public void setCreate_date_time(LocalDateTime create_date_time) {
		this.create_date_time = create_date_time;
	}


	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}


	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
	}


	public short getRevtype() {
		return revtype;
	}


	public void setRevtype(short revtype) {
		this.revtype = revtype;
	}


	public Long getStatus_id() {
		return status_id;
	}


	public void setStatus_id(Long status_id) {
		this.status_id = status_id;
	}


	public Long getEntidad_id() {
		return entidad_id;
	}


	public void setEntidad_id(Long entidad_id) {
		this.entidad_id = entidad_id;
	}


	public Integer getRe() {
		return re;
	}


	public void setRe(Integer re) {
		this.re = re;
	}




	public Long getId_user() {
		return id_user;
	}




	public void setId_user(Long id_user) {
		this.id_user = id_user;
	}

	public String getEdm() {
		return edm;
	}

    public void setEdm(String edm) {
        this.edm = edm;
    }
    
    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }
	
}
