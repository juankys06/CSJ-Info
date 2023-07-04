package com.solicitud.solicitud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solicitud.solicitud.mail.DeniedEmail;

public interface DeniedEmailRepository extends JpaRepository<DeniedEmail, Long> {
	boolean existsByEmail(String email);
}