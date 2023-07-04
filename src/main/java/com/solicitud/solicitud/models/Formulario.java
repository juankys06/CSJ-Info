package com.solicitud.solicitud.models;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@EntityListeners(AuditingEntityListener.class) 
@Table(name = "csjinfo_Formularios")
@AuditTable(value = "csjinfo_auditoria")
@Audited
public class Formulario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_formulario")
	private Long id;
	
	@Column(nullable = false, name="descripcion_formulario")
	private String descripcion;
	
	@Column(nullable = false, columnDefinition = "BIT DEFAULT (1)", name="activo_formulario")
	private boolean activo;
	
	@Lob
	@Column(nullable = false, name="contenido_formulario")
	private String contenido;
	
	@LastModifiedBy
	private String usuarioModified;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
	
	@Column(nullable = true, unique = true, name="url_formulario")
	private String url;
	
	@OneToMany(mappedBy = "formulario", orphanRemoval = true)
	@NotAudited
	private Set<Solicitudes> solicitudes;
	
	@OneToMany(mappedBy = "formulario", orphanRemoval = true)
    private Set<Encuesta> encuestas;
    
    @OneToOne
    @NotAudited
    @JoinColumn(name = "modal_id", referencedColumnName = "id", nullable = true)
    private Modal modal;
	
	@ManyToMany(mappedBy = "formularios")
	@NotAudited
	@JsonIgnore
	private Set<Role> roles;
	
	@Column(nullable = true, unique = true)
	private String email;
	
	@JsonIgnore
	@NotAudited
	private String emailPassword;
	
	/**
	 * SMTP Host
	 */
	public String host;
	/**
	 * SMTP port
	 */
	public Integer port;
	
    @Column(nullable = true, columnDefinition = "varchar(5000)")
    private String request_token;
	
//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER"), unique = true)
//	private User user;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public boolean getActivo() {
		return this.activo;
	}
	
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	public boolean activo() {
		return this.activo;
	}
	
	public void desactivar() {
		this.activo = false;
	}
	
	public String getContenido() {
		return contenido;
	}
	
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmailPassword() {
		return this.emailPassword;
	}
	
	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public Set<Role> getRoles() {
		return roles;
	}
	
	@JsonIgnore
	public Set<Encuesta> getEncuestas(){
		return encuestas;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public String getToken() {
		return request_token;
	}
	
    public void setToken(String request_token) {
        this.request_token = request_token;
    }
    
    public Modal getModal() {
        return modal;
    }

    public void setModal(Modal modal) {
        this.modal = modal;
    }
}
