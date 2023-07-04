package com.solicitud.solicitud.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.models.Encuesta;
import com.solicitud.solicitud.models.Seleccion;
import com.solicitud.solicitud.models.intermediate.PreguntaRespuesta;
import com.solicitud.solicitud.repository.PreguntaRepository;
import com.solicitud.solicitud.repository.SeleccionRepository;
import com.solicitud.solicitud.service.EncuestaService;

@RestController
public class ReporteController {
	
	@Autowired
	PreguntaRepository preguntaRepository;
	@Autowired
	SeleccionRepository seleccionRepository;
	@Autowired
	EncuestaService encuestaService;
	
	@GetMapping("/reportes")
	public ResponseEntity<?> byEncuesta(@RequestParam(value = "encuesta", required = false)   Long encuestaId,
							  @RequestParam(value = "formulario", required = false) Long formularioId)
	{
		List<Seleccion> seleccionados = null;
		
		if(encuestaId != null) {
			Encuesta encuesta = encuestaService.findById(encuestaId).orElseThrow(() -> new ResourceNotFoundException("Encuesta", "id", encuestaId));
			seleccionados = seleccionRepository.findByEncuestaId(encuestaId);
			
			final Reporte reporte = new Reporte(encuesta.getTitulo(), seleccionRepository.countByEncuestaId(encuestaId)/encuesta.getPreguntas().size());
			for(com.solicitud.solicitud.models.Pregunta pregunta : encuesta.getPreguntas()) {
				double total_resp = 0; //-- Total de respuestas seleccionadas para la pregunta
				
				Pregunta rPregunta = new Pregunta(pregunta.getTexto()); //-- Pregunta en el reporte
				for(PreguntaRespuesta preguntaRespuesta : pregunta.getRespuestas()) {
					Respuesta rRespuesta = new Respuesta(preguntaRespuesta.getRespuesta().getTexto()); //-- Respuesta en el reporte
					
					for(Seleccion seleccion : seleccionados)
						if(seleccion.getPreguntaId() == pregunta.getId() && seleccion.getRespuestaId() == preguntaRespuesta.getRespuesta().getId()) {
							rRespuesta.valor++;
							total_resp++;
						}
					
					rPregunta.respuestas.add(rRespuesta);
				}
				
				for(Respuesta respuesta : rPregunta.respuestas)
					respuesta.valor /= total_resp;
				
				reporte.preguntas.add(rPregunta);
			}

			return ResponseEntity.ok(reporte);
		}else if(formularioId != null) {
			List<Encuesta> encuestas = encuestaService.getEncuestas(null, formularioId);
			List<Reporte> reportes = new ArrayList<Reporte>();
			
			for(Encuesta oEncuesta : encuestas) {
				seleccionados = seleccionRepository.findByEncuestaId(oEncuesta.getId());
				Reporte reporte = new Reporte(oEncuesta.getTitulo(), seleccionRepository.countByEncuestaId(oEncuesta.getId())/oEncuesta.getPreguntas().size());
				
				for(com.solicitud.solicitud.models.Pregunta pregunta : oEncuesta.getPreguntas()) {
					double total_resp = 0; //-- Total de respuestas seleccionadas para la pregunta
					
					Pregunta rPregunta = new Pregunta(pregunta.getTexto()); //-- Pregunta en el reporte
					for(PreguntaRespuesta preguntaRespuesta : pregunta.getRespuestas()) {
						Respuesta rRespuesta = new Respuesta(preguntaRespuesta.getRespuesta().getTexto()); //-- Respuesta en el reporte
						
						for(Seleccion seleccion : seleccionados)
							if(seleccion.getPreguntaId() == pregunta.getId() && seleccion.getRespuestaId() == preguntaRespuesta.getRespuesta().getId()) {
								rRespuesta.valor++;
								total_resp++;
							}
						
						rPregunta.respuestas.add(rRespuesta);
					}
					
					for(Respuesta respuesta : rPregunta.respuestas)
						respuesta.valor /= total_resp;
					
					reporte.preguntas.add(rPregunta);
				}
				
				reportes.add(reporte);
			}
			
			return ResponseEntity.ok(reportes);
		} else {
			HttpHeaders responseHeaders = new HttpHeaders(); 
			responseHeaders.set("Content-Type", "application/json");
			
			return new ResponseEntity<String>("{'message':'Número de parámetros insuficientes.', 'error': 'Alguno de los parámetros (encuesta o formulario), debe estar presente en la petición.'}".replace('\'', '"'), responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
}

class Reporte {
	public String nombre;
	public Long muestra;
	public List<Pregunta> preguntas = new ArrayList<Pregunta>();
	
	public Reporte(String nombre, Long muestra) {
		this.nombre = nombre;
		this.muestra = muestra;
	}
}

class Pregunta {
	public String nombre;
	public List<Respuesta> respuestas = new ArrayList<Respuesta>();
	
	public Pregunta(String nombre) {
		this.nombre = nombre;
	}
}

class Respuesta {
	public String nombre;
	public Double valor = 0.0;
	
	public Respuesta(String nombre) {
		this.nombre = nombre;
	}
}
