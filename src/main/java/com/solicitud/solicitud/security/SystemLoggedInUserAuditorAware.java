package com.solicitud.solicitud.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Optional;

import com.solicitud.solicitud.security.services.UserDetailsImpl;;;

@Component
@EnableJpaAuditing
public class SystemLoggedInUserAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
		/*
		 * Object principal =
		 * SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 * UserDetailsImpl userDetails = null; String userName = "null"; if (principal
		 * instanceof UserDetails) { userDetails = (UserDetailsImpl) principal; userName
		 * = userDetails.getEmail(); return Optional.of(userName); }else { return
		 * Optional.of(userName); }
		 */
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object userPrincipal = (Object) authentication.getPrincipal();

        UserDetailsImpl userDetails = null;
    	String userName = "null";
    	if (userPrincipal instanceof UserDetails) {
    	  userDetails = (UserDetailsImpl) userPrincipal;
    	   userName = userDetails.getEmail();
          return Optional.of(userName);
    	}else {
    	return Optional.of(userName);
    	}
    }
}