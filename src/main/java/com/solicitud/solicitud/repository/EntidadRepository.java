package com.solicitud.solicitud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.Entidad;

@Repository
public interface EntidadRepository extends JpaRepository<Entidad, Long> {
	@Query(value = "SELECT * FROM csjinfo_entidad  where entidad_codigo IS NULL order by nombre asc ", nativeQuery = true)
	List<Entidad> findByEntidaN();
	
	List<Entidad> findByEmail(String email);
}
