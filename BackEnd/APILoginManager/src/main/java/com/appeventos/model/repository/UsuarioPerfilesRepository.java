package com.appeventos.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appeventos.model.entity.UsuarioPerfiles;
import com.appeventos.model.entity.UsuarioPerfilesId;

public interface UsuarioPerfilesRepository extends JpaRepository<UsuarioPerfiles, UsuarioPerfilesId>{
	boolean existsByUsuario_AliasUsuario (String alias);
	List <UsuarioPerfiles> findByUsuarioAliasUsuario (String userName);
}
