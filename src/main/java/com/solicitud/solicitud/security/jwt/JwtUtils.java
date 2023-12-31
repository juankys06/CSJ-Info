package com.solicitud.solicitud.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.solicitud.solicitud.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${solicitud.app.jwtSecret}")
	private String jwtSecret;

	@Value("${solicitud.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Firma JWT inválida: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Token JWT inválido: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("El token JWT ha caducado: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("El token JWT no es compatible: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT afirma que la cadena está vacía: {}", e.getMessage());
		}

		return false;
	}
}
