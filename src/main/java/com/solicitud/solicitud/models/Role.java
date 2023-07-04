package com.solicitud.solicitud.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@EntityListeners(AuditingEntityListener.class) 
@Table(name = "csjinfo_roles")
@AuditTable(value = "csjinfo_auditoria")
@Audited
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_roles")
	private Long id;

	@NotBlank
	@Column(nullable = false, name="descripcion_roles")
	private String descripcion;
	
	@LastModifiedBy
	private String usuarioModified;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "roles")
	@NotAudited
	private Set<AppUser> users;
	
	@ManyToMany(mappedBy = "roles")
	@Audited (targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Set<Permiso> permisos;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "csjinfo_role_formulario", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "formulario_id"))
	@NotAudited
	private Set<Formulario> formularios = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void getId(Long id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Set<AppUser> getUsers(){
		return users;
	}
	
	public Set<Permiso> getPermisos(){
		return permisos;
	}
	
	public void setPermisos(List<Permiso> permisos) {
		this.permisos = new HashSet<Permiso>(permisos);
		
		permisos.forEach(permiso -> permiso.getRoles().add(this));
	}
	
	public void addPermiso(Permiso permiso) {
		permisos.add(permiso);
		permiso.getRoles().add(this);
	}
	
	public void removePermiso(Permiso permiso) {
		permisos.remove(permiso);
		permiso.getRoles().remove(this);
	}
	
	public void clearPermisos() {
		for(Permiso permiso : permisos)
			permiso.getRoles().remove(this);
		
		permisos.clear();
	}
	
	public Set<Formulario> getFormularios() {
		return formularios;
	}

	public void setFormularios(List<Formulario> formularios) {
		this.formularios = new HashSet<Formulario>(formularios);
		
		formularios.forEach(formulario -> formulario.getRoles().add(this));
	}
	
	
	public void addFormulario(Formulario formulario) {
		formularios.add(formulario);
		formulario.getRoles().add(this);
	}
	
	public void clearFormulario() {
		for(Formulario formulario : formularios)
			formulario.getRoles().remove(this);
		
		formularios.clear();
	}
}