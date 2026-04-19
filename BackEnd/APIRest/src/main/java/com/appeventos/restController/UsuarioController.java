package com.appeventos.restController;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appeventos.model.entity.Usuario;
import com.appeventos.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/{username}")
    public ResponseEntity<Usuario> getByUsername(
            @PathVariable String username,
            Authentication authentication) {

        // Un usuario solo puede ver su propio perfil, salvo que sea admin
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin && !authentication.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(usuarioService.findByUsername(username));
    }
}
