package com.mscv.usuario.external.fallback;

import com.mscv.usuario.entity.Hotel;
import com.mscv.usuario.external.service.HotelService;
import org.springframework.stereotype.Component;

@Component
public class HotelFallback implements HotelService {

    @Override
    public Hotel getHotel(String hotelId) {
        return Hotel.builder()
                .id(hotelId)
                .nombre("Hotel no disponible")
                .ubicacion("Servicio temporalmente caído")
                .build();
    }
}
