package com.solicitud.solicitud.models.keys;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class PreguntaRespuestaKey implements Serializable{
	@Column(name = "pregunta_id")
	Long pregunta_id;
	@Column(name = "respuesta_id")
	Long respuesta_id;
	
	@SuppressWarnings("unused")
	private PreguntaRespuestaKey() {}
	
	public PreguntaRespuestaKey(Long pregunta_id, Long respuesta_id) {
		this.pregunta_id = pregunta_id;
		this.respuesta_id = respuesta_id;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        PreguntaRespuestaKey that = (PreguntaRespuestaKey) o;
        return Objects.equals(pregunta_id, that.pregunta_id) &&
               Objects.equals(respuesta_id, that.respuesta_id);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(pregunta_id, respuesta_id);
    }
}
