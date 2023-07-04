package com.solicitud.solicitud.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.exception.EmailExistsException;
import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.models.Entidad;
import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;
import com.solicitud.solicitud.repository.EntidadDepartamentoMunicipioRepository;
import com.solicitud.solicitud.repository.EntidadRepository;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.service.EntidadService;

@RestController
public class EntidadController {
	@Autowired
	EntidadService entidadService;
	
	@Autowired
	private EntidadRepository entidadRepository;
	@Autowired
	private FormularioRepository formularioRepository;
	@Autowired
	private EntidadDepartamentoMunicipioRepository entiDepMunRepository;
 
    @RequestMapping(value = "/entidad")
    public List<Entidad> sample() {
        return entidadService.getAll();
    }
    @GetMapping("/entidadN")
	public List<Entidad> geEntidadN() {
			return entidadRepository.findByEntidaN();
	}
    @GetMapping("/entidad/{id}")
	public Entidad getEntidadId(@PathVariable(value = "id") Long entidadId) {
		return entidadService.findById(entidadId).orElseThrow(() -> new ResourceNotFoundException("Entidad", "id", entidadId));
	}
	
    @RequestMapping(value = "/entidad", method = RequestMethod.POST)
    public Entidad createSample( @RequestParam(required = true) String nombre,
								 @RequestParam(required = false) @Email String email,
								 @RequestParam(required = false) String tipo,
								 @RequestParam(required = false) String telefono,
								 @RequestParam(required = false) Long entidadPadre) {
    	Entidad Service = new Entidad();
    	if(entidadPadre != null) {
    		Entidad entidadObj = entidadRepository.findById( entidadPadre).orElseThrow( () -> new ResourceNotFoundException("Entidad", "id",  entidadPadre));
    		Service.setEntidadPadre(entidadObj);
    	}
    	
    	if(email != null)
            Service.setEmail(email);
            
        if (telefono != null)
            Service.setTelefono(telefono);
    	
    	if(tipo != null)
    		Service.setTipo(tipo);
    	
    	Service.setNombre(nombre);
    	
    	if(email != null) {
    		List<Formulario> form = formularioRepository.findByEmail(email);
    		if(form.size()>0) {
    			throw new EmailExistsException();
    		}
    	}
    	
        return entidadService.createEntidad(Service);
    }
 
    @RequestMapping(value = "/entidad/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteSample(@PathVariable(value = "id") Long id) {
        return entidadService.deleteEntidad(id);
    }
 
    @RequestMapping(value = "/entidad/{id}", method = RequestMethod.PUT)
    public Entidad updateSample(@PathVariable(value = "id") Long id,
            @Valid @RequestBody Entidad Service) {
        return entidadService.updateEntidad(id, Service);
    }
}
