package com.solicitud.solicitud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solicitud.solicitud.models.Pregunta;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
	
}
