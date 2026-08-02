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
import com.infinityfutbol.dto.response.ReservaProximaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.infinityfutbol.dto.response.ReservaHistorialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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

    @GetMapping
    public List<ReservaProximaResponse> listarReservasProximas(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return reservaService.listarReservasProximas(
                usuarioAutenticado.getIdUsuario()
        );
    }

    @DeleteMapping("/{idReserva}")
    public ReservaResponse cancelarReserva(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @PathVariable
            String idReserva
    ) {
        return reservaService.cancelarReserva(
                usuarioAutenticado.getIdUsuario(),
                idReserva
        );
    }

    @GetMapping("/historial")
    public Page<ReservaHistorialResponse>
    listarReservasPasadas(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @PageableDefault(size = 6)
            Pageable pageable
    ) {
        return reservaService
                .listarReservasPasadas(
                        usuarioAutenticado
                                .getIdUsuario(),
                        pageable
                );
    }
}