package com.solicitud.solicitud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.Permiso;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long>{

}
