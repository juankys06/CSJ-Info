package com.solicitud.solicitud.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solicitud.solicitud.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByScreenname(String screenname);
	Optional<User> findByEmailaddress(String emailaddress);
	Page<User> findAllByEmailaddress(String emailaddress, Pageable pageable);

	Boolean existsByScreenname(String screenname);

	Boolean existsByEmailaddress(String emailaddress);
	Page<User> findAll(Pageable pageable);
}
