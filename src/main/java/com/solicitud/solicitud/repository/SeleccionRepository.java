package com.solicitud.solicitud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.solicitud.solicitud.models.Seleccion;

public interface SeleccionRepository extends JpaRepository<Seleccion, Long> {
	List<Seleccion> findByEncuestaId(Long encuestaId);
	@Query("SELECT COUNT(s) FROM Seleccion s WHERE encuesta.id = ?1")
	Long countByEncuestaId(Long encuestaId);
	@Query(value = "SELECT * FROM csjinfo_seleccion cs INNER JOIN csjinfo_encuestas ce ON cs.encuesta_id = ce.id_encuesta INNER JOIN csjinfo_formularios cf ON cf.id_formulario = ce.formulario_id WHERE ce.formulario_id = :formularioId ", nativeQuery = true)
	List<Seleccion> findAllByFormularioId(Long formularioId);
}
