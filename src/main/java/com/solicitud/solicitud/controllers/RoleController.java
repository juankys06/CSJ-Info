package com.solicitud.solicitud.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.models.AppUser;
import com.solicitud.solicitud.models.Auditoria;
import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.Permiso;
import com.solicitud.solicitud.models.Role;
import com.solicitud.solicitud.models.User;
import com.solicitud.solicitud.payload.request.RolesRequest;
import com.solicitud.solicitud.payload.request.UserRoleRequest;
import com.solicitud.solicitud.repository.AppUserRepository;
import com.solicitud.solicitud.repository.AuditoriaRepository;
import com.solicitud.solicitud.repository.PermisoRepository;
import com.solicitud.solicitud.repository.RoleRepository;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.UserRepository;
import com.solicitud.solicitud.security.SystemLoggedInUserAuditorAware;

@RestController
public class RoleController {
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	FormularioRepository formularioRepository;
	
	@Autowired
	PermisoRepository permisoRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AppUserRepository appUserRepository;
	
	@Autowired
	AuditoriaRepository auditoriaRepository;
	
	@Autowired
	SystemLoggedInUserAuditorAware systemLoggedInUserAuditorAware;
	
	@GetMapping("/roles")
	public List<Role> getRoles(){
		return roleRepository.findAll();
	}
	
	@GetMapping("/roles/{id}")
	public Role getRole(@PathVariable(value = "id") Long roleId) {
		return roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
	}
	
	@GetMapping("permisos/{id}")
	public Permiso getPermiso(@PathVariable(value = "id") Long permisoId) {
		return permisoRepository.findById(permisoId).orElseThrow(() -> new ResourceNotFoundException("Permiso", "id", permisoId));
	}
	
	@GetMapping("/permisos")
	public List<Permiso> getPermisos(){
		return permisoRepository.findAll();
	}
	
	@PostMapping("/roles")
	public Role createRole(@Valid @RequestBody RolesRequest request) {
		
		Role role = new Role();
		
		role.setDescripcion(request.getDescripcion());
		role = roleRepository.save(role);
		
		if(!request.getPermisos().isEmpty())
			role.setPermisos(permisoRepository.findAllById(request.getPermisos()));
		
		if(!request.getFormularios().isEmpty())
			role.setFormularios(formularioRepository.findAllById(request.getFormularios()));
		
		return roleRepository.save(role);
	}
	
	@PostMapping("/permisos")
	public Permiso createPermiso(@Valid @RequestBody Permiso permiso) {
		return permisoRepository.save(permiso);
	}
	
	@PutMapping("/roles/{id}")
	public Role editRole(@PathVariable(value = "id") Long roleId, @Valid @RequestBody RolesRequest request) {
		Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
		
		if(request.getDescripcion() != null)
			role.setDescripcion(request.getDescripcion());
		
		if(!request.getPermisos().isEmpty()) {
			role.clearPermisos();
			for(Permiso permiso : permisoRepository.findAllById(request.getPermisos()))
				role.addPermiso(permiso);
		}
		
		if(!request.getFormularios().isEmpty()) {
			role.clearFormulario();
			for(Formulario formulario : formularioRepository.findAllById(request.getFormularios()))
				role.addFormulario(formulario);
		}
		
		return roleRepository.save(role);
	}
	
	@PutMapping("/permisos/{id}")
	public Permiso editPermiso(@PathVariable(value = "id") Long permisoId, @Valid @RequestBody Permiso modifiedPermiso) {
		Permiso permiso = permisoRepository.findById(permisoId).orElseThrow(() -> new ResourceNotFoundException("Permiso", "id", permisoId));
		
		if(modifiedPermiso.getDescripcion() != null)
			permiso.setDescripcion(modifiedPermiso.getDescripcion());
		
		return permisoRepository.save(permiso);
	}
	
	@DeleteMapping("/roles/{id}")
	public ResponseEntity<Role> deleteRole(@PathVariable(value = "id") Long roleId){
		Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Rol", "id", roleId));
		role.clearPermisos();
		role.clearFormulario();
		roleRepository.delete(role);	
		Date in = new Date();
		LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
		int[] ids = auditoriaRepository.findByIds();
		for(int i =0;i<ids.length;i++) {
			Optional<Auditoria> audi = auditoriaRepository.findById(ids[i]);
			Auditoria aud=audi.get();
			aud.setUpdateDateTime(ldt);
			aud.setUsuarioModified(systemLoggedInUserAuditorAware.getCurrentAuditor().get());
			auditoriaRepository.save(aud);
		}
		
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/permisos/{id}")
	public ResponseEntity<Permiso> deletePermiso(@PathVariable(value = "id") Long permisoId){
		try {
			permisoRepository.deleteById(permisoId);
		}catch(EmptyResultDataAccessException exception) {
			throw new ResourceNotFoundException("Permiso", "id", permisoId);
		}
		
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/users/{id}/roles")
	public ResponseEntity<User> addRolesToUser(@PathVariable(value = "id") Long userId, @Valid @RequestBody UserRoleRequest request){
		AppUser user = appUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", userId));
		
		if( ! request.getRoles().isEmpty() ) {
			user.clearRoles();
			for(Role role : roleRepository.findAllById(request.getRoles()))
				user.addRole(role);
		}
		
		appUserRepository.save(user);
		
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasAuthority('Administrador')")
	@GetMapping("/ousers") //-- Outside Users
	public Page<User> getLiferayUsers(@RequestParam(required = false) String email, Pageable pageable){
		if(email != null)
			return userRepository.findAllByEmailaddress(email, pageable);
		else
			return userRepository.findAll(pageable);
	}
	
	@PreAuthorize("hasAuthority('Administrador')")
	@GetMapping("/users")
	public Page<AppUser> getUsers(@RequestParam(required = false) String email,
								  Pageable pageable){
		if(email != null)
			return appUserRepository.findByEmail(email, pageable);
		else
			return appUserRepository.findAll(pageable);
	}
	
	@GetMapping("/users/{id}")
	public AppUser getAppUserId(@PathVariable(value = "id") Long AppUserId) {
		return appUserRepository.findById(AppUserId).orElseThrow(() -> new ResourceNotFoundException("AppUser", "id", AppUserId));
	}
	
	
	@PostMapping("/users")
	public ResponseEntity<String> importUser(@RequestParam List<Long> usuarios){
		
		if(usuarios.isEmpty())
			return ResponseEntity.badRequest().build();
		
		List<User> users = userRepository.findAllById(usuarios);
		List<AppUser> appUsers = new ArrayList<AppUser>(); 
				
		for(int i = 0 ; i < users.size() ; i++)
			appUsers.add(new AppUser(users.get(i)));
		
		appUserRepository.saveAll(appUsers);
		
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<AppUser> deleteUser(@PathVariable(name = "id") Long userId){
		appUserRepository.deleteById(userId);
		
		return ResponseEntity.noContent().build();
	} 
}


