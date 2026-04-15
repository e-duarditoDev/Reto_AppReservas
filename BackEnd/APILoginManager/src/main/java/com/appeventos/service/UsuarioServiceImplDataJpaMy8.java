package com.appeventos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appeventos.model.entity.Usuario;
import com.appeventos.model.repository.UsuarioRepository;

@Service
public class UsuarioServiceImplDataJpaMy8 implements UsuarioService{

	@Autowired
	UsuarioRepository usuRepo;
	
	@Override
	public boolean existsByAliasUsuario(String alias) {
		if (alias == null)
			throw new RuntimeException("Error: El userName no puede ser nulo.");

		return usuRepo.existsByAliasUsuario(alias);
	}

	@Override
	public boolean existsByEmail(String email) {
		if (email.isBlank() || email == null)
			throw new RuntimeException("Error: No se obtenido el parametro email.");
		
		return usuRepo.existsByEmail(email);
	}

	@Override
	public Usuario insertarUno(Usuario usuario) {
		if (usuario == null)
			throw new RuntimeException("Error: El usuario no puede ser nulo.");
		
		return usuRepo.save(usuario);
	}

	@Override
	public Usuario findByEmail(String email) {
		if (email.isBlank() || email == null)
			throw new RuntimeException("Proporciona un email válido.");
		
		return usuRepo.findByEmail(email);
	}
}
