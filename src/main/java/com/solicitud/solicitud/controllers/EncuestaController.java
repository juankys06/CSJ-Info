package com.solicitud.solicitud.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.exception.UnprocessableEntityException;
import com.solicitud.solicitud.models.Auditoria;
import com.solicitud.solicitud.models.Encuesta;
import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.Pregunta;
import com.solicitud.solicitud.models.Respuesta;
import com.solicitud.solicitud.models.Seleccion;
import com.solicitud.solicitud.repository.AuditoriaRepository;
import com.solicitud.solicitud.repository.EncuestaRepository;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.PreguntaRepository;
import com.solicitud.solicitud.repository.RespuestaRepository;
import com.solicitud.solicitud.repository.SeleccionRepository;
import com.solicitud.solicitud.security.SystemLoggedInUserAuditorAware;
import com.solicitud.solicitud.service.EncuestaService;

@RestController
public class EncuestaController {
	@Autowired
	EncuestaService encuestaService;
	
	@Autowired
	EncuestaRepository encuestaRepository;
	
	@Autowired
	PreguntaRepository preguntaRepository;
	
	@Autowired
	RespuestaRepository respuestaRepository;
	
	@Autowired
	SeleccionRepository seleccionRepository;
	
	@Autowired
	AuditoriaRepository auditoriaRepository;
	
	@Autowired
	FormularioRepository formularioRepository;
	
	@Autowired
	SystemLoggedInUserAuditorAware systemLoggedInUserAuditorAware;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@GetMapping("/encuestas")
	public List<Encuesta> getEncuestas(@RequestParam(required = false) Boolean activo, @RequestParam(required = false, name = "formulario") Long formularioId){
		return encuestaService.getEncuestas(activo, formularioId);
	}
	@GetMapping("/seleccion")
	public List<Seleccion> getSeleccion(@RequestParam(required = true, name = "encuesta_id") Long encuestaId){
		return seleccionRepository.findByEncuestaId(encuestaId);
	}
	@GetMapping("/encuestaCount")
	public boolean getEncuestasCount(@RequestParam(required = false) Long encuesta, 
            @RequestParam(required = false) String email) {
        int CountSol = encuestaRepository.getCountSol(encuesta, email);
        int CountPre = encuestaRepository.getCountPre(encuesta);
        int CountRes = encuestaRepository.getCountRes(encuesta, email);

        if (CountSol > (CountRes / CountPre))
            return true;
        else
            return false;
    }
    
	@GetMapping("/encuesta/{id}")
	public Encuesta getEncuestaId(@PathVariable(value = "id") Long encuestaId) {
		return encuestaService.findById(encuestaId).orElseThrow(() -> new ResourceNotFoundException("Encuesta", "id", encuestaId));
	}
	
	@PostMapping("/encuestas")
	public ResponseEntity<Encuesta> crearEncuesta(@RequestParam(required = true) String titulo, 
												  @RequestParam(required = false) @Pattern(regexp = "\\w+(-\\w+)*") String url,
												  @RequestParam(required = true, name = "formulario") Long formularioId) {
		Formulario formulario = formularioRepository.findById(formularioId).orElseThrow( ()-> new ResourceNotFoundException("Formulario", "id", formularioId));
		Encuesta encuesta = new Encuesta();
		encuesta.setTitulo(titulo);
		encuesta.setFormulario(formulario);
		
		if(url != null)
			if(url.matches("\\w+(-\\w+)*"))
				encuesta.setUrl(url);
			else
				return ResponseEntity.unprocessableEntity().build();

		return ResponseEntity.ok(encuestaService.save(encuesta));
	}
	
	@Transactional
	@PutMapping("/encuestas/{id}")
	public ResponseEntity<Encuesta> editEncuesta(@PathVariable(name = "id") Long encuestaId, 
												 @RequestParam(required = false) String titulo, 
												 @RequestParam(required = false) Boolean activo, 
												 @RequestParam(required = false) List<Long> preguntas,
												 @RequestParam(required = false, name = "formulario") Long formularioId,
												 @RequestParam(required = false) @Pattern(regexp = "") String url) {
		Encuesta encuesta = encuestaService.findById(encuestaId).orElseThrow(() -> new ResourceNotFoundException("Encuesta", "id", encuestaId));
		Formulario formulario = null;
		
		if(formularioId != null) {
			formulario = formularioRepository.findById(formularioId).orElseThrow(() -> new ResourceNotFoundException("Formulario", "id", formularioId));
			encuesta.setFormulario(formulario);
		}
		
		if(titulo != null)
			encuesta.setTitulo(titulo);
		
		if(url != null)
			if(url.matches("\\w+(-\\w+)*"))
				encuesta.setUrl(url);
			else
				return ResponseEntity.unprocessableEntity().build();
		
		if(activo != null)
			encuesta.setActivo(activo);
		
		if(preguntas != null)
			if(!preguntas.isEmpty()){
				encuesta.clearPreguntas();
				List<Pregunta> preguntasObj = preguntaRepository.findAllById(preguntas);
				encuesta.addPreguntas(preguntasObj);
			}
		
		return ResponseEntity.ok(encuestaService.save(encuesta));
	}
	
