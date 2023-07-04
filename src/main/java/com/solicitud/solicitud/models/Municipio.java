package com.solicitud.solicitud.models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;

@Entity
@EntityListeners(AuditingEntityListener.class) 
@Table(name = "csjinfo_Municipios")
public class Municipio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_municipio;
	
	@NotBlank
	@Column(length = 75)
	private String nombre;
	
	@OneToMany(mappedBy = "municipio", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnore
	@NotAudited
	private Set<EntidadDepartamentoMunicipio> entidadDepartamentoMunicipio;
	
	@ManyToOne
	@JoinColumn(name = "id_departamento")
	@JsonIgnore
    @Audited (targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Departamento departamento;


	

	public Municipio(Long id_municipio, @NotBlank String nombre,
			Set<EntidadDepartamentoMunicipio> entidadDepartamentoMunicipio, Departamento departamento) {
		super();
		this.id_municipio = id_municipio;
		this.nombre = nombre;
		this.entidadDepartamentoMunicipio = entidadDepartamentoMunicipio;
		this.departamento = departamento;
	}



	public Municipio() {
		super();
		// TODO Auto-generated constructor stub
	}



	public Long getId_municipio() {
		return id_municipio;
	}

	public void setId_municipio(Long id_municipio) {
		this.id_municipio = id_municipio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Set<EntidadDepartamentoMunicipio> getEntidadDepartamentoMunicipio() {
		return entidadDepartamentoMunicipio;
	}

	public void setEntidadDepartamentoMunicipio(Set<EntidadDepartamentoMunicipio> entidadDepartamentoMunicipio) {
		this.entidadDepartamentoMunicipio = entidadDepartamentoMunicipio;
	}



	public Departamento getDepartamento() {
		return departamento;
	}



	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}
	
	
	
}
