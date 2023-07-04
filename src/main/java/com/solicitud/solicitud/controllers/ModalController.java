package com.solicitud.solicitud.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.Modal;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.ModalRepository;
import com.solicitud.solicitud.exception.ResourceNotFoundException;

@RestController
public class ModalController {
    @Autowired
    private FormularioRepository formularioRepository;

    @Autowired
    ModalRepository modalRepository;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/modals")
    public List<Modal> sample() {
        return modalRepository.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modals/{id}")
    public Modal getModalById(@PathVariable(value = "id") Long modalId) {
        return modalRepository.findById(modalId)
                .orElseThrow(() -> new ResourceNotFoundException("Modal", "id", modalId));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/modals", method = RequestMethod.POST)
    public Modal createModal(@RequestBody Map<String, Object> payload) {

        Modal modal = new Modal();
        String titulo = (String) payload.get("titulo");
        String contenido = (String) payload.get("contenido");
        Boolean activo = (Boolean) payload.get("activo");
        Long formulario_id = ((Number) payload.get("formulario_id")).longValue();

        Formulario formulario = formularioRepository.findById(formulario_id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Formulario",
                        "id",
                        formulario_id));

        modal.setTitulo(titulo);
        modal.setContenido(contenido);
        modal.setActivo(activo);
        modal.setFormulario(formulario);

        formulario.setModal(modal);

        return modalRepository.save(modal);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/modals/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteSample(@PathVariable(value = "id") Long id) {
        
        Modal modal = modalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Modal",
                        "id",
                        id));
        Formulario formulario = modal.getFormulario();
        formulario.setModal(null);
        formularioRepository.save(formulario);
        modalRepository.deleteById(id);

        return ResponseEntity.ok("Modal borrado");
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/modals/{id}", method = RequestMethod.PUT)
    public Modal updateModal(
            @PathVariable(value = "id") Long id,
            @RequestBody Map<String, Object> payload) {

        Modal modal = modalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Formulario",
                        "id",
                        id));
        String titulo = (String) payload.get("titulo");
        String contenido = (String) payload.get("contenido");
        Boolean activo = (Boolean) payload.get("activo");
        Long formulario_id = ((Number) payload.get("formulario_id")).longValue();

        Formulario formulario = formularioRepository.findById(formulario_id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Formulario",
                        "id",
                        formulario_id));

        modal.setTitulo(titulo);
        modal.setContenido(contenido);
        modal.setActivo(activo);
        modal.setFormulario(formulario);

        return modalRepository.save(modal);
    }
}
