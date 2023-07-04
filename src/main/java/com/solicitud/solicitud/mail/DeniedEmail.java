package com.solicitud.solicitud.mail;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;

@Entity
@Table(name = "csjinfo_denied_email")
public class DeniedEmail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Email
	@Column(nullable = false, unique = true)
	private String email;
	
	@SuppressWarnings("unused")
	private DeniedEmail() {}
	public DeniedEmail(String email) {
		this.email = email;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}