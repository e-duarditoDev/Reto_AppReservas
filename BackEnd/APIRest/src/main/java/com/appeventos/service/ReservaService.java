package com.appeventos.service;

import java.util.List;

import com.appeventos.model.entity.Reserva;

public interface ReservaService {

    List<Reserva> findByUsername(String username);

    Reserva create(String username, Long idEvento);

    void deleteById(Long id, String username);
}
