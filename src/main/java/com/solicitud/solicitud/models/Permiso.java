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
import javax.persistence.ForeignKey;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

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
@Table(name = "csjinfo_Permisos")
@AuditTable(value = "csjinfo_auditoria")
@Audited
public class Permiso {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_permiso")
	private long id;
	
	@NotBlank
	@Column(nullable = false, name="descripcion_permiso")
	private String descripcion;
	
	@LastModifiedBy
	private String usuarioModified;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
	
	@JsonIgnore
	@ManyToMany
	@NotAudited
	@JoinTable(name = "csjinfo_role_permission", joinColumns = @JoinColumn(name = "permiso_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), foreignKey = @ForeignKey(name = "FK_PERMISO"), inverseForeignKey = @ForeignKey(name = "FK_ROLE"))

	private Set<Role> roles;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}
}