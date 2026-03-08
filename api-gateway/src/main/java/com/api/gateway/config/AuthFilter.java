package com.api.gateway.config;

import com.api.gateway.dto.TokenDto;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    // Cliente WebClient reactivo para comunicarse con auth-service
    private final WebClient.Builder webClient;

    public AuthFilter(WebClient.Builder webClient) {
        super(Config.class);
        this.webClient = webClient;
    }

    // Se ejecuta por cada petición que coincide con las rutas configuradas.
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Obtener el header Authorization de la peticion
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            // Si no existe Authorization -> error 401 (No autorizado)
            if (authHeader == null) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // Separar "Bearer TOKEN" en partes ["Bearer", "token"]
            String[] chunks = authHeader.split(" ");

            // Validar formato correcto (debe ser exactamente 2 partes y empezar con "Bearer")
            if (chunks.length != 2 || !chunks[0].equals("Bearer")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            // Extraer el token
            String token = chunks[1];

            // Llamar al microservicio auth-service para validar el token
            return webClient.build()
                    .post() // Metodo POST
                    .uri("http://auth-service/auth/validate?token=" + token) // URL de auth-service
                    .retrieve() // Ejecuta la peticion
                    .bodyToMono(TokenDto.class) // Convertir respuesta a TokenDto (reactivo)
                    .map(response -> exchange) // Si la validación es exitosa, continuamos con el exchange
                    .flatMap(chain::filter); // Encadenar al siguiente filtro (enviar al microservicio destino)
        };
    }

    // Metodo para manejar errores
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {

    }
}
