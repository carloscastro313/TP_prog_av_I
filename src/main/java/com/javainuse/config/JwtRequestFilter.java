package com.javainuse.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.javainuse.service.JwtUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Value("${jwt.secret}")
	private String secret;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;
		// El JWT Token viene de la siguiente forma "Bearer token". Le removemos la palabra Bearer asi 
		// nos queda solo el Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				//Obtenemos del token el nombre de usuario
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		// una vez que obtenemos el token lo validamos.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			// si el token es válido, configure Spring Security para configurarlo manualmente
			// la autenticamcion
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				final JwtParser jwtParser = Jwts.parser().setSigningKey(secret);
				final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwtToken);
				final Claims claims = claimsJws.getBody();
				logger.info("roles "+claims.get("AUTHORITIES_KEY").toString());
				final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
				String[] auths =claims.get("AUTHORITIES_KEY").toString().split(",");
				logger.info("tiene el rol "+auths.length);
				for(String auth:auths) {
					authorities.add(new SimpleGrantedAuthority(auth));
					logger.info("tiene el rol "+auth);
					
				}
				
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, authorities);
				
				//usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// Después de configurar la Autenticación en el contexto, especificamos
				// Que el usuario actual esté autenticado. Así pasa el
				// Spring Security Configurations fue existoso.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

}