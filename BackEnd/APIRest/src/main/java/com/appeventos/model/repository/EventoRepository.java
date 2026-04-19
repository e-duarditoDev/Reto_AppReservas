package com.appeventos.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appeventos.model.entity.Evento;
import com.appeventos.model.entity.Evento.Destacado;
import com.appeventos.model.entity.Evento.Estado;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByEstado(Estado estado);

    List<Evento> findByDestacado(Destacado destacado);

    List<Evento> findAllByEstadoAndFechaInicioBefore(Estado estado, LocalDateTime fecha);
}
