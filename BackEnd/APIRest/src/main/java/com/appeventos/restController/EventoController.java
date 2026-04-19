package com.appeventos.restController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appeventos.model.entity.Evento;
import com.appeventos.model.entity.Evento.Destacado;
import com.appeventos.model.entity.Evento.Estado;
import com.appeventos.service.EventoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    // --- Rutas públicas (GET) ---

    @GetMapping
    public ResponseEntity<List<Evento>> getAll() {
        return ResponseEntity.ok(eventoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.findById(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Evento>> getByEstado(@PathVariable Estado estado) {
        return ResponseEntity.ok(eventoService.findByEstado(estado));
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<Evento>> getDestacados() {
        return ResponseEntity.ok(eventoService.findByDestacado(Destacado.S));
    }

    // --- Rutas de admin (requieren ROLE_ADMIN según SecurityConfig) ---

    @PostMapping
    public ResponseEntity<Evento> create(@RequestBody Evento evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.save(evento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> update(@PathVariable Long id, @RequestBody Evento evento) {
        return ResponseEntity.ok(eventoService.update(id, evento));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> updateEstado(
            @PathVariable Long id,
            @RequestParam Estado estado) {
        eventoService.updateEstado(id, estado);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
