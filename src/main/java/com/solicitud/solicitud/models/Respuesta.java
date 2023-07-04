package com.solicitud.solicitud.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solicitud.solicitud.models.intermediate.PreguntaRespuesta;

@Entity
@Table(name = "csjinfo_Respuestas")
public class Respuesta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String texto;
	
	@OneToMany(mappedBy = "respuesta", orphanRemoval = true)
	@JsonIgnore
	private Set<PreguntaRespuesta> preguntas;
	
	public Respuesta() {}
	
	public Respuesta(String texto) {
		this.texto = texto;
		preguntas = new HashSet<PreguntaRespuesta>();
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
	
	public Set<PreguntaRespuesta> getPreguntas(){
		return preguntas;
	}
	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Respuesta other = (Respuesta) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
