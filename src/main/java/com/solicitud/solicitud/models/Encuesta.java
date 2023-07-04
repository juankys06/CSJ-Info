package com.solicitud.solicitud.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.JoinColumn;

@Entity
@EntityListeners(AuditingEntityListener.class) 
@Table(name = "csjinfo_Encuestas")
@AuditTable(value = "csjinfo_auditoria")
@Audited
public class Encuesta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_encuesta")
	private long id;
	
	private String titulo;
	
	@Column(nullable = false, columnDefinition = "BIT DEFAULT (0)", name="activo_encuesta")
	private boolean activo;
	
	@Column(nullable = true, unique = true, name= "url_encuesta")
	private String url;
	
	@LastModifiedBy
	private String usuarioModified;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
    
    @ManyToOne
    @JoinColumn(name = "formulario_id", nullable = false, foreignKey = @ForeignKey(name = "FK_FORMULARIO_ENCUESTA"))
    private Formulario formulario;
	
	@ManyToMany
	@JoinTable(name = "csjinfo_encuesta_pregunta", joinColumns = @JoinColumn(name = "encuesta_id"), inverseJoinColumns = @JoinColumn(name = "pregunta_id"))
	@NotAudited
	private Set<Pregunta> preguntas;
	
	public Encuesta() {}
	
	public Encuesta(String titulo) {
		this.titulo = titulo;
	}
	
	public long getId() {
		return id;
	}
	
	public String getTitulo() {
		return titulo;
	}
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public boolean isActivo() {
		return activo;
	}
	
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	public Set<Pregunta> getPreguntas(){
		return preguntas;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Formulario getFormulario() {
		return formulario;
	}
	
	public void setFormulario(Formulario formulario) {
		this.formulario = formulario;
	}
	
	public void addPregunta(Pregunta pregunta) {
		preguntas.add(pregunta);
		pregunta.getEncuestas().add(this);
	}
	
	public void addPreguntas(Collection<Pregunta> preguntas) {
		for(Pregunta pregunta : preguntas) {
			if(this.preguntas.contains(pregunta))
				continue;
			this.preguntas.add(pregunta);
			pregunta.getEncuestas().add(this);
		}
	}
	
	public void removePregunta(Pregunta pregunta) {
		preguntas.remove(pregunta);
		pregunta.getEncuestas().remove(this);
	}
	
	public void clearPreguntas() {
		for(Pregunta pregunta : new HashSet<Pregunta>(preguntas)) {
			pregunta.getEncuestas().remove(this);
			preguntas.remove(pregunta);
		}
	}
}
