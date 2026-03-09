package com.auth.jwt.security;

import com.auth.jwt.dto.RequestDto;
import com.auth.jwt.entity.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    // Inyecta el secreto desde application.yml
    @Value("${jwt.secret}")
    private String secret;

    // Almacena la clave criptográfica para firmar/validar tokens
    private Key key;

    // Inyectamos el validador de ruta
    private final RouteValidator routeValidator;

    public JwtProvider(RouteValidator routeValidator) {
        this.routeValidator = routeValidator;
    }

    // Metodo que se ejecuta despues de la construccion del bean
    // Convierte el secreto (texto plano) en una clave criptográfica HMAC-SHA256
    // que JWT usará para firmar y validar tokens.
    @PostConstruct
    protected void init() {
        // Convertir el string del secreto a bytes y crear la clave HMAC
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Crea un token para un usuario autenticado
    public String createToken(AuthUser authUser) {
        // Crea los claims que se guardaran en el token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", authUser.getId()); // Guardar ID del usuario
        claims.put("role", authUser.getRole()); // Guardar el rol en el token

        // Generar fechas de emsion y expiracion
        Date now = new Date(); // Fecha y Hora actual
        Date exp = new Date(now.getTime() + 3600000); // 1 hora

        // Construir el token
        return Jwts.builder()
                .setSubject(authUser.getUsername())
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key) // Firma del token con la clave secret
                .compact(); // Generar el string del token
    }

    // Valida un token JWT y verifica que tenga los permisos necesarios
    // para acceder a la ruta solicitada.
    public boolean validate(String token, RequestDto requestDto) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // Establece la clave para verificar la firma
                    .build()
                    .parseClaimsJws(token);

            // 2. Si la ruta requiere ADMIN, verificar que el token tenga rol ADMIN
            if (routeValidator.isAdmin(requestDto) && !isAdmin(token)) {
                return false; // No es admin pero intenta acceder a ruta admin
            }

            return true; // Token válido y autorización correcta
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica si el token pertenece a un administrador
    private boolean isAdmin(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = (String) claims.get("role");
            return "admin".equals(role);
        } catch (Exception e) {
            return false; // Si hay error al extraer rol, no es admin
        }
    }

    // Extrae el nombre de usuario del token
    public String getUserNameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key) // Clave para verificar
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // Devolver el nombre de usuario
            return claims.getSubject();
        } catch (Exception e) {
            return "Bad token";
        }
    }
}
