package com.appeventos.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.appeventos.model.entity.Evento;
import com.appeventos.model.entity.Evento.Destacado;
import com.appeventos.model.entity.Evento.Estado;
import com.appeventos.model.repository.EventoRepository;
import com.appeventos.service.EventoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;

    @Override
    public List<Evento> findAll() {
        return eventoRepository.findAll();
    }

    @Override
    public Evento findById(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con id: " + id));
    }

    @Override
    public List<Evento> findByEstado(Estado estado) {
        return eventoRepository.findByEstado(estado);
    }

    @Override
    public List<Evento> findByDestacado(Destacado destacado) {
        return eventoRepository.findByDestacado(destacado);
    }

    @Override
    public Evento save(Evento evento) {
        return eventoRepository.save(evento);
    }

    @Override
    public Evento update(Long id, Evento evento) {
        Evento existente = findById(id);
        existente.setNombre(evento.getNombre());
        existente.setDescripcion(evento.getDescripcion());
        existente.setFechaInicio(evento.getFechaInicio());
        existente.setFechaFin(evento.getFechaFin());
        existente.setPrecio(evento.getPrecio());
        existente.setAforo(evento.getAforo());
        existente.setEstado(evento.getEstado());
        existente.setDestacado(evento.getDestacado());
        existente.setTipo(evento.getTipo());
        return eventoRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new EntityNotFoundException("Evento no encontrado con id: " + id);
        }
        eventoRepository.deleteById(id);
    }

    @Override
    public void updateEstado(Long id, Estado estado) {
        Evento evento = findById(id);
        evento.setEstado(estado);
        eventoRepository.save(evento);
    }
}
