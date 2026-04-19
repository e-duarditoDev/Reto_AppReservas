package com.appeventos.restController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appeventos.model.entity.Reserva;
import com.appeventos.service.ReservaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<Reserva>> getMisReservas(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(reservaService.findByUsername(username));
    }

    @PostMapping("/{idEvento}")
    public ResponseEntity<Reserva> create(
            @PathVariable Long idEvento,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservaService.create(username, idEvento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        reservaService.deleteById(id, username);
        return ResponseEntity.noContent().build();
    }
}
