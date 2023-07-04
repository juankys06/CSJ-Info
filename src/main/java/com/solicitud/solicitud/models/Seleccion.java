package com.solicitud.solicitud.models;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.solicitud.solicitud.models.intermediate.PreguntaRespuesta;

@Entity
@Table(name = "csjinfo_seleccion")
public class Seleccion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@MapsId("respId")
	@JoinColumns(foreignKey = @ForeignKey(name = "FK_PREGUNTA_RESPUESTA_ID"), value = {
		@JoinColumn(name = "pregunta_id", referencedColumnName = "pregunta_id"),
		@JoinColumn(name = "respuesta_id", referencedColumnName = "respuesta_id")
	})
	@ManyToOne
	private PreguntaRespuesta preguntaRespuesta;
	
	@Column(name = "pregunta_Id", nullable = false)
	private long pregunta_id;
	@Column(name = "respuesta_Id", nullable = false)
	private long respuesta_id;
	
	@Column(nullable = true)
	private String user_agent;
	
	@CreationTimestamp
    private LocalDateTime create_date_time;
 
    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
	
	@ManyToOne(optional = false, cascade = CascadeType.REMOVE)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ENCUESTA"), name = "encuesta_id", referencedColumnName = "id_encuesta")
	private Encuesta encuesta;
	
	public Seleccion() {}
	
	public Seleccion(Long pregunta_id, Long respuesta_id, Encuesta encuesta, String user_agent) {
		this.pregunta_id = pregunta_id;
		this.respuesta_id = respuesta_id;
		this.encuesta = encuesta;
		this.user_agent = user_agent;
	}
	
	public long getId() {
		return id;
	}
	
	public long getPreguntaId() {
		return pregunta_id;
	}
	
	public void setPreguntaId(Long pregunta_id) {
		this.pregunta_id = pregunta_id;
	}
	
	public long getRespuestaId() {
		return respuesta_id;
	}
	
	public void setRespuestaId(Long respuesta_id) {
		this.respuesta_id = respuesta_id;
	}
	
	public Encuesta getEncuesta() {
		return encuesta;
	}
	
	public String getUserAgent() {
		return user_agent;
	}
	
	public void setUserAgent(String user_agent) {
		this.user_agent = user_agent;
	}

	public LocalDateTime getCreate_date_time() {
		return create_date_time;
	}

	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}
	
}
