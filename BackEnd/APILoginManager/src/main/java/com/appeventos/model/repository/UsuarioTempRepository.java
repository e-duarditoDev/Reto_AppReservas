package com.appeventos.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appeventos.model.entity.UsuarioTemp;


public interface UsuarioTempRepository extends JpaRepository<UsuarioTemp, Long>{

	UsuarioTemp findByEmail(String email);
	boolean existsByEmail (String email);
	UsuarioTemp findByToken (String token);

}
