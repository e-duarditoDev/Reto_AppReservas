package com.appeventos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appeventos.model.entity.Perfil;
import com.appeventos.model.repository.PerfilRepository;

@Service
public class PerfilServiceImplDataJpaMy8 implements PerfilService{

	@Autowired
	PerfilRepository perfilRepo;
	
	@Override
	public Perfil findByNombre(String nombre) {
		if (nombre.isBlank() || nombre == null)
			throw new RuntimeException("Error: El nombre no puede ser nulo.");
		
		return perfilRepo.findByNombre(nombre);
	}

}
