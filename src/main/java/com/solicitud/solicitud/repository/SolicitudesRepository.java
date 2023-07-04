package com.solicitud.solicitud.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.solicitud.solicitud.models.Solicitudes;

public interface SolicitudesRepository extends JpaRepository<Solicitudes, Long>, JpaSpecificationExecutor<Solicitudes> {
	//Page<Solicitudes> findByEntidadCodigo(Long entidadCodigo, Pageable pageable);
    Page<Solicitudes> findByStatusId(Long statusId, Pageable pageable);
    
    @Query(value = "SELECT * FROM csjinfo_solicitudes  WHERE formulario_id = :formulario order by status_id asc, fecha asc ", nativeQuery = true)
    List<Solicitudes> findAllByOrderByStatusIdAscFechaAsc(Long formulario);

    @Query(value = "SELECT * FROM csjinfo_solicitudes  WHERE formulario_id = :formulario order by id_solicitud desc", nativeQuery = true)
    List<Solicitudes> findAllByOrderByIdSolicitudDesc(Long formulario);
    
    @Query(value = "SELECT * FROM csjinfo_solicitudes s where s.formulario_id = :formulario AND :entidad in (select * from OPENJSON(s.edm) WITH (id INT '$.entidad.codigo')) " , nativeQuery = true)
    List<Solicitudes> findAllByOrderByEntidadCodigoAscIdAsc(Long formulario, Long entidad);
    
    @Query(value = "SELECT * FROM csjinfo_solicitudes s where s.formulario_id = :formulario AND (:entidad is null or :entidad in (select * from OPENJSON(s.edm) WITH (id INT '$.entidad.codigo'))) AND (status_id IN :status) AND (:desde is null or :desde = '' or s.create_date_time >= :desde) AND (:hasta is null or :hasta = '' or s.create_date_time <= :hasta) ", nativeQuery = true)
    List<Solicitudes> findAllByOrderByEntidadCodigoAscIdAsc(Long formulario, Long entidad, List<Long> status, String desde, String hasta);
    
    
    @Query("SELECT s FROM Solicitudes s WHERE s.correo_asignacion = ?1")
    List<Solicitudes> findByCorreo_Asignacion(String correo_asignacion);
    
    @Query("SELECT s FROM Solicitudes s WHERE s.correo_asignacion IS NOT NULL")
    List<Solicitudes> findByCorreo_Asignacion();
    
    @Query("SELECT s FROM Solicitudes s WHERE s.correo_solicitante= ?1")
    List<Solicitudes> findByCorreo_Solicitante(String correo_solicitante);
    
    @Query("SELECT s FROM Solicitudes s WHERE s.updateDateTime between ?1 and ?2")
    public List<Solicitudes> findByAll(LocalDateTime desde, LocalDateTime hasta);
    
    @Query(value = "SELECT * FROM csjinfo_solicitudes  WHERE formulario_id = :formulario and create_date_time between :desde and :hasta ", nativeQuery = true)
    public List<Solicitudes> findByAll(LocalDateTime desde, LocalDateTime hasta, Long formulario);
    
    @Query(value = "SELECT TOP 1 id_solicitud FROM csjinfo_solicitudes ORDER BY id_solicitud DESC ", nativeQuery = true)
    public Integer getLast();
}
