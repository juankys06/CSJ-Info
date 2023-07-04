package com.solicitud.solicitud.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.solicitud.solicitud.models.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String email;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;
	private Collection<? extends GrantedAuthority> authoritiesRol;

	public UserDetailsImpl(Long id, String username, String email, String password,
			Collection<? extends GrantedAuthority> authorities,Collection<? extends GrantedAuthority> authoritiesRol) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.authoritiesRol= authoritiesRol;
	}

	public static UserDetailsImpl build(AppUser user) {
		List<GrantedAuthority> authorities = user.getPermisos().stream()
				.map(permiso -> new SimpleGrantedAuthority(permiso.getDescripcion()))
				.collect(Collectors.toList());
		
		List<GrantedAuthority> authoritiesRol = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getId().toString()))
				.collect(Collectors.toList());

		return new UserDetailsImpl(
				user.getUser().getUserid(), 
				user.getUser().getScreenname(), 
				user.getUser().getEmailaddress(),
				user.getUser().getPassword_(), 
				authorities,
				authoritiesRol);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public Collection<? extends GrantedAuthority> getAuthoritiesRol() {
		return authoritiesRol;
	}


	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}
