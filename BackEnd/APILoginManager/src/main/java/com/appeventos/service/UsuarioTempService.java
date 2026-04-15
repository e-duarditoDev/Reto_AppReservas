package com.appeventos.service;

import com.appeventos.model.entity.UsuarioTemp;

public interface UsuarioTempService {
	 UsuarioTemp findByEmail (String email);
	 boolean existsByEmail (String email);
	 void insertOne (UsuarioTemp usuarioTemp);
	 UsuarioTemp findByToken (String token);
	 void deleteOne (Long id);
	 
}
