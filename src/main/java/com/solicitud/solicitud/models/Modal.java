package com.solicitud.solicitud.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "csjinfo_modals")
public class Modal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column
    private String titulo;

    @NotBlank
    @Lob
    @Column
    private String contenido;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT (0)", name = "activo")
    private Boolean activo;

    @OneToOne(mappedBy = "modal", cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Formulario formulario;
    

    public Modal() {
    }

    public Modal(String titulo, String contenido, Boolean activo, Formulario formulario) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.activo = activo;
        this.formulario = formulario;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    
    public Long getFormulario_id() {
        return formulario.getId();
    }

    public String getFormulario_descripcion() {
        return formulario.getDescripcion();
    }

    @JsonIgnore
    public Formulario getFormulario() {
        return formulario;
    }

    @JsonIgnore
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

}
