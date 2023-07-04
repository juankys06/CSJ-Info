package com.solicitud.solicitud.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solicitud.solicitud.models.AppUser;
import com.solicitud.solicitud.repository.AppUserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	AppUserRepository appUserRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String emailaddress) throws UsernameNotFoundException {
		AppUser user = appUserRepository.findByEmailaddress(emailaddress)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + emailaddress));

		return UserDetailsImpl.build(user);
	}
	
	@Transactional
	public UserDetails loadUsersByUsername(String screenname) throws UsernameNotFoundException {
		AppUser user = appUserRepository.findByScreenname(screenname)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + screenname));

		return UserDetailsImpl.build(user);
	}

}
