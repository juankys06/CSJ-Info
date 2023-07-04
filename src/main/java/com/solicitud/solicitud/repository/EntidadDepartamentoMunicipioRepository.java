package com.solicitud.solicitud.repository;

import java.util.List;

import javax.validation.constraints.Email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;

@Repository
public interface EntidadDepartamentoMunicipioRepository extends JpaRepository<EntidadDepartamentoMunicipio, Long>{
	@Query(value = "SELECT * FROM csjinfo_entidad_departamento_municipio  where entidad_id = ?1 ", nativeQuery = true)
	List<EntidadDepartamentoMunicipio> findBySelect(Long entidad_id);
	
	@Query(value = "SELECT * FROM csjinfo_entidad_departamento_municipio  where correo_dm = ?1 ", nativeQuery = true)
    List<EntidadDepartamentoMunicipio> findByEmail_dm(String correo_dm);
    
    @Query(value = "SELECT * FROM csjinfo_entidad_departamento_municipio  where correo_dm = ?1 and municipio_id is not null", nativeQuery = true)
    List<EntidadDepartamentoMunicipio> findByEmail_dmMunicipio(String correo_dm);
}
