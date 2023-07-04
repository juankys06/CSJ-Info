package com.solicitud.solicitud.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityListeners;
import javax.persistence.EntityNotFoundException;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.solicitud.solicitud.models.Entidad;
import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;
import com.solicitud.solicitud.exception.EmailExistsException;
import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.models.Auditoria;
import com.solicitud.solicitud.repository.EntidadRepository;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.AuditoriaRepository;
import com.solicitud.solicitud.repository.EntidadDepartamentoMunicipioRepository;
import com.solicitud.solicitud.security.SystemLoggedInUserAuditorAware;

@Service
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class EntidadService {
    @Autowired
    AuditoriaRepository auditoriaRepository;

    @Autowired
    EntidadDepartamentoMunicipioRepository entidadDepartamentoMunicipioRepository;

    @Autowired
    EntidadRepository entidadRepo;
    @Autowired
    FormularioRepository formularioRepo;

    @Autowired
    SystemLoggedInUserAuditorAware systemLoggedInUserAuditorAware;

    public Optional<Entidad> findById(Long id) {
        return entidadRepo.findById(id);
    }

    public List<Entidad> getAll() {
        return (List<Entidad>) entidadRepo.findAll();
    }

    public Entidad createEntidad(Entidad entidad) {

        if (entidad.getEntidadPadre() != null) {
            Entidad entidadObj = entidadRepo.findById(entidad.getEntidadPadre().getCodigo())
                    .orElseThrow(() -> new ResourceNotFoundException("Entidad", "id", entidad));
            entidad.setEntidadPadre(entidadObj);
        }
        entidadRepo.save(entidad);

        if (entidad.getTipo().compareTo("Nacional") == 0) {
            Entidad entidadObj = entidadRepo.findById(entidad.getCodigo())
                    .orElseThrow(() -> new ResourceNotFoundException("Entidad", "id", entidad));
            EntidadDepartamentoMunicipio entidadDepartamentoMunicipioObj = new EntidadDepartamentoMunicipio();

            entidadDepartamentoMunicipioObj.setEntidad(entidadObj);

            entidadDepartamentoMunicipioRepository.save(entidadDepartamentoMunicipioObj);
        }
        return entidad;
    }

    public Entidad updateEntidad(Long Id, Entidad entidad) {

        Entidad updatedStatus;
        Optional<Entidad> searchEntity = entidadRepo.findById(Id);

        if (searchEntity.isPresent()) {

            Entidad sample = searchEntity.get();

            sample.setNombre(entidad.getNombre());
            sample.setTipo(entidad.getTipo());

            List<Formulario> form = formularioRepo.findByEmail(entidad.getEmail());

            if ((entidad.getEmail() != null && !entidad.getEmail().equals(sample.getEmail())) &&
                    (form.size() > 0)) {
                throw new EmailExistsException();
            }

            sample.setEmail(entidad.getEmail());
            sample.setTelefono(entidad.getTelefono());
            updatedStatus = entidadRepo.save(sample);

        } else {
            throw new EntityNotFoundException();
        }
        return updatedStatus;
    }

    public ResponseEntity<Object> deleteEntidad(Long Id) {

        Optional<Entidad> Entity = entidadRepo.findById(Id);
        if (Entity.isPresent()) {
            Entidad entidad = Entity.get();
            entidadRepo.delete(entidad);
            Date in = new Date();
            LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
            int[] ids = auditoriaRepository.findByIds();
            for (int i = 0; i < ids.length; i++) {
                Optional<Auditoria> audi = auditoriaRepository.findById(ids[i]);
                Auditoria aud = audi.get();
                aud.setUpdateDateTime(ldt);
                aud.setUsuarioModified(systemLoggedInUserAuditorAware.getCurrentAuditor().get());
                auditoriaRepository.save(aud);
            }

        } else {
            throw new EntityNotFoundException();
        }
        return ResponseEntity.ok().build();
    }
}
