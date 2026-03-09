package com.auth.jwt.service;

import com.auth.jwt.dto.AuthUserDto;
import com.auth.jwt.dto.NewUserDto;
import com.auth.jwt.dto.RequestDto;
import com.auth.jwt.dto.TokenDto;
import com.auth.jwt.entity.AuthUser;
import com.auth.jwt.repository.AuthUserRepository;
import com.auth.jwt.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthUserRepository authUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    // Inyeccion de dependencias por constructor
    public AuthService(
            AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    // Registra un nuevo usuario
    public AuthUser save(NewUserDto dto) {
        Optional<AuthUser> user = authUserRepository.findByUsername(dto.getUsername());
        // Si el usuario existe no se registra
        if (user.isPresent()) {
            return null;
        }
        // Encriptar password
        String password = passwordEncoder.encode(dto.getPassword());
        // Crear usuario a guardar
        AuthUser authUser = AuthUser.builder()
                .username(dto.getUsername())
                .password(password)
                .role(dto.getRole())
                .build();
        // Guardar usuario
        return authUserRepository.save(authUser);
    }

    // Autentica usuario y genera JWT
    public TokenDto login(AuthUserDto dto) {
        Optional<AuthUser> user = authUserRepository.findByUsername(dto.getUsername());
        // Verifica si el usuario no existe
        if (user.isEmpty()) {
            return null;
        }
        // Verifica si la contraseña es correcta
        if (passwordEncoder.matches(dto.getPassword(), user.get().getPassword())) {
            // Genera token JWT
            return new TokenDto(jwtProvider.createToken(user.get()));
        }
        return null;
    }

    // Verifica si un token JWT es valido
    public TokenDto validate(String token, RequestDto requestDto) {
        // Verifica firma y expiracion del token
        if (!jwtProvider.validate(token, requestDto)) {
            return null;
        }
        // Obtener username desde el token
        String userName = jwtProvider.getUserNameFromToken(token);
        // Verificar que el usuario exista en la BD
        if (authUserRepository.findByUsername(userName).isEmpty()) {
            return null;
        }
        return new TokenDto(token);
    }
}
