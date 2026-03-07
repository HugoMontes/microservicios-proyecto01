package com.mscv.usuario.service.impl;

import com.mscv.usuario.entity.Calificacion;
import com.mscv.usuario.entity.Hotel;
import com.mscv.usuario.entity.Usuario;
import com.mscv.usuario.exception.ResourceNotFoundException;
import com.mscv.usuario.external.service.CalificacionService;
import com.mscv.usuario.external.service.HotelService;
import com.mscv.usuario.repository.UsuarioRepository;
import com.mscv.usuario.service.UsuarioService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final RestTemplate restTemplate;

    private final UsuarioRepository usuarioRepository;

    private final HotelService hotelService;

    private final CalificacionService calificacionService;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            RestTemplate restTemplate,
            HotelService hotelService,
            CalificacionService calificacionService) {
        this.usuarioRepository = usuarioRepository;
        this.restTemplate = restTemplate;
        this.hotelService = hotelService;
        this.calificacionService = calificacionService;
    }

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        String randomUsuarioId = UUID.randomUUID().toString();
        usuario.setUsuarioId(randomUsuarioId);
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

//    @Override
//    public Usuario getUsuario(String usuarioId) {
//        // 1. Obtenemos el usuario
//        Usuario usuario = usuarioRepository.findById(usuarioId)
//                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID : " + usuarioId));
//        // 2. Obtenemos el listado de calificaciones del usuario
//        Calificacion[] calificacionesDelUsuario = restTemplate
//                .getForObject("http://localhost:8083/calificaciones/usuarios/" + usuario.getUsuarioId(), Calificacion[].class);
//        // 3. Convertimos array de calificaciones a un ArrayList
//        List<Calificacion> calificaciones = Arrays.asList(calificacionesDelUsuario);
//        // 4. Mostramos el listado de calificaciones en consola
//        logger.info("Calificaciones del usuario: {}", calificaciones);
//        // 5. Seteamos el ArrayList al usuario
//        usuario.setCalificaciones(Arrays.stream(calificacionesDelUsuario).toList());
//        // 6. Retornar el usuario con las calificaciones
//        return usuario;
//    }

    private int cantidadReintentos = 1;

    // @CircuitBreaker(name = "usuarioServiceBreaker", fallbackMethod = "fallbackUsuario")
    // @Retry(name = "usuarioServiceRetry", fallbackMethod = "fallbackUsuario")
    public Usuario getUsuario(String usuarioId) {
        logger.info("Listar un solo usuario : UsuarioServiceImpl");
        logger.info("Cantidad de intentos : {}", cantidadReintentos);
        cantidadReintentos++;

        // 1. Obtenemos el usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID : " + usuarioId));

        // 2. Obtenemos el listado de calificaciones del usuario
//        Calificacion[] calificacionesDelUsuario = restTemplate
//                // .getForObject("http://localhost:8083/calificaciones/usuarios/" + usuario.getUsuarioId(), Calificacion[].class);
//                .getForObject("http://CALIFICACION-SERVICE/calificaciones/usuarios/" + usuario.getUsuarioId(), Calificacion[].class);
//        // 3. Convertimos array de calificaciones a un ArrayList
//        List<Calificacion> calificaciones = Arrays.asList(calificacionesDelUsuario);

        List<Calificacion> calificaciones = calificacionService.calificacionesUsuario(usuario.getUsuarioId());

        // 4. Mostramos el listado de calificaciones en consola
        logger.info("Calificaciones del usuario: {}", calificaciones);

        // A la lista de calificaciones añadimos los datos de los hoteles
        List<Calificacion> listaCalificaciones = calificaciones.stream().map(calificacion -> {
            System.out.println("Hotel ID: " + calificacion.getHotelId());

            // Obtener datos del hotel por el ID
//            ResponseEntity<Hotel> forEntity = restTemplate
//                    // .getForEntity("http://localhost:8082/hoteles/"+calificacion.getHotelId() , Hotel.class);
//                    .getForEntity("http://HOTEL-SERVICE/hoteles/"+calificacion.getHotelId() , Hotel.class);
//            Hotel hotel = forEntity.getBody();
//            logger.info("Respuesta con codigo de estado: {}", forEntity.getStatusCode());

            Hotel hotel = hotelService.getHotel(calificacion.getHotelId());

            // Añadimos los datos del hotel a la calificacion
            calificacion.setHotel(hotel);
            // Retornar la calificacion para luego añadirla al List
            return calificacion;
        }).toList();

        // 5. Seteamos el ArrayList al usuario
        // usuario.setCalificaciones(Arrays.stream(calificacionesDelUsuario).toList());
        usuario.setCalificaciones(listaCalificaciones);

        // 6. Retornar el usuario con las calificaciones
        return usuario;
    }

    public Usuario fallbackUsuario(String usuarioId, Exception exception) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        logger.info("Ejecutando fallbackUsuario : {}", usuario);
        usuario.setInformacionAdicional("Algunos servicios no estan disponibles");
        usuario.setCalificaciones(new ArrayList<>());
        return usuario;
    }
}