package com.solicitud.solicitud.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.models.RespuestaUsuario;
import com.solicitud.solicitud.repository.AuditoriaRepository;
import com.solicitud.solicitud.repository.RespuestaUsuarioRepository;
import com.solicitud.solicitud.security.services.UserDetailsImpl;

@RestController
public class RespuestaUsuarioController {
	
	@Autowired
	AuditoriaRepository auditoriaRepository;
	
	@Autowired
	private RespuestaUsuarioRepository respuestaUsuarioRepository;
	
	
	@GetMapping("/respuestausuario")
	public List<RespuestaUsuario> getRespuestas(){
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		return respuestaUsuarioRepository.findByUser(userDetails.getId());
	}
	
	@GetMapping("/respuestausuario/{id}")
	public RespuestaUsuario getFormularioById(@PathVariable(value = "id") Long id){
		return respuestaUsuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RespuestaUsuario", "id", id));
	}
	
	
	@PostMapping("/respuestausuario")
	public ResponseEntity<RespuestaUsuario> createRespuesta(@RequestParam String titulo,  
									   @RequestParam String contenido) {
		
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		RespuestaUsuario respuesta = new RespuestaUsuario();
		
		respuesta.setId_user(userDetails.getId());
		respuesta.setTitulo(titulo);
		respuesta.setContenido_respuesta(contenido);
		
		return ResponseEntity.ok(respuestaUsuarioRepository.save(respuesta));
	}
	
	@PutMapping("/respuestausuario/{id}")
	public ResponseEntity<RespuestaUsuario> editRespuestaUsuario(@PathVariable(value = "id") Long id, 
									 @RequestParam(required = false) String titulo, 
									 @RequestParam(required = false) String contenido) {
		RespuestaUsuario respuesta = respuestaUsuarioRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("RespuestaUsuario", "id", id) );
		
		
		
		if(titulo != null)
			respuesta.setTitulo(titulo);
		
		if(contenido != null)
			respuesta.setContenido_respuesta(contenido);
		
		return ResponseEntity.ok(respuestaUsuarioRepository.save(respuesta));
	}
	
	@DeleteMapping("/respuestausuario/{id}")
	public ResponseEntity<RespuestaUsuario> deleteRespuestaUsuario(@PathVariable(value = "id") Long id) {
		
		try {
			respuestaUsuarioRepository.deleteById(id);
			
		} catch(EmptyResultDataAccessException exception) {
			throw new ResourceNotFoundException("RespuestaUsuario", "id", id);
		}
		
		return ResponseEntity.noContent().build();
	}

}
