package com.solicitud.solicitud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.Departamento;

@Repository
public interface DepartamentoReposytory extends JpaRepository<Departamento,Long>{
	
	@Query(value = "SELECT * FROM csjinfo_departamento  where id_departamento NOT in (SELECT distinct d.id_departamento FROM csjinfo_entidad_departamento_municipio edm inner join csjinfo_departamento d on d.id_departamento = edm.departamento_id where edm.entidad_id = ?1) ", nativeQuery = true)
	List<Departamento> findByEntidad_Id(Long entidad_id);
	
	@Query(value = "SELECT * FROM csjinfo_departamento  where id_departamento in (SELECT distinct d.id_departamento FROM csjinfo_entidad_departamento_municipio edm inner join csjinfo_departamento d on d.id_departamento = edm.departamento_id where edm.entidad_id = ?1) ", nativeQuery = true)
	List<Departamento> findBySelectDepartamento(Long entidad_id);
}
