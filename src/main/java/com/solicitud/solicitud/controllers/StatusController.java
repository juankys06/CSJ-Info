package com.solicitud.solicitud.controllers;

import java.util.List;

import javax.validation.Valid;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
import com.solicitud.solicitud.models.Status;
import com.solicitud.solicitud.service.StatusService;
import com.solicitud.solicitud.repository.StatusRepository;
import com.solicitud.solicitud.exception.ResourceNotFoundException;
 
@RestController
public class StatusController {
	@Autowired
	StatusService sampleService;
	
	@Autowired
	StatusRepository statusRepositorio;
 
    @RequestMapping(value = "/status")
    public List<Status> sample() {
        return sampleService.getAll();
    }
    
    @GetMapping("/status/{id}")
    public Status getStatusById(@PathVariable(value = "id") Long statusId) {
        return statusRepositorio.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "id", statusId));
    }
 
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public Status createSample(@Valid @RequestBody Status sampleEntity) {
        return sampleService.createStatus(sampleEntity);
    }
 
    @RequestMapping(value = "/status/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteSample(@PathVariable(value = "id") Long id) {
        return sampleService.deleteStatus(id);
    }
 
    @RequestMapping(value = "/status/{id}", method = RequestMethod.PUT)
    public Status updateSample(@PathVariable(value = "id") Long id,
            @Valid @RequestBody Status sampleEntity) {
        return sampleService.updateStatus(id, sampleEntity);
    }
}
