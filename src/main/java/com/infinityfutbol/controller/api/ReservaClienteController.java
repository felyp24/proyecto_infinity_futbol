package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.ReservaResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inicio/reservas")
@PreAuthorize("hasRole('USUARIO')")
public class ReservaClienteController {

    private final ReservaService reservaService;

    public ReservaClienteController(
            ReservaService reservaService
    ) {
        this.reservaService = reservaService;
    }

    @PostMapping("/{idClase}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse reservarClase(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @PathVariable
            String idClase
    ) {
        return reservaService.reservarClase(
                usuarioAutenticado.getIdUsuario(),
                idClase
        );
    }
}