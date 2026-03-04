package com.mcvs.calificacion.service.impl;

import com.mcvs.calificacion.entity.Calificacion;
import com.mcvs.calificacion.repository.CalificacionRepository;
import com.mcvs.calificacion.service.CalificacionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionServiceImpl  implements CalificacionService {

    private final CalificacionRepository calificacionRepository;

    public CalificacionServiceImpl(CalificacionRepository calificacionRepository){
        this.calificacionRepository = calificacionRepository;
    }

    @Override
    public Calificacion create(Calificacion calificacion) {
        return calificacionRepository.save(calificacion);
    }

    @Override
    public List<Calificacion> getCalificaciones() {
        return calificacionRepository.findAll();
    }

    @Override
    public List<Calificacion> getCalificacionesByUsuarioId(String usuarioId) {
        return calificacionRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Calificacion> getCalificacionesByHotelId(String hotelId) {
        return calificacionRepository.findByHotelId(hotelId);
    }
}
