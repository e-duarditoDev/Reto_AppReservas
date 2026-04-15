package com.appeventos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * CONFIGURACION DE FILTROS Y/O RUTAS EN FUNCION QUIEN ENTRE
 * 		- cliente hace peticion y filtro aplica config
 * 		- 
 */
	
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor //para no tener que construir jwt
public class SecurityConfig {

	@Bean
	PasswordEncoder passdwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception { //todos los metodos de filtros spring security, request, las sessiones lanzan excepcion
		http 
		.csrf(csrf -> csrf.disable())//csrf es un token de seguridad, encripta info formularios, se deactiva
		.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//cada sesion un objeto (costosa, memoria). Comodo por no tener iniciar sesion, pero costoso, STATELESS = contenido objeto (no tienes session)-> SessionId = null				
		.cors (c -> {})//Cross-Origin Resource Sharing (cors) mecanismo seg. del NAVEGADOR. si puede peticiones front-back en origenes distintos (Angular :4200, Spring :8080)
		.authorizeHttpRequests(auth -> auth //rutas autorizadas
				
			//RUTAS PUBLICAS
			.requestMatchers(HttpMethod.POST, "/auth/confirmar-email", "/auth/login", "/auth/alta-cliente/**").permitAll()

			//CUALQUIER OTRA PETICION
			.anyRequest().authenticated()
//			)
//			
//			.httpBasic(Customizer.withDefaults() //no hace falta porque le pasamos un Raw
			
				);
		
		return http.build();
		
	}
}
