package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.request.ActualizarSaldoCreditoRequest;
import com.infinityfutbol.dto.response.CreditoAdminResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.CreditoAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.infinityfutbol.dto.response.HistorialAjusteCreditoResponse;


@RestController
@RequestMapping("/api/admin/creditos")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class CreditoAdminController {

    private final CreditoAdminService
            creditoAdminService;

    public CreditoAdminController(
            CreditoAdminService creditoAdminService
    ) {
        this.creditoAdminService =
                creditoAdminService;
    }

    @GetMapping
    public Page<CreditoAdminResponse> listarCuentas(
            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String texto,

            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return creditoAdminService.listarCuentas(
                texto,
                pageable
        );
    }

    @GetMapping("/historial")
    public Page<HistorialAjusteCreditoResponse>
    listarHistorialAjustes(
            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String texto,

            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return creditoAdminService
                .listarHistorialAjustes(
                        texto,
                        pageable
                );
    }

    @PatchMapping("/{idAlumno}")
    public CreditoAdminResponse actualizarSaldo(
            @PathVariable
            String idAlumno,

            @Valid
            @RequestBody
            ActualizarSaldoCreditoRequest request,

            @AuthenticationPrincipal
            CustomUserDetails administradorAutenticado
    ) {
        return creditoAdminService.actualizarSaldo(
                idAlumno,
                request,

                administradorAutenticado
                        .getIdUsuario(),

                administradorAutenticado
                        .getUsername()
        );
    }
}