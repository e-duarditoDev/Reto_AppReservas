package com.appeventos.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:5173", "http://localhost:8080", "http://localhost:8082"));
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    config.setAllowedHeaders(List.of("*"));
	    
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}
	
	@Bean
	SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception { //todos los metodos de filtros spring security, request, las sessiones lanzan excepcion
		http 
		.csrf(csrf -> csrf.disable())//csrf es un token de seguridad, encripta info formularios, se deactiva
		.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//cada sesion un objeto (costosa, memoria). Comodo por no tener iniciar sesion, pero costoso, STATELESS = contenido objeto (no tienes session)-> SessionId = null				
		.cors (c -> c.configurationSource(corsConfigurationSource()))//Cross-Origin Resource Sharing (cors) mecanismo seg del NAVEGADOR. para recibir peticiones front-back en origenes distintos (Angular :4200, Spring :8082)
		.authorizeHttpRequests(auth -> auth //rutas autorizadas
				
			//RUTAS PUBLICAS
			.requestMatchers(HttpMethod.POST, "/auth/confirmar-email", "/auth/login/**", "/auth/alta-cliente/**").permitAll()

			//CUALQUIER OTRA PETICION
			//.anyRequest().authenticated()
			//TODOS (PARA PRUEBAS)
			.anyRequest().permitAll() //temporal
//			)
//			
//			.httpBasic(Customizer.withDefaults() //no hace falta porque le pasamos un Raw
			
				);
		
		return http.build();
		
	}
}
