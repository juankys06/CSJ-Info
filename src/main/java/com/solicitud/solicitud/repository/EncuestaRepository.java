package com.solicitud.solicitud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.solicitud.solicitud.models.Encuesta;

public interface EncuestaRepository extends JpaRepository<Encuesta, Long>, JpaSpecificationExecutor<Encuesta> {
	List<Encuesta> findByActivoTrue();
	
	@Query(value = "select count(*)\r\n" + 
			"  from csjinfo_encuestas e join csjinfo_encuesta_pregunta ep on e.id_encuesta=ep.encuesta_id where e.id_encuesta = :encuesta ", nativeQuery = true)
	public Integer getCountPre(long encuesta);
	 
	@Query(value = "select count(*)\r\n" + 
			"  from (\r\n" + 
			"  SELECT distinct(se.id)\r\n" + 
			"  FROM csjinfo_solicitudes s join csjinfo_formularios f on f.id_formulario = s.formulario_id\r\n" + 
			"  join  csjinfo_encuestas en on en.formulario_id = f.id_formulario \r\n" + 
			"  join csjinfo_seleccion se on en.id_encuesta = se.encuesta_id   where s.correo_solicitante = :email \r\n" + 
			"  and se.user_agent = :email and en.id_encuesta=:encuesta) respuestas  ", nativeQuery = true)
	public Integer getCountRes(long encuesta, String email);
	 
	@Query(value = "SELECT count(*) \r\n" + 
			"  FROM csjinfo_solicitudes s join csjinfo_formularios f on f.id_formulario = s.formulario_id\r\n" + 
			"  join  csjinfo_encuestas en on en.formulario_id = f.id_formulario where s.correo_solicitante = :email\r\n" + 
			"  and en.id_encuesta = :encuesta", nativeQuery = true)
	public Integer getCountSol(long encuesta, String email);
}
