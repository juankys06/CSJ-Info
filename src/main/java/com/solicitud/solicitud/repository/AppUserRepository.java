package com.solicitud.solicitud.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.solicitud.solicitud.models.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
	@Query("SELECT au FROM AppUser au WHERE au.user.screenname = ?1")
	Optional<AppUser> findByScreenname(String screenname);
	@Query("SELECT au FROM AppUser au WHERE au.user.emailaddress = ?1")
	Optional<AppUser> findByEmailaddress(String emailaddress);
	//@Query("SELECT au FROM AppUser au INNER JOIN User u ON u.userid = au.id WHERE u.emailaddress = ?1")
	@Query("SELECT au FROM AppUser au WHERE au.user.emailaddress = ?1")
	Page<AppUser> findByEmail(String email, Pageable pageable);
}
