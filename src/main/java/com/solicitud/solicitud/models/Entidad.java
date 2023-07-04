package com.solicitud.solicitud.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;
import com.solicitud.solicitud.models.intermediate.PreguntaRespuesta;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "csjinfo_Entidad")
@DynamicUpdate
@AuditTable(value = "csjinfo_auditoria")
@Audited
public class Entidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @NotBlank
    @Column(length = 200)
    private String nombre;

    @Column(unique = false, name = "email_entidad")
    private String email;

    @Column(nullable = true, name = "telefono_entidad")
    private String telefono;

    @Column(nullable = true, columnDefinition = "VARCHAR(50) CHECK (tipo IN ('Nacional', 'Departamento', 'Municipio'))")
    private String tipo;

    @LastModifiedBy
    private String usuarioModified;

    @CreationTimestamp
    private LocalDateTime create_date_time;

    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;

    @OneToMany(mappedBy = "entidad", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JsonIgnoreProperties("entidad")
    @NotAudited
    private Set<EntidadDepartamentoMunicipio> entidadDepartamentoMunicipio;

    @OneToMany(mappedBy = "entidadPadre")
    private List<Entidad> entidadHijos;

    @ManyToOne
    @JoinColumn(name = "entidad_codigo", referencedColumnName = "codigo")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JsonIgnoreProperties("entidadHijos")
    private Entidad entidadPadre;

    public Entidad(Long codigo, @NotBlank String nombre, String email, String telefono, String tipo,
            Entidad entidadPadre) {
        super();
        this.codigo = codigo;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.tipo = tipo;
        this.entidadPadre = entidadPadre;
    }

    public Entidad() {
        // TODO Auto-generated constructor stub
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuarioModified() {
        return usuarioModified;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Set<EntidadDepartamentoMunicipio> getEntidadDepartamentoMunicipio() {
        return entidadDepartamentoMunicipio;
    }

    public void setEntidadDepartamentoMunicipio(Set<EntidadDepartamentoMunicipio> entidadDepartamentoMunicipio) {
        this.entidadDepartamentoMunicipio = entidadDepartamentoMunicipio;
    }

    public List<Entidad> getEntidadHijos() {
        return entidadHijos;
    }

    public void setEntidadHijos(List<Entidad> entidadHijos) {
        this.entidadHijos = entidadHijos;
    }

    public Entidad getEntidadPadre() {
        return entidadPadre;
    }

    public void setEntidadPadre(Entidad entidadPadre) {
        this.entidadPadre = entidadPadre;
    }

}
