package com.solicitud.solicitud.service;

import java.util.List;
import java.util.Optional;
 
import javax.persistence.EntityNotFoundException;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
 
import com.solicitud.solicitud.models.Status;
import com.solicitud.solicitud.repository.StatusRepository;
 
@Service
public class StatusService {
	@Autowired
	StatusRepository statusRepo;
 
    public List<Status> getAll() {
        return (List<Status>) statusRepo.findAll();
    }
 
    public Status createStatus(Status status) {
        return statusRepo.save(status);
    }
 
    public Status updateStatus(Long Id, Status status) {
    	Status updatedStatus;
        Optional<Status> searchEntity = statusRepo.findById(Id);
        if (searchEntity.isPresent()) {
        	Status sample = searchEntity.get();
            sample.setNombreStatus(status.getNombreStatus());
            updatedStatus= statusRepo.save(sample);
        } else {
            throw new EntityNotFoundException();
        }
        return updatedStatus;
    }
 
    public ResponseEntity<Object> deleteStatus(Long Id) {
        Optional<Status> sampleEntity = statusRepo.findById(Id);
        if (sampleEntity.isPresent()) {
        	Status sample = sampleEntity.get();
        	statusRepo.delete(sample);
        } else {
            throw new EntityNotFoundException();
        }
        return ResponseEntity.ok().build();
    }
}
