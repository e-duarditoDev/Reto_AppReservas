package com.appeventos.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appeventos.model.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsername(String username);

    int countByUsername(String username);

    int countByEvento_IdEvento(Long idEvento);

    Optional<Reserva> findByEvento_IdEventoAndUsername(Long idEvento, String username);
}
