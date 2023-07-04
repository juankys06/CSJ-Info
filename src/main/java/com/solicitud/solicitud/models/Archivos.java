package com.solicitud.solicitud.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "csjinfo_Archivos")
public class Archivos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Lob
	@Column
	private String url;
	
	@ManyToOne
	@JoinColumn(name = "solicitudes_id", foreignKey = @ForeignKey(name = "FK_SOLICITUD"))
	private Solicitudes solicitudes;
	
	@ManyToOne
	@JoinColumn(name = "replica_id", foreignKey = @ForeignKey(name = "FK_REPLICA"))
	private Replica replicas;

	public Archivos( @NotBlank @URL String url, Solicitudes solicitudes,Replica replicas) {
		this.url = url;
		this.solicitudes = solicitudes;
		this.replicas = replicas;
	}
	
	public Archivos( @NotBlank @URL String url, Solicitudes solicitudes) {
		this.url = url;
		this.solicitudes = solicitudes;
	}
	
	public Archivos( @NotBlank @URL String url, Replica replicas) {
		this.url = url;
		this.replicas = replicas;
	}

	public Archivos() {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@JsonIgnore
	public Solicitudes getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(Solicitudes solicitudes) {
		this.solicitudes = solicitudes;
	}
	
	@JsonIgnore
	public Replica getReplicas() {
		return replicas;
	}

	public void setReplicas(Replica replicas) {
		this.replicas = replicas;
	}
	
	
	
	
}
