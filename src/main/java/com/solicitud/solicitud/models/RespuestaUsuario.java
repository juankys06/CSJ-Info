package com.solicitud.solicitud.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity 
@Table(name = "csjinfo_respuesta_usuario")
public class RespuestaUsuario{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(length = 200)
	private String titulo;
	
	@NotBlank
	@Lob
	private String contenido_respuesta;
	
	@LastModifiedBy
    @Column(name = "usuario_modified")
	private String usuarioModified;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
    
    @NotNull
    private Long id_user;

	public RespuestaUsuario(Long id, @NotBlank String titulo, @NotBlank String contenido_respuesta, String usuarioModified,
			LocalDateTime create_date_time, LocalDateTime updateDateTime, @NotNull Long id_user) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.contenido_respuesta = contenido_respuesta;
		this.usuarioModified = usuarioModified;
		this.create_date_time = create_date_time;
		this.updateDateTime = updateDateTime;
		this.id_user = id_user;
	}
	
	public RespuestaUsuario() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getContenido_respuesta() {
		return contenido_respuesta;
	}

	public void setContenido_respuesta(String contenido_respuesta) {
		this.contenido_respuesta = contenido_respuesta;
	}

	public String getUsuarioModified() {
		return usuarioModified;
	}

	public void setUsuarioModified(String usuarioModified) {
		this.usuarioModified = usuarioModified;
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

	public Long getId_user() {
		return id_user;
	}

	public void setId_user(Long id_user) {
		this.id_user = id_user;
	}
    
    
}