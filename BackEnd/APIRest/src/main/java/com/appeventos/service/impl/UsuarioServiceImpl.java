package com.appeventos.service.impl;

import org.springframework.stereotype.Service;

import com.appeventos.model.entity.Usuario;
import com.appeventos.model.repository.UsuarioRepository;
import com.appeventos.service.UsuarioService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + username));
    }
}
