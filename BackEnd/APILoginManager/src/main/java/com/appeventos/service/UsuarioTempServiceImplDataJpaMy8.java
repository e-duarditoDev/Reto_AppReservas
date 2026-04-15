package com.appeventos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appeventos.model.entity.UsuarioTemp;
import com.appeventos.model.repository.UsuarioTempRepository;

@Service
public class UsuarioTempServiceImplDataJpaMy8 implements UsuarioTempService{

	@Autowired
	UsuarioTempRepository usuarioTempRepo;
	
	@Override
	public UsuarioTemp findByEmail(String email) {

		if (email == null || email.isBlank())
			throw new RuntimeException("Error: El email no puede ser nulo.");

		return usuarioTempRepo.findByEmail(email);
	}

	@Override
	public boolean existsByEmail(String email) {

		if (email == null || email.isBlank())
			throw new RuntimeException("Error: El email no puede ser nulo.");

		return usuarioTempRepo.existsByEmail(email);
	}

	@Override
	public void insertOne(UsuarioTemp usuarioTemp) {
		if (usuarioTemp == null)
			throw new RuntimeException("Error. No se ha podido completar la operacion.");
		usuarioTempRepo.save(usuarioTemp);
	}

	@Override
	public UsuarioTemp findByToken(String token) {
		if (token == null || token.isEmpty())
			throw new RuntimeException("Error: token invalido.");
		return usuarioTempRepo.findByToken(token);
	}

	@Override
	public void deleteOne(Long id) {
		if (id == null)
			throw new RuntimeException("Error: No se ha proporcionado un id.");
		
		
	    if (!usuarioTempRepo.existsById(id)) {
	        throw new RuntimeException("Error: no existe usuarioTemp con id " + id);
	    }
	    
		usuarioTempRepo.deleteById(id);
		
	}
	
	

}
