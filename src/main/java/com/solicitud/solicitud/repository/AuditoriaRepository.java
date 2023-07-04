package com.solicitud.solicitud.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import com.solicitud.solicitud.models.Auditoria;


public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer>, JpaSpecificationExecutor<Auditoria> {
	Auditoria findByRev(Integer rev);
	

	@Query("SELECT s FROM Auditoria s WHERE s.codigo = ?1  order  by s.id desc")
	List<Auditoria> findByCodigo(Long Codigo);
	/*List<Auditoria> findByIdformulario(Long IdFormulario);
	List<Auditoria> findByIdSolicitud(Long IdSolicitud);
	List<Auditoria> findById_Encuesta(Long Id_Encuesta);
	
	List<Auditoria> findById_Permiso(Long Id_Permiso);*/
	
	@Query("SELECT s FROM Auditoria s WHERE s.id_formulario = ?1  order  by s.id desc")
    List<Auditoria> findById_Formulario(Long id);
	
	@Query("SELECT s FROM Auditoria s WHERE s.id_solicitud = ?1  order  by s.id desc")
    List<Auditoria> findById_Solicitud(Long id);
	
	@Query("SELECT s FROM Auditoria s WHERE s.id_roles = ?1  order  by s.id desc")
    List<Auditoria> findById_Roles(Long id);
	
	@Query("SELECT s FROM Auditoria s WHERE s.id_permiso = ?1  order  by s.id desc")
    List<Auditoria> findById_Permiso(Long id);
	
	@Query("SELECT s FROM Auditoria s WHERE s.id_encuesta = ?1  order  by s.id desc")
    List<Auditoria> findById_Encuesta(Long id);
	
	@Query("SELECT s FROM Auditoria s WHERE s.id_user = ?1  order  by s.id desc")
    List<Auditoria> findById_User(Long id);
	
	@Query("SELECT s FROM Auditoria s WHERE s.correo_solicitante = ?1 ")
    List<Auditoria> findByCorreo_Solicitante(String correo_solicitante);
	
	@Query("SELECT s FROM Auditoria s WHERE s.usuarioModified = ?1 ")
    List<Auditoria> findByUsuario(String correo_usuario);
	
	
	public Page<Auditoria> findAllByOrderByRevDesc(Pageable pageable);
	
	@Query("SELECT a FROM Auditoria a where a.rev in (select MAX(rev) from Auditoria group by id_solicitud) ")
    public List<Auditoria> findByAll();
	
	@Query("SELECT max(rev) from Auditoria")
	public Integer findByMaxRev();
	
	@Query("SELECT a.id FROM Auditoria a where a.rev in (select MAX(rev) from Auditoria) ")
    public int[] findByIds();
}
