package com.appeventos.service;

import com.appeventos.model.entity.Usuario;

public interface UsuarioService {
	
	boolean existsByAliasUsuario(String alias);
	Usuario findByEmail (String email);
	boolean existsByEmail (String email);
	Usuario insertarUno (Usuario usuario);
}
