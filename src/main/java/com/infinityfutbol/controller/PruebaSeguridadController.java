package com.infinityfutbol.controller;

import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.security.JwtCookieService;
import com.infinityfutbol.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class PruebaSeguridadController {

    private final JwtService jwtService;
    private final JwtCookieService jwtCookieService;

    public PruebaSeguridadController(
            JwtService jwtService,
            JwtCookieService jwtCookieService
    ) {
        this.jwtService = jwtService;
        this.jwtCookieService = jwtCookieService;
    }

    @GetMapping("/perfil/prueba")
    public String probarUsuarioAutenticado(
            Authentication authentication
    ) {
        return "Usuario autenticado: "
                + authentication.getName()
                + " | Permisos: "
                + authentication.getAuthorities();
    }

    @GetMapping("/admin/prueba")
    public String probarAdministrador(
            Authentication authentication
    ) {
        return "Acceso administrativo correcto para: "
                + authentication.getName()
                + " | Permisos: "
                + authentication.getAuthorities();
    }

    @GetMapping("/jwt/prueba")
    public Map<String, Object> probarJwt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse response
    ) {
        String token = jwtService.generarToken(userDetails);

        jwtCookieService.agregarToken(response, token);

        return Map.of(
                "mensaje", "JWT generado y guardado en la cookie",
                "cookie", "ACCESS_TOKEN",
                "idUsuario", jwtService.extraerIdUsuario(token),
                "username", jwtService.extraerUsername(token),
                "valido", jwtService.esTokenValido(token, userDetails)
        );
    }

    @GetMapping("/jwt/cookie")
    public Map<String, Object> comprobarCookie(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Optional<String> token =
                jwtCookieService.obtenerToken(request);

        if (token.isEmpty()) {
            return Map.of(
                    "cookiePresente", false,
                    "mensaje", "No se encontró la cookie ACCESS_TOKEN"
            );
        }

        return Map.of(
                "cookiePresente", true,
                "idUsuario",
                jwtService.extraerIdUsuario(token.get()),
                "username",
                jwtService.extraerUsername(token.get()),
                "valido",
                jwtService.esTokenValido(
                        token.get(),
                        userDetails
                )
        );
    }
}