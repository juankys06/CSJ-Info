package com.solicitud.solicitud.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ForeignKey;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "csjinfo_Replica")
public class Replica {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Lob
	@Column
	private String respuesta;
	
	@NotBlank
	@Lob
	@Column
	private String asunto;
	
	
	@Lob
	@Column
	private String cc;
	
	@ManyToOne
	@JoinColumn(name = "solicitudes_id", foreignKey = @ForeignKey(name = "FK_SOLICITUDES"))
	private Solicitudes solicitudes;
	
	@OneToMany(mappedBy = "solicitudes", orphanRemoval = true, cascade = CascadeType.ALL)
	@NotAudited
	private List<Archivos> archivos= new ArrayList<>();

	public Replica() {}

	public Replica(String respuesta, String asunto, String cc) {
		this.respuesta = respuesta;
		this.asunto = asunto;
		this.cc = cc;
	}

	public Long getId() {
		return id;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}
	
	@JsonIgnore
	public Solicitudes getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(Solicitudes solicitud_id) {
		this.solicitudes = solicitud_id;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public List<Archivos> getArchivos() {
		return archivos;
	}

	public void setArchivos(List<Archivos> archivos) {
		this.archivos = archivos;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}	
	
	
	
	
}
