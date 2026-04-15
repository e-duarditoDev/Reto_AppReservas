package com.appeventos.service;

import java.util.List;

import com.appeventos.model.entity.UsuarioPerfiles;

public interface UsuarioPerfilesService {
	UsuarioPerfiles insertarUno (UsuarioPerfiles usuarioPerfil);
	boolean existsByUsuario_AliasUsuario (String alias);
	List <UsuarioPerfiles> findByUsuarioAliasUsuario (String alias);
}
