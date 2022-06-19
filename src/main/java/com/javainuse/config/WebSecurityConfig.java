package com.javainuse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.javainuse.service.JwtUserDetailsService;

@Configuration
@EnableWebSecurity
//con perPostEnabled se usa para indicar a q metodos puede acceder solo el admin
//Los metodos que no lleven anotación pueden acceder el admin como un generic user
//@preauthorized solo puede acceder el admin
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	  //Devuelve el mensaje de no autorizado
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Configuramos AuthenticationManager para que sepa donde buscar los usuarios
		// Indicamos que tipo de encriptacion vamos a utilizar ( BCryptPasswordEncoder)
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}
    /**
     * Encripta el pasword
     * @return pasword ecriptado
     */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		// We don't need CSRF for this example
		httpSecurity.csrf().disable()
				// Indicamos que los endpong, no requieren estar logeados
				.authorizeRequests().antMatchers("/authenticate", "/register").permitAll().
		
				//el resto va a requerir autenticacion
				anyRequest().authenticated().and().
				
				
				// tenemos que usar la sesión sin estado
				// la sesión no se utilizará para almacenar el estado del usuario
				exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Con esto, hacemos que cualquier otra solicitud que requiera autenticacion pase 
		// por doFilterInternal
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
}