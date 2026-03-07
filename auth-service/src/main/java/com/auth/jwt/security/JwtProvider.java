package com.auth.jwt.security;

import com.auth.jwt.entity.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
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

    // Metodo que se ejecuta despues de la construccion del bean
    // Prepare la clave de firma a partir del secreto
    @PostConstruct
    protected void init() {
        // Decodifica el secreto en base64
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        // Crea una clave segura a partir de los bytes
        key = Keys.hmacShaKeyFor(decodedKey);
    }

    // Crea un token para un usuario autenticado
    public String createToken(AuthUser authUser) {
        // Crea los claims que se guardaran en el token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", authUser.getId()); // Guardar ID del usuario

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

    // Verifica si un token es valido
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // Establece la clave para verificar la firma
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
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
