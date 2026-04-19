package com.appeventos.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appeventos.model.entity.Tipo;

public interface TipoRepository extends JpaRepository<Tipo, Long> {
}
