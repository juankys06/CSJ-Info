package com.solicitud.solicitud.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.solicitud.solicitud.models.Replica;

public interface ReplicaRepository  extends JpaRepository<Replica, Long> {
	Page<Replica> findBySolicitudesId(Long solicitudesId, Pageable pageable);
}
