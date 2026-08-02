package com.infinityfutbol.service;

import com.infinityfutbol.dto.request.LoginRequest;
import com.infinityfutbol.dto.response.LoginResponse;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.security.JwtCookieService;
import com.infinityfutbol.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtCookieService jwtCookieService;
    private final UsuarioRepository usuarioRepository;

    public LoginService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtCookieService jwtCookieService,
            UsuarioRepository usuarioRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtCookieService = jwtCookieService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public LoginResponse iniciarSesion(
            LoginRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication =
                autenticarUsuario(request);

        CustomUserDetails usuarioAutenticado =
                (CustomUserDetails) authentication.getPrincipal();

        actualizarUltimoAcceso(
                usuarioAutenticado.getIdUsuario()
        );

        List<String> roles =
                obtenerRoles(usuarioAutenticado);

        String token =
                jwtService.generarToken(
                        usuarioAutenticado
                );

        jwtCookieService.agregarToken(
                response,
                token
        );

        return new LoginResponse(
                usuarioAutenticado.getIdUsuario(),
                usuarioAutenticado.getUsername(),
                roles,
                determinarRutaDestino(roles),
                "Inicio de sesión correcto"
        );
    }

    private Authentication autenticarUsuario(
            LoginRequest request
    ) {
        try {
            UsernamePasswordAuthenticationToken token =
                    UsernamePasswordAuthenticationToken
                            .unauthenticated(
                                    request.username().trim(),
                                    request.password()
                            );

            return authenticationManager.authenticate(token);

        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "El usuario, la contraseña o el estado de la cuenta no son válidos"
            );
        }
    }

    private void actualizarUltimoAcceso(
            String idUsuario
    ) {
        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "No se pudo identificar al usuario"
                        )
                );

        usuario.setUltimoAcceso(
                LocalDateTime.now()
        );
    }

    private List<String> obtenerRoles(
            CustomUserDetails usuario
    ) {
        return usuario
                .getAuthorities()
                .stream()
                .map(authority ->
                        authority
                                .getAuthority()
                                .replaceFirst(
                                        "^ROLE_",
                                        ""
                                )
                )
                .sorted()
                .toList();
    }

    private String determinarRutaDestino(
            List<String> roles
    ) {
        if (roles.contains("ADMINISTRADOR")) {
            return "/admin";
        }

        if (roles.contains("COORDINADOR")) {
            return "/coordinador";
        }

        if (roles.contains("ENTRENADOR")) {
            return "/perfil";
        }

        if (roles.contains("USUARIO")) {
            return "/inicio";
        }

        return "/perfil";
    }
}