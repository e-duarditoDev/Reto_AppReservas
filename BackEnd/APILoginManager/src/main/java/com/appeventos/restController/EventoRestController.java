package com.appeventos.restController;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/eventos")
public class EventoRestController {

    @GetMapping
    public List<Map<String, Object>> listarEventos() {
        return List.of(
            Map.of("titulo", "Concierto", "descripcion", "Evento musical", "ubicacion", "Madrid", "capacidad", 100),
            Map.of("titulo", "Tech Meetup", "descripcion", "Networking devs", "ubicacion", "Barcelona", "capacidad", 50)
        );
    }
}