package com.appeventos.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.appeventos.model.entity.Tipo;
import com.appeventos.model.repository.TipoRepository;
import com.appeventos.service.TipoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoServiceImpl implements TipoService {

    private final TipoRepository tipoRepository;

    @Override
    public List<Tipo> findAll() {
        return tipoRepository.findAll();
    }

    @Override
    public Tipo findById(Long id) {
        return tipoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo no encontrado con id: " + id));
    }

    @Override
    public Tipo save(Tipo tipo) {
        return tipoRepository.save(tipo);
    }

    @Override
    public Tipo update(Long id, Tipo tipo) {
        Tipo existente = findById(id);
        existente.setNombre(tipo.getNombre());
        existente.setDescripcion(tipo.getDescripcion());
        return tipoRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        if (!tipoRepository.existsById(id)) {
            throw new EntityNotFoundException("Tipo no encontrado con id: " + id);
        }
        tipoRepository.deleteById(id);
    }
}
