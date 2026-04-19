package com.appeventos.service;

import java.util.List;

import com.appeventos.model.entity.Evento;
import com.appeventos.model.entity.Evento.Destacado;
import com.appeventos.model.entity.Evento.Estado;

public interface EventoService {

    List<Evento> findAll();

    Evento findById(Long id);

    List<Evento> findByEstado(Estado estado);

    List<Evento> findByDestacado(Destacado destacado);

    Evento save(Evento evento);

    Evento update(Long id, Evento evento);

    void deleteById(Long id);

    void updateEstado(Long id, Estado estado);
}
