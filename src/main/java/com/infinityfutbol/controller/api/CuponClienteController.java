package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.CuponValidacionResponse;
import com.infinityfutbol.service.CuponDescuentoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        "/api/inicio/creditos/cupones"
)
@PreAuthorize("hasRole('USUARIO')")
public class CuponClienteController {

    private final CuponDescuentoService
            cuponDescuentoService;

    public CuponClienteController(
            CuponDescuentoService
                    cuponDescuentoService
    ) {
        this.cuponDescuentoService =
                cuponDescuentoService;
    }

    @GetMapping("/validar")
    public CuponValidacionResponse validarCupon(
            @RequestParam
            String codigo
    ) {
        return cuponDescuentoService
                .validarCuponCliente(codigo);
    }
}