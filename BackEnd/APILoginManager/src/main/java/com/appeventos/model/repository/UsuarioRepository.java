package com.appeventos.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appeventos.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
	boolean existsByAliasUsuario(String alias);
	Usuario findByEmail (String email);
	boolean existsByEmail (String email);
}
