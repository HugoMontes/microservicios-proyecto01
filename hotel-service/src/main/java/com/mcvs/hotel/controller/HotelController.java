package com.mcvs.hotel.controller;

import com.mcvs.hotel.entity.Hotel;
import com.mcvs.hotel.service.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hoteles")
public class HotelController {

    public final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    public ResponseEntity<Hotel> guardarHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.create(hotel));
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> listarHoteles(){
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.getAll());
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<Hotel> buscarHotel(@PathVariable String hotelId){
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.get(hotelId));
    }
}
