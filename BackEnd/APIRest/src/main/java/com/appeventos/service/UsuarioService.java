package com.appeventos.service;

import com.appeventos.model.entity.Usuario;

public interface UsuarioService {

    Usuario findByUsername(String username);
}
