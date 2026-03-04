package com.mcvs.calificacion.controller;

import com.mcvs.calificacion.entity.Calificacion;
import com.mcvs.calificacion.service.CalificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping
    public ResponseEntity<Calificacion> guardarCalificaion(@RequestBody Calificacion calificacion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calificacionService.create(calificacion));
    }

    @GetMapping
    public ResponseEntity<List<Calificacion>> listarCalificaiones() {
        return ResponseEntity.ok(calificacionService.getCalificaciones());
    }

    @GetMapping("/usuarios/{usuarioId}")
    public ResponseEntity<List<Calificacion>> listarCalificaionesPorUsuarioId(@PathVariable String usuarioId) {
        return ResponseEntity.ok(calificacionService.getCalificacionesByUsuarioId(usuarioId));

    }

    @GetMapping("/hoteles/{hotelId}")
    public ResponseEntity<List<Calificacion>> listarCalificaionesPorHotelId(@PathVariable String hotelId) {
        return ResponseEntity.ok(calificacionService.getCalificacionesByHotelId(hotelId));
    }
}