	@DeleteMapping("/encuestas/{id}")
	public ResponseEntity<Encuesta> deleteEncuesta(@PathVariable(value = "id") Long encuestaId){
		try {
			encuestaService.deleteById(encuestaId);
			Date in = new Date();
			LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
			Optional<Auditoria> auditoria  = auditoriaRepository.findById(auditoriaRepository.findByMaxRev());
			Auditoria aud = auditoria.get();
			aud.setUpdateDateTime(ldt);
			aud.setUsuarioModified(systemLoggedInUserAuditorAware.getCurrentAuditor().get());
			auditoriaRepository.save(aud);
			
		} catch(EmptyResultDataAccessException ex) {
			throw new ResourceNotFoundException("Encuesta", "id", encuestaId);
		}
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/preguntas")
	public List<Pregunta> getPreguntas(){
		return preguntaRepository.findAll();
	}
	
	@Transactional
	@PostMapping("/preguntas")
	public Pregunta createPregunta(@RequestParam(name = "pregunta") String texto, @RequestParam String[] respuestas) {
		Pregunta pregunta = new Pregunta(texto);
		preguntaRepository.save(pregunta);
		
		Set<Long> answers = new HashSet<Long>();
		
		for(String resp : respuestas) {
			Respuesta tempResp = respuestaRepository.findByTexto(resp);
			
			if(tempResp != null)
				answers.add(tempResp.getId());
			else
				answers.add(respuestaRepository.save(new Respuesta(resp)).getId());
		}
		if( ! answers.isEmpty() )
			for(Long respuestaId : answers) {
				jdbcTemplate.update("INSERT INTO csjinfo_pregunta_respuesta (pregunta_id, respuesta_id) VALUES (?, ?)", pregunta.getId(), Math.toIntExact(respuestaId));
			}
		
		return preguntaRepository.saveAndFlush(pregunta);
	}
	
	@Transactional
	@PutMapping("/preguntas/{id}")
	public Pregunta editPregunta(@PathVariable(value = "id") Long preguntaId, 
                                 @RequestParam(required = false) List<String> respuestas) {
		
		Pregunta pregunta = preguntaRepository.findById(preguntaId).orElseThrow(() -> new ResourceNotFoundException("Pregunta", "id", preguntaId));
		Set<Long> answers = new HashSet<Long>();
		
		if(respuestas != null)
			if(! respuestas.isEmpty() ) {
				try {
					jdbcTemplate.update("DELETE FROM csjinfo_pregunta_respuesta WHERE pregunta_id = ?", preguntaId);
				} catch(org.springframework.dao.DataAccessException exception) {
					throw new UnprocessableEntityException("No se puede editar la pregunta. Ya hay respuestas seleccionadas por usuarios. Por favor, cree una pregunta nueva.");
				}
				
				for(String resp : respuestas) {
					Respuesta tempResp = respuestaRepository.findByTexto(resp);
					
					if(tempResp != null)
						answers.add(tempResp.getId());
					else
						answers.add(respuestaRepository.save(new Respuesta(resp)).getId());
				}
			}
		
		if( ! answers.isEmpty() )
			for(Long respuestaId : answers)
				jdbcTemplate.update("INSERT INTO csjinfo_pregunta_respuesta (pregunta_id, respuesta_id) VALUES (?, ?)", pregunta.getId(), respuestaId);

		return preguntaRepository.save(pregunta);
	}
	
	@GetMapping("/respuestas")
	public List<Respuesta> getRespuestas(){
		return respuestaRepository.findAll();
	}
	
	@Transactional
	@DeleteMapping("/preguntas/{id}")
	public ResponseEntity<Pregunta> deletePregunta(@PathVariable(name = "id") Long preguntaId){
		Pregunta pregunta = preguntaRepository.findById(preguntaId).orElseThrow(() -> new ResourceNotFoundException("Pregunta", "id", preguntaId));
		
		jdbcTemplate.update("DELETE FROM csjinfo_pregunta_respuesta WHERE pregunta_id = ?", preguntaId);
		
		preguntaRepository.delete(pregunta);
		
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/seleccion")
	public ResponseEntity<String> seleccionar(@RequestParam(value = "preguntas") List<Long> preguntaId, 
											  @RequestParam(value = "respuestas") List<Long> respuestaId, 
											  @RequestParam(required = false) String user_agent,
											  @RequestParam(value = "encuesta") Long encuestaId) {
		if(preguntaId.size() != respuestaId.size())
			return ResponseEntity.badRequest().build();
		
		Encuesta encuesta = encuestaService.findById(encuestaId).orElseThrow(() -> new ResourceNotFoundException("Encuesta", "id", encuestaId));

		Set<Seleccion> selecciones = new HashSet<Seleccion>();
		
		for(int i = 0 ; i < preguntaId.size() ; i++)
			selecciones.add(new Seleccion(preguntaId.get(i), respuestaId.get(i), encuesta, user_agent));
		
		seleccionRepository.saveAll(selecciones);

		return ResponseEntity.ok("Se han guardado tus respuestas");
	}
}
