package com.solicitud.solicitud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio,Long>{
	@Query(value = "SELECT * FROM csjinfo_municipios  where id_municipio NOT in (SELECT distinct m.id_municipio FROM csjinfo_entidad_departamento_municipio edm inner join csjinfo_municipios m on m.id_municipio = edm.municipio_id where edm.entidad_id = ?1) ", nativeQuery = true)
	List<Municipio> findByEntidad_Id(Long entidad_id);
	
	@Query(value = "SELECT * FROM csjinfo_municipios  where id_municipio in (SELECT distinct m.id_municipio FROM csjinfo_entidad_departamento_municipio edm inner join csjinfo_municipios m on m.id_municipio = edm.municipio_id where edm.entidad_id = ?1) ", nativeQuery = true)
	List<Municipio> findBySelectMunicipio(Long entidad_id);
}
