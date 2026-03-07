package com.mscv.usuario.external.fallback;

import com.mscv.usuario.entity.Calificacion;
import com.mscv.usuario.external.service.CalificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalificacionFallback implements CalificacionService {
    @Override
    public List<Calificacion> calificacionesUsuario(String usuarioId) {
        Calificacion calificacion = Calificacion.builder()
                .calificacionId("0")
                .usuarioId(usuarioId)
                .hotelId("N/A")
                .calificacion(0)
                .observaciones("Servicio de calificaciones no disponible")
                .build();

        return List.of(calificacion);
    }

    @Override
    public ResponseEntity<Calificacion> guardarCalificacion(Calificacion calificacion) {
        return null;
    }

    @Override
    public ResponseEntity<Calificacion> actualizarCalificacion(String calificacionId, Calificacion calificacion) {
        return null;
    }

    @Override
    public void eliminarCalificacion(String calificacionId) {

    }
}
