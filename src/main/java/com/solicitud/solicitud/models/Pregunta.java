package com.solicitud.solicitud.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solicitud.solicitud.models.intermediate.PreguntaRespuesta;

@Entity
@Table(name = "csjinfo_Preguntas")
public class Pregunta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 255)
	private String texto;
	
	@ManyToMany(mappedBy = "preguntas")
	@JsonIgnore
	private Set<Encuesta> encuestas;
	
	@OneToMany(
		mappedBy = "pregunta",
		orphanRemoval = true
	)
	private Set<PreguntaRespuesta> respuestas;
	
	
	
	@SuppressWarnings("unused")
	private Pregunta() {}
	
	public Pregunta(String texto) {
		this.texto = texto;
		this.respuestas = new HashSet<PreguntaRespuesta>();
	}
	
	public Long getId() {
		return id;
	}
	
	public String getTexto() {
		return texto;
	}
	
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public Set<Encuesta> getEncuestas(){
		return encuestas;
	}
	
	public Set<PreguntaRespuesta> getRespuestas(){
		return respuestas;
	}
	
	public void addRespuesta(Respuesta respuesta) {
		
		PreguntaRespuesta preguntaRespuesta = new PreguntaRespuesta(this, respuesta);
		respuestas.add(preguntaRespuesta);
	}
	
	public void addRespuestas(Collection<Respuesta> respuestas) {
		
		for(Respuesta resp : respuestas){
			//System.out.println(resp);
			PreguntaRespuesta preguntaRespuesta = new PreguntaRespuesta(this, resp);
			//System.out.println(preguntaRespuesta.getRespuesta());
			
			this.respuestas.add(preguntaRespuesta);
		}
	}
	
	public void removeRespuesta(Respuesta respuesta) {
		for(Iterator<PreguntaRespuesta> iterator = respuestas.iterator(); iterator.hasNext(); ) {
			PreguntaRespuesta preguntaRespuesta = iterator.next();
			
			if(preguntaRespuesta.getPregunta().equals(this) && preguntaRespuesta.getRespuesta().equals(respuesta)){
				iterator.remove();
				preguntaRespuesta.getRespuesta().getPreguntas().remove(preguntaRespuesta);
				preguntaRespuesta.setPregunta(null);
				preguntaRespuesta.setRespuesta(null);
			}
		}
	}
	
	public void clearRespuestas() {
		for(Iterator<PreguntaRespuesta> iterator = respuestas.iterator(); iterator.hasNext(); ) {
			PreguntaRespuesta preguntaRespuesta = iterator.next();
			iterator.remove();
			
			preguntaRespuesta.getRespuesta().getPreguntas().remove(preguntaRespuesta);
			preguntaRespuesta.getPregunta().getRespuestas().remove(preguntaRespuesta);
			
//			preguntaRespuesta.setPregunta(null);
//			preguntaRespuesta.setRespuesta(null);
		}
	}
}
