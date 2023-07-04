package com.solicitud.solicitud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.solicitud.solicitud.models.Respuesta;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long>, JpaSpecificationExecutor<Respuesta> {
	Respuesta findByTexto(String texto);
}