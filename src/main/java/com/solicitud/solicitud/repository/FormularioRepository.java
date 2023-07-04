package com.solicitud.solicitud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.Formulario;

@Repository
public interface FormularioRepository extends JpaRepository<Formulario, Long>{
    Formulario findByActivoTrue();
    
    Formulario findByUrl(String Url);
	
	List<Formulario> findByEmail(String Email);
}
