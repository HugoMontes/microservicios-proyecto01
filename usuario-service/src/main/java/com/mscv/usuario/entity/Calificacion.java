package com.mscv.usuario.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Calificacion {

    private String calificacionId;

    private String usuarioId;

    private String hotelId;

    private int calificacion;

    private String observaciones;

    private Hotel hotel;
}
