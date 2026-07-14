package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.PerfilResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.PerfilService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.infinityfutbol.dto.request.ActualizarPerfilRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.infinityfutbol.dto.request.ActualizarCredencialesRequest;
import com.infinityfutbol.dto.response.CredencialesResponse;
import com.infinityfutbol.security.JwtCookieService;
import com.infinityfutbol.service.UsuarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@RestController
@RequestMapping("/api/perfil")
@PreAuthorize("isAuthenticated()")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final JwtCookieService jwtCookieService;

    private final PerfilService perfilService;

    public PerfilController(
            PerfilService perfilService,
            UsuarioService usuarioService,
            JwtCookieService jwtCookieService
    ) {
        this.perfilService = perfilService;
        this.usuarioService = usuarioService;
        this.jwtCookieService = jwtCookieService;
    }

    @GetMapping
    public PerfilResponse obtenerPerfil(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return perfilService.obtenerPerfil(
                usuarioAutenticado.getIdUsuario()
        );
    }

    @PutMapping
    public PerfilResponse actualizarPerfil(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @Valid
            @RequestBody
            ActualizarPerfilRequest request
    ) {
        return perfilService.actualizarPerfil(
                usuarioAutenticado.getIdUsuario(),
                request
        );
    }
    @PutMapping("/credenciales")
    public CredencialesResponse actualizarCredenciales(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @Valid
            @RequestBody
            ActualizarCredencialesRequest request,

            HttpServletResponse response
    ) {
        CredencialesResponse resultado =
                usuarioService.actualizarCredenciales(
                        usuarioAutenticado.getIdUsuario(),
                        request
                );

        if (resultado.requiereNuevoInicioSesion()) {
            jwtCookieService.eliminarToken(response);
            SecurityContextHolder.clearContext();
        }

        return resultado;
    }
}