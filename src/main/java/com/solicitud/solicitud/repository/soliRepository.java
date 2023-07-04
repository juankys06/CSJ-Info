package com.solicitud.solicitud.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.solicitud.solicitud.models.Solicitudes;

public interface soliRepository extends JpaRepository<Solicitudes, Long>{
	
}
