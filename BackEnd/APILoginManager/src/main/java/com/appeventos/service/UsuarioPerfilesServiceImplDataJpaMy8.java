package com.appeventos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appeventos.model.entity.UsuarioPerfiles;
import com.appeventos.model.repository.UsuarioPerfilesRepository;

@Service
public class UsuarioPerfilesServiceImplDataJpaMy8 implements UsuarioPerfilesService{

	@Autowired
	UsuarioPerfilesRepository usuarioPerfRepo;
	
	@Override
	public UsuarioPerfiles insertarUno(UsuarioPerfiles usuarioPerfil) {
		if (usuarioPerfil == null)
			throw new RuntimeException("Error: El usuario-perfil no puede ser nulo.");
		
		return usuarioPerfRepo.save(usuarioPerfil);
	}


	@Override
	public boolean existsByUsuario_AliasUsuario(String alias) {
		if (alias.isBlank() || alias == null)
			throw new RuntimeException("Error: El userName no puede ser nulo.");
		
		return usuarioPerfRepo.existsByUsuario_AliasUsuario(alias);
	}


	@Override
	public List <UsuarioPerfiles> findByUsuarioAliasUsuario (String alias) {

		if (alias.isBlank() || alias == null)
			throw new RuntimeException("Error: El userName no puede ser nulo.");
		
		List <UsuarioPerfiles> listaUsuarioPerf = usuarioPerfRepo.findByUsuarioAliasUsuario(alias);
		
		return listaUsuarioPerf;
	}
	
}
