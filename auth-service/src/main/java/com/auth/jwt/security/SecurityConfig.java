package com.auth.jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactiva protección CSRF (común en APIs REST)
                .csrf(csrf -> csrf.disable())
                // Permite todas las peticiones sin autenticación
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}


//package com.auth.jwt.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                // Desactiva CSRF porque usaremos JWT (stateless API)
//                .csrf(csrf -> csrf.disable())
//
//                // Configura las reglas de autorización
//                .authorizeHttpRequests(auth -> auth
//                        // Permite acceso libre a endpoints de autenticación
//                        .requestMatchers("/auth/**").permitAll()
//
//                        // Permite acceso a la consola H2 (solo para desarrollo)
//                        .requestMatchers("/h2-console/**").permitAll()
//
//                        // Cualquier otra petición requiere autenticación
//                        .anyRequest().authenticated()
//                )
//
//                // Indica que la aplicación no usará sesiones HTTP
//                // Cada request se autentica con JWT
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//
//                // Permite que la consola H2 se muestre en iframe
//                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
//
//        return http.build();
//    }
//}