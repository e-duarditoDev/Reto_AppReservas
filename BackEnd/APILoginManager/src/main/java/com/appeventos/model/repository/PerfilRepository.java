package com.appeventos.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appeventos.model.entity.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
	Perfil findByNombre (String nombre);
}
