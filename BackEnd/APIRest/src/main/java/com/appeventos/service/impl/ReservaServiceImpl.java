package com.appeventos.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.appeventos.model.entity.Evento;
import com.appeventos.model.entity.Evento.Estado;
import com.appeventos.model.entity.Reserva;
import com.appeventos.model.repository.ReservaRepository;
import com.appeventos.service.EventoService;
import com.appeventos.service.ReservaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final EventoService eventoService;

    @Override
    public List<Reserva> findByUsername(String username) {
        return reservaRepository.findByUsername(username);
    }

    @Override
    public List<Reserva> findByEvento(Long idEvento) {
        return reservaRepository.findByEvento_IdEvento(idEvento);
    }

    @Override
    public Reserva create(String username, Long idEvento) {
        Evento evento = eventoService.findById(idEvento);

        if (evento.getEstado() != Estado.ACTIVO) {
            throw new IllegalStateException("No se puede reservar un evento que no está activo.");
        }

        boolean yaReservado = reservaRepository
                .findByEvento_IdEventoAndUsername(idEvento, username)
                .isPresent();
        if (yaReservado) {
            throw new IllegalStateException("Ya tienes una reserva para este evento.");
        }

        if (reservaRepository.countByUsername(username) >= 10) {
            throw new IllegalStateException("Has alcanzado el límite de 10 reservas.");
        }

        int reservasActuales = reservaRepository.countByEvento_IdEvento(idEvento);
        if (reservasActuales >= evento.getAforo()) {
            throw new IllegalStateException("El aforo del evento está completo.");
        }

        Reserva reserva = new Reserva();
        reserva.setUsername(username);
        reserva.setEvento(evento);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setPrecioVenta(evento.getPrecio());

        return reservaRepository.save(reserva);
    }

    @Override
    public void deleteById(Long id, String username) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con id: " + id));

        if (!reserva.getUsername().equals(username)) {
            throw new SecurityException("No tienes permiso para cancelar esta reserva.");
        }

        reservaRepository.deleteById(id);
    }
}
