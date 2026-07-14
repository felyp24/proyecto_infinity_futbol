package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.request.CambiarEstadoUsuarioRequest;
import com.infinityfutbol.dto.response.UsuarioEstadoResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    @PatchMapping("/{idUsuario}/estado")
    public UsuarioEstadoResponse cambiarEstado(
            @PathVariable String idUsuario,

            @Valid
            @RequestBody
            CambiarEstadoUsuarioRequest request,

            @AuthenticationPrincipal
            CustomUserDetails administradorAutenticado
    ) {
        return usuarioService.cambiarEstadoUsuario(
                idUsuario,
                request,
                administradorAutenticado.getIdUsuario()
        );
    }
}