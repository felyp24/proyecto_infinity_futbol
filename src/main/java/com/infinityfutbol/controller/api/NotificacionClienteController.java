package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.NotificacionResponse;
import com.infinityfutbol.dto.response.NotificacionesInicioResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.NotificacionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inicio/notificaciones")
@PreAuthorize("hasRole('USUARIO')")
public class NotificacionClienteController {

    private final NotificacionService notificacionService;

    public NotificacionClienteController(
            NotificacionService notificacionService
    ) {
        this.notificacionService =
                notificacionService;
    }

    @GetMapping
    public NotificacionesInicioResponse listarNotificaciones(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return notificacionService
                .listarNotificacionesCliente(
                        usuarioAutenticado.getIdUsuario()
                );
    }

    @PatchMapping("/{idNotificacion}/leer")
    public NotificacionResponse marcarComoLeida(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @PathVariable
            String idNotificacion
    ) {
        return notificacionService.marcarComoLeida(
                usuarioAutenticado.getIdUsuario(),
                idNotificacion
        );
    }
}