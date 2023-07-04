package com.solicitud.solicitud.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.models.Auditoria;
import com.solicitud.solicitud.models.Departamento;
import com.solicitud.solicitud.models.Entidad;
import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.Municipio;
import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;
import com.solicitud.solicitud.repository.DepartamentoReposytory;
import com.solicitud.solicitud.repository.EntidadDepartamentoMunicipioRepository;
import com.solicitud.solicitud.repository.EntidadRepository;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.MunicipioRepository;

@RestController
public class EntidadDepartamentoMunicipioController {
    @Autowired
    EntidadDepartamentoMunicipioRepository entidadDepartamentoMunicipioRepository;

    @Autowired
    private EntidadRepository entidadRepository;
    @Autowired
    private FormularioRepository formularioRepository;

    @Autowired
    private DepartamentoReposytory departamentoReposytory;

    @Autowired
    private MunicipioRepository municipioRepository;

    @GetMapping("/entidadDepartamentoMunicipio")
    public List<EntidadDepartamentoMunicipio> getEntidadDepartamentoMunicipio() {
        return entidadDepartamentoMunicipioRepository.findAll();
    }

    @GetMapping("/entidadDepartamentoMunicipio/{id}")
    public EntidadDepartamentoMunicipio getEntidadDepartamentoMunicipioId(
            @PathVariable(value = "id") Long entidadDepartamentoMunicipioId) {
        return entidadDepartamentoMunicipioRepository.findById(entidadDepartamentoMunicipioId)
                .orElseThrow(() -> new ResourceNotFoundException("EntidadDepartamentoMunicipioId", "id",
                        entidadDepartamentoMunicipioId));
    }

    @GetMapping("/municipios/seleccion/{entidad}")
    public List<EntidadDepartamentoMunicipio> getSelectMunicipio(@PathVariable(value = "entidad") Long entidadId) {
        return entidadDepartamentoMunicipioRepository.findBySelect(entidadId);
    }

    @GetMapping("/departamento/seleccion/{entidad}")
    public List<EntidadDepartamentoMunicipio> getSelectDepartamento(@PathVariable(value = "entidad") Long entidadId) {
        return entidadDepartamentoMunicipioRepository.findBySelect(entidadId);
    }

    @PostMapping("/entidadDepartamentoMunicipio")
    public EntidadDepartamentoMunicipio create(@RequestParam(required = true) Long entidad,
            @RequestParam(required = false) Long departamento,
            @RequestParam(required = false) Long municipio,
            @RequestParam(required = true) @Email String correo,
            @RequestParam(required = false) String telefono) {

        EntidadDepartamentoMunicipio entidadDepartamentoMunicipioObj = new EntidadDepartamentoMunicipio();

        Entidad entidadObj = entidadRepository.findById(entidad)
                .orElseThrow(() -> new ResourceNotFoundException("Entidad", "id", entidad));

        if (departamento != null) {
            Departamento departamentoObj = departamentoReposytory.findById(departamento)
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento", "id_departamento", departamento));
            entidadDepartamentoMunicipioObj.setDepartamento(departamentoObj);
        }
        if (municipio != null) {
            Municipio municipioObj = municipioRepository.findById(municipio)
                    .orElseThrow(() -> new ResourceNotFoundException("Municipio", "id_municipio", municipio));
            entidadDepartamentoMunicipioObj.setMunicipio(municipioObj);
        }
        entidadDepartamentoMunicipioObj.setEntidad(entidadObj);
        entidadDepartamentoMunicipioObj.setCorreo_dm(correo);

        if (telefono != null)
            entidadDepartamentoMunicipioObj.setTelefono_dm(telefono);

        List<Formulario> form = formularioRepository.findByEmail(correo);
        if (form.size() > 0) {
            throw new EmailExistsException();
        }

        return entidadDepartamentoMunicipioRepository.save(entidadDepartamentoMunicipioObj);

    }

    @PutMapping("/entidadDepartamentoMunicipio/{id}")
    public ResponseEntity<EntidadDepartamentoMunicipio> edit(
        @PathVariable(value = "id") Long entidadDepartamentoMunicipioId, 
		@RequestParam(required = false) Long departamento, 
        @RequestParam(required = false) Long municipio,
        @RequestParam(required = false) @Email String correo,
        @RequestParam(required = false) String telefono
        ) {
		EntidadDepartamentoMunicipio EntidadDepartamentoMunicipioObj = entidadDepartamentoMunicipioRepository.findById(entidadDepartamentoMunicipioId).orElseThrow( () -> new ResourceNotFoundException("EntidadDepartamentoMunicipio", "id_edm", entidadDepartamentoMunicipioId));
		
		if(departamento != null) {
			Departamento departamentoObj = departamentoReposytory.findById(departamento).orElseThrow( () -> new ResourceNotFoundException("Departamento", "id_departamento", departamento));
			EntidadDepartamentoMunicipioObj.setDepartamento(departamentoObj );
		}
        if (municipio != null) {
            Municipio municipioObj = municipioRepository.findById(municipio)
                    .orElseThrow(() -> new ResourceNotFoundException("Municipio", "id_municipio", municipio));
            EntidadDepartamentoMunicipioObj.setMunicipio(municipioObj);
        }

        Boolean isSame = true;

		if(correo != null){
            isSame = correo.equals(EntidadDepartamentoMunicipioObj.getCorreo_dm());
            EntidadDepartamentoMunicipioObj.setCorreo_dm(correo);
        }
            
        if (telefono != null)
            EntidadDepartamentoMunicipioObj.setTelefono_dm(telefono);
		
		List<Formulario> form = formularioRepository.findByEmail(correo);
        
		if( (correo != null &&  !correo.equals("") && !isSame) && (form.size()>0)) {
			throw new EmailExistsException();
		}
		
		return ResponseEntity.ok(entidadDepartamentoMunicipioRepository.save(EntidadDepartamentoMunicipioObj));
	}

    @DeleteMapping("/entidadDepartamentoMunicipio/{id}")
    public ResponseEntity<EntidadDepartamentoMunicipio> deleteEntidadDepartamentoMunicipio(
            @PathVariable(value = "id") Long entidadDepartamentoMunicipioId) {

        try {
            entidadDepartamentoMunicipioRepository.deleteById(entidadDepartamentoMunicipioId);
            /*
             * Date in = new Date();
             * LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(),
             * ZoneId.systemDefault());
             * int[] ids = auditoriaRepository.findByIds();
             * for(int i =0;i<ids.length;i++) {
             * Optional<Auditoria> audi = auditoriaRepository.findById(ids[i]);
             * Auditoria aud=audi.get();
             * aud.setUpdateDateTime(ldt);
             * aud.setUsuarioModified(systemLoggedInUserAuditorAware.getCurrentAuditor().
             * get());
             * auditoriaRepository.save(aud);
             * }
             */

        } catch (EmptyResultDataAccessException exception) {
            throw new ResourceNotFoundException("Formularios", "id", entidadDepartamentoMunicipioId);
        }

        return ResponseEntity.noContent().build();
    }
}
