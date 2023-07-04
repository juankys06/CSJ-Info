package com.solicitud.solicitud.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.Archivos;

@Repository
public interface ArchivosRepository  extends JpaRepository<Archivos, Long> {
	List<Archivos> findBySolicitudesId(Long solicitudesId);
}
