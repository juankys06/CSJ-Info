package com.solicitud.solicitud.models.intermediate;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.solicitud.solicitud.models.Pregunta;
import com.solicitud.solicitud.models.Respuesta;
import com.solicitud.solicitud.models.keys.PreguntaRespuestaKey;

@Entity
@Table(name = "csjinfo_pregunta_respuesta")
public class PreguntaRespuesta {
	@EmbeddedId
	@JsonIgnore
	private PreguntaRespuestaKey id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@MapsId("pregunta_id")
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_pregunta"),name = "pregunta_id", referencedColumnName = "id")
	@JsonIgnore
	private Pregunta pregunta;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@MapsId("respuesta_id")
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_respuesta"),name = "respuesta_id", referencedColumnName = "id")
	@JsonValue
	private Respuesta respuesta;

	
	@SuppressWarnings("unused")
	private PreguntaRespuesta() {}
	
	public PreguntaRespuesta(Pregunta pregunta, Respuesta respuesta) {
		this.pregunta = pregunta;
		this.respuesta = respuesta;
		this.id = new PreguntaRespuestaKey(pregunta.getId(), respuesta.getId());
	}
	
	public PreguntaRespuestaKey getId() {
		return id;
	}
	
	public Pregunta getPregunta() {
		return pregunta;
	}
	
	public void setPregunta(Pregunta pregunta) {
		this.pregunta = pregunta;
	}
	
	public Respuesta getRespuesta() {
		return respuesta;
	}
	
	public void setRespuesta(Respuesta respuesta) {
		this.respuesta = respuesta;
	}
}
