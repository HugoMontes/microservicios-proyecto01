package com.auth.jwt.security;

import com.auth.jwt.dto.RequestDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@ConfigurationProperties(prefix = "admin-paths")
public class RouteValidator {

    // Lista de rutas que requieren rol ADMIN
    // Se carga desde application.yml
    private List<RequestDto> paths;

    // Generar metodos getter y setter necesarios para @ConfigurationProperties
    public List<RequestDto> getPaths() {
        return paths;
    }

    public void setPaths(List<RequestDto> paths) {
        this.paths = paths;
    }

    // Determina si una petición específica requiere rol ADMIN.
    public boolean isAdmin(RequestDto requestDto) {
        // Recibe una peticion URI + Metodo y devuelve true si esa peticion
        // requiere un rol ADMIN y false si es cualquier otro rol.
        return paths.stream().anyMatch(p ->
                Pattern.matches(p.getUri(), requestDto.getUri())
                        && p.getMethod().equals(requestDto.getMethod()));
    }
}
