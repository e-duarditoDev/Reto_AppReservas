package com.appeventos.service;

import java.util.List;

import com.appeventos.model.entity.Tipo;

public interface TipoService {

    List<Tipo> findAll();

    Tipo findById(Long id);

    Tipo save(Tipo tipo);

    Tipo update(Long id, Tipo tipo);

    void deleteById(Long id);
}
