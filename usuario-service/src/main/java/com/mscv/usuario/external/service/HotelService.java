package com.mscv.usuario.external.service;

import com.mscv.usuario.entity.Hotel;
import com.mscv.usuario.external.fallback.HotelFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "HOTEL-SERVICE", fallback = HotelFallback.class)
public interface HotelService {

    @GetMapping("/hoteles/{hotelId}")
    Hotel getHotel(@PathVariable String hotelId);
}
