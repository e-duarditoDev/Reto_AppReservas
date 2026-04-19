package com.appeventos.restController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appeventos.model.entity.Tipo;
import com.appeventos.service.TipoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tipos")
@RequiredArgsConstructor
public class TipoController {

    private final TipoService tipoService;

    @GetMapping
    public ResponseEntity<List<Tipo>> getAll() {
        return ResponseEntity.ok(tipoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tipo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Tipo> create(@RequestBody Tipo tipo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoService.save(tipo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tipo> update(@PathVariable Long id, @RequestBody Tipo tipo) {
        return ResponseEntity.ok(tipoService.update(id, tipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
