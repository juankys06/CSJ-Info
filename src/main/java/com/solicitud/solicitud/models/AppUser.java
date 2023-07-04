package com.solicitud.solicitud.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "csjinfo_app_user")
@EntityListeners(AuditingEntityListener.class) 
@AuditTable(value = "csjinfo_auditoria")
@Audited
public class AppUser {
	@Id
	@NotAudited
	private Long id;
	
	@LastModifiedBy
	private String usuarioModified;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;    
    
//	@OneToOne(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
//	private Formulario formulario;
	
	@OneToOne
	@JoinColumn(name = "id_user", nullable = false, foreignKey = @ForeignKey(name = "FK_APP_USER"), unique = true)
	@MapsId
	@JsonIgnore
	@Audited (targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private User user;
	
	@NotAudited
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "csjinfo_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), foreignKey = @ForeignKey(name = "FK_USER"), inverseForeignKey = @ForeignKey(name = "FK_ROLE"))
	private Set<Role> roles = new HashSet<>();
	
	public AppUser() {}
	
	public AppUser(User user) {
		this.user = user;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getScreenName() {
		return user.getScreenname();
	}
	
	public String getEmailAddress() {
		return user.getEmailaddress();
	}
	
	public String getGreeting() {
		return user.getGreeting();
	}
	
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		roles.add(role);
		role.getUsers().add(this);
	}
	
	public void clearRoles() {
		for(Role role : roles)
			role.getUsers().remove(this);
		
		roles.clear();
	}
	
	@JsonIgnore
	public Set<Permiso> getPermisos() {
		Set<Permiso> permisos = new HashSet<Permiso>() ;
		
		roles.forEach(role -> permisos.addAll(role.getPermisos()));
		
		return permisos;
	}
}
