package com.solicitud.solicitud.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.solicitud.solicitud.models.Encuesta;
import com.solicitud.solicitud.repository.EncuestaRepository;

@Service
public class EncuestaService {
	@Autowired
	EncuestaRepository encuestaRepository;
	
	public Optional<Encuesta> findById(Long id){
		return encuestaRepository.findById(id);
	}
	
	public Encuesta save(Encuesta encuesta) {
		return encuestaRepository.save(encuesta);
	}
	
	@SuppressWarnings("serial")
	public List<Encuesta> getEncuestas(Boolean activo, Long formularioId){
		return encuestaRepository.findAll(new Specification<Encuesta>() {
			@Override
			public Predicate toPredicate(Root<Encuesta> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				
				if(activo != null)
					predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("activo"), activo)));
				
				if(formularioId != null)
					predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("formulario"), formularioId)));
				
				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		});
	}
	
	public List<Encuesta> findByActivoTrue() {
		return encuestaRepository.findByActivoTrue();
	}
	
	public void deleteById(Long id) {
		encuestaRepository.deleteById(id);
	}
}
