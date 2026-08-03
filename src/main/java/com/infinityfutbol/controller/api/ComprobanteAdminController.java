package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.ComprobanteAdminResponse;
import com.infinityfutbol.service.ComprobanteAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/comprobantes")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ComprobanteAdminController {

    private final ComprobanteAdminService
            comprobanteAdminService;

    public ComprobanteAdminController(
            ComprobanteAdminService
                    comprobanteAdminService
    ) {
        this.comprobanteAdminService =
                comprobanteAdminService;
    }

    @GetMapping
    public Page<ComprobanteAdminResponse>
    listarComprobantes(
            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String texto,

            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return comprobanteAdminService
                .listarComprobantesEmitidos(
                        texto,
                        pageable
                );
    }

    @GetMapping("/{idComprobante}")
    public ComprobanteAdminResponse
    obtenerComprobante(
            @PathVariable
            String idComprobante
    ) {
        return comprobanteAdminService
                .obtenerComprobante(
                        idComprobante
                );
    }
}