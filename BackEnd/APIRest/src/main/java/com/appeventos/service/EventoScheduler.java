package com.appeventos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.appeventos.model.entity.Evento;
import com.appeventos.model.entity.Evento.Estado;
import com.appeventos.model.repository.EventoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventoScheduler {

    private final EventoRepository eventoRepository;

    // Se ejecuta cada 60 segundos
    @Scheduled(fixedDelay = 60000)
    public void marcarEventosTerminados() {
        List<Evento> caducados = eventoRepository
                .findAllByEstadoAndFechaFinBefore(Estado.ACTIVO, LocalDateTime.now());

        caducados.forEach(e -> e.setEstado(Estado.TERMINADO));
        eventoRepository.saveAll(caducados);
    }
}
