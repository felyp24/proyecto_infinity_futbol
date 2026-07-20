package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.ClaseCalendarioResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.InicioClienteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.infinityfutbol.dto.response.ResumenInicioResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inicio")
@PreAuthorize("hasRole('USUARIO')")
public class InicioClienteController {

    private final InicioClienteService inicioClienteService;

    public InicioClienteController(
            InicioClienteService inicioClienteService
    ) {
        this.inicioClienteService =
                inicioClienteService;
    }

    @GetMapping("/clases")
    public List<ClaseCalendarioResponse> listarClases(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate start,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate end
    ) {
        return inicioClienteService
                .listarClasesCalendario(
                        usuarioAutenticado.getIdUsuario(),
                        start,
                        end
                );
    }

    @GetMapping("/resumen")
    public ResumenInicioResponse obtenerResumen(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return inicioClienteService.obtenerResumen(
                usuarioAutenticado.getIdUsuario()
        );
    }
}