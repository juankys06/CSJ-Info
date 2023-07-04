package com.solicitud.solicitud.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.exception.EmailExistsException;
import com.solicitud.solicitud.exception.MSQueryException;
import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.models.Auditoria;
import com.solicitud.solicitud.models.Entidad;
import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;
import com.solicitud.solicitud.repository.AuditoriaRepository;
import com.solicitud.solicitud.repository.EntidadDepartamentoMunicipioRepository;
import com.solicitud.solicitud.repository.EntidadRepository;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.UserRepository;
import com.solicitud.solicitud.security.SystemLoggedInUserAuditorAware;

@RestController
public class FormularioController {
	@Autowired
	AuditoriaRepository auditoriaRepository;
	
	@Autowired
	SystemLoggedInUserAuditorAware systemLoggedInUserAuditorAware;
	
	@Autowired
	private EntidadRepository entidadRepository;
	@Autowired
	private FormularioRepository formularioRepository;
	@Autowired
    private EntidadDepartamentoMunicipioRepository entiDepMunRepository;
    
    @Autowired
    private tareasProgramadas tareasProgramadasInstance;
	
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/formularios")
	public List<Formulario> getFormularios(){
		return formularioRepository.findAll();
	}
	
	@GetMapping("/formularios/activo")
    public Formulario getActivo() {
        return formularioRepository.findByActivoTrue();
    }

    @GetMapping("/formularios/url/{url}")
    public Formulario getActivo(@PathVariable(value = "url") String url) {
        return formularioRepository.findByUrl(url);
    }
	
	@GetMapping("/formularios/{id}")
	public Formulario getFormularioById(@PathVariable(value = "id") Long formularioId){
		return formularioRepository.findById(formularioId).orElseThrow(() -> new ResourceNotFoundException("Formularios", "id", formularioId));
	}
	
	@PostMapping("/formularios")
	public ResponseEntity<Formulario> createFormulario(@RequestParam String descripcion, 
									   @RequestParam(required = false) Boolean activo, 
									   @RequestParam String contenido, 
									   @RequestParam(required = true) String url) {
		Formulario formulario = new Formulario();
		
		formulario.setDescripcion(descripcion);		
		formulario.setContenido(contenido);
		
		if(activo != null)
			formulario.setActivo(activo);
		
		if(url.matches("\\w+(-\\w+)*"))
			formulario.setUrl(url);
		else
			return ResponseEntity.unprocessableEntity().build();
		
		return ResponseEntity.ok(formularioRepository.save(formulario));
	}
	
	@PutMapping("/formularios/{id}")
	public ResponseEntity<Formulario> editFormulario(@PathVariable(value = "id") Long formularioId, 
									 @RequestParam(required = false) String descripcion, 
									 @RequestParam(required = false) String contenido, 
									 @RequestParam(required = false) Boolean activo, 
									 @RequestParam(required = false) String url,
									 @RequestParam(required = false) @Email String email,
									 @RequestParam(required = false) String emailPassword,
									 @RequestParam(required = false) String host,
									 @RequestParam(required = false) Integer port,
									 @RequestParam(required = false) String imapHost,
									 @RequestParam(required = false) Integer imapPort) {
		Formulario formulario = formularioRepository.findById(formularioId).orElseThrow( () -> new ResourceNotFoundException("Formulario", "id", formularioId) );
		
		if(email!=null && formulario.getEmail()!=null && !formulario.getEmail().equals(email)) {
			List<Entidad> enti = entidadRepository.findByEmail(email);
			List<Formulario> form = formularioRepository.findByEmail(email);
			List<EntidadDepartamentoMunicipio> edm = entiDepMunRepository.findByEmail_dm(email);
			if(enti.size()>0 || form.size()>0 || edm.size()>0) {
				throw new EmailExistsException();
			}
		}
		
		if(descripcion != null)
			formulario.setDescripcion(descripcion);
		
		if(contenido != null)
			formulario.setContenido(contenido);
		
		if(activo != null)
			formulario.setActivo(activo);
		
		if(url != null)
			if(url.matches("\\w+(-\\w+)*"))
				formulario.setUrl(url);
			else
				return ResponseEntity.unprocessableEntity().build();
		
		if(email != null)
			formulario.setEmail(email);
		
		if(emailPassword != null)
			formulario.setEmailPassword(emailPassword);
		
		if(host != null)
			formulario.host = host;
		
		if(port != null)
            formulario.port = port;

        formularioRepository.save(formulario);

        if (activo != null && activo == true) {
            new Thread(() -> {
                try {
                    System.out.println("Sincronizando correos");
                    tareasProgramadasInstance.downloadEmailsFormularios();
                } catch (MSQueryException | MessagingException | IOException | URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }, "scheduling-1").start();
        }

		return ResponseEntity.ok(formulario);
	}
	
	@DeleteMapping("/formularios/{id}")
	public ResponseEntity<Formulario> deleteFormulario(@PathVariable(value = "id") Long formularioId) {
		
		try {
			formularioRepository.deleteById(formularioId);
			Date in = new Date();
			LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
			int[] ids = auditoriaRepository.findByIds();
			for(int i =0;i<ids.length;i++) {
				Optional<Auditoria> audi = auditoriaRepository.findById(ids[i]);
				Auditoria aud=audi.get();
				aud.setUpdateDateTime(ldt);
				aud.setUsuarioModified(systemLoggedInUserAuditorAware.getCurrentAuditor().get());
				auditoriaRepository.save(aud);
			}
			
		} catch(EmptyResultDataAccessException exception) {
			throw new ResourceNotFoundException("Formularios", "id", formularioId);
		}
		
		return ResponseEntity.noContent().build();
	}
}
