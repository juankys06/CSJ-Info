package com.solicitud.solicitud.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "csjinfo_Status")
public class Status {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank
	@Column(length = 75)
	private String nombreStatus;
	
	@OneToMany(mappedBy = "status", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Solicitudes> solicitud= new ArrayList<>();

	

	public Status(long id, @NotBlank String nombreStatus, List<Solicitudes> solicitud) {
		this.id = id;
		this.nombreStatus = nombreStatus;
		this.solicitud = solicitud;
	}


	public Status() {
		// TODO Auto-generated constructor stub
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombreStatus() {
		return nombreStatus;
	}

	public void setNombreStatus(String nombreStatus) {
		this.nombreStatus = nombreStatus;
	}

	@JsonIgnore
	public List<Solicitudes> getSolicitud() {
		return solicitud;
	}


	public void setSolicitud(List<Solicitudes> solicitud) {
		this.solicitud = solicitud;
	}
	
	


	
}
