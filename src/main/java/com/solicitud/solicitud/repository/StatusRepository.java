package com.solicitud.solicitud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solicitud.solicitud.models.Status;

public interface StatusRepository extends JpaRepository<Status, Long> {

}
