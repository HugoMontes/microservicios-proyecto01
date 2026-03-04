package com.mscv.usuario.external.service;

import com.mscv.usuario.entity.Calificacion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "CALIFICACION-SERVICE")
public interface CalificacionService {

    @GetMapping("/calificaciones/usuarios/{usuarioId}")
    List<Calificacion> calificacionesUsuario(@PathVariable String usuarioId);

    @PostMapping("/calificaciones")
    ResponseEntity<Calificacion> guardarCalificacion(@RequestBody Calificacion calificacion);

    @PutMapping("/calificaciones/{calificacionId}")
    ResponseEntity<Calificacion> actualizarCalificacion(@PathVariable String calificacionId,
                                                        @RequestBody Calificacion calificacion);

    @DeleteMapping("/calificaciones/{calificacionId}")
    void eliminarCalificacion(@PathVariable String calificacionId);
}
