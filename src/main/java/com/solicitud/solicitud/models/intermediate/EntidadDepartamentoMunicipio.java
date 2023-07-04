package com.solicitud.solicitud.models.intermediate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import javax.validation.constraints.Email;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditJoinTable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solicitud.solicitud.models.Departamento;
import com.solicitud.solicitud.models.Entidad;
import com.solicitud.solicitud.models.Municipio;
import com.solicitud.solicitud.models.Solicitudes;

@Entity
@EntityListeners(AuditingEntityListener.class) 
@Table(name = "csjinfo_entidad_departamento_municipio")
public class EntidadDepartamentoMunicipio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_edm;
	
	@Column(length = 255)
	@Email
    private String correo_dm;
    
    @Column(length = 255)
    private String telefono_dm;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
    
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "entidad_id")
    private Entidad entidad;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;
    
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;
    
    // @OneToMany(mappedBy = "entidadDepartamentoMunicipio", cascade = CascadeType.ALL, orphanRemoval = true)
    // @JsonIgnore
    // @AuditJoinTable(name = "csjinfo_auditoria")
    // private List<Solicitudes> solicitudes= new ArrayList<>();


	public EntidadDepartamentoMunicipio(Long id_edm, @Email String correo_dm, LocalDateTime create_date_time,
			LocalDateTime updateDateTime, Entidad entidad, Departamento departamento, Municipio municipio, 
            String telefono_dm) {
		super();
		this.id_edm = id_edm;
		this.correo_dm = correo_dm;
        this.telefono_dm = telefono_dm;
		this.create_date_time = create_date_time;
		this.updateDateTime = updateDateTime;
		this.entidad = entidad;
		this.departamento = departamento;
		this.municipio = municipio;
	}

	public EntidadDepartamentoMunicipio() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId_edm() {
		return id_edm;
	}

	public void setId_edm(Long id_edm) {
		this.id_edm = id_edm;
	}

	public LocalDateTime getCreate_date_time() {
		return create_date_time;
	}

	public void setCreate_date_time(LocalDateTime create_date_time) {
		this.create_date_time = create_date_time;
	}

	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public String getCorreo_dm() {
		return correo_dm;
	}

    public void setCorreo_dm(String correo_dm) {
        this.correo_dm = correo_dm;
    }

    public String getTelefono_dm() {
        return telefono_dm;
    }

    public void setTelefono_dm(String telefono_dm) {
        this.telefono_dm = telefono_dm;
    }

	// public List<Solicitudes> getSolicitudes() {
	// 	return solicitudes;
	// }

	// public void setSolicitudes(List<Solicitudes> solicitudes) {
	// 	this.solicitudes = solicitudes;
	// }
	
	
    
    
}
