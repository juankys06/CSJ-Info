package com.solicitud.solicitud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.RespuestaUsuario;



@Repository
public interface RespuestaUsuarioRepository extends JpaRepository<RespuestaUsuario, Long> {
	
	@Query("SELECT s FROM RespuestaUsuario s WHERE s.id_user = ?1 ")
	List<RespuestaUsuario> findByUser(Long user_id);
}
