package com.solicitud.solicitud.models;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;

@Entity
@EntityListeners(AuditingEntityListener.class) 
@Table(name = "csjinfo_Departamento")
public class Departamento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_departamento;
	
	@NotBlank
	@Column(length = 75)
	private String nombre;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
    
    @OneToMany(mappedBy = "departamento", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnore
	@NotAudited
	private Set<EntidadDepartamentoMunicipio> entidadDepartamentoMunicipio;
    
    @OneToMany(mappedBy = "departamento", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@NotAudited
	private Set<Municipio> municipio;
    

    

	public Departamento(Long id_departamento, @NotBlank String nombre, LocalDateTime create_date_time,
			LocalDateTime updateDateTime, Set<EntidadDepartamentoMunicipio> entidadDepartamentoMunicipio,
			Set<Municipio> municipio) {
		super();
		this.id_departamento = id_departamento;
		this.nombre = nombre;
		this.create_date_time = create_date_time;
		this.updateDateTime = updateDateTime;
		this.entidadDepartamentoMunicipio = entidadDepartamentoMunicipio;
		this.municipio = municipio;
	}


	public Departamento() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Long getId_departamento() {
		return id_departamento;
	}

	public void setId_departamento(Long id_departamento) {
		this.id_departamento = id_departamento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public Set<Municipio> getMunicipio() {
		return municipio;
	}


	public void setMunicipio(Set<Municipio> municipio) {
		this.municipio = municipio;
	}
    
    
    
}
