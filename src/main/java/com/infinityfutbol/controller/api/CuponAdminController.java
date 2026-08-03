package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.request.CambiarEstadoCuponRequest;
import com.infinityfutbol.dto.request.CrearCuponRequest;
import com.infinityfutbol.dto.response.CuponAdminResponse;
import com.infinityfutbol.service.CuponDescuentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cupones")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class CuponAdminController {

    private final CuponDescuentoService
            cuponDescuentoService;

    public CuponAdminController(
            CuponDescuentoService
                    cuponDescuentoService
    ) {
        this.cuponDescuentoService =
                cuponDescuentoService;
    }

    @GetMapping
    public List<CuponAdminResponse> listarCupones(
            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String texto
    ) {
        return cuponDescuentoService
                .listarCupones(texto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuponAdminResponse crearCupon(
            @Valid
            @RequestBody
            CrearCuponRequest request
    ) {
        return cuponDescuentoService
                .crearCupon(request);
    }

    @PatchMapping("/{idCupon}/estado")
    public CuponAdminResponse cambiarEstado(
            @PathVariable
            String idCupon,

            @Valid
            @RequestBody
            CambiarEstadoCuponRequest request
    ) {
        return cuponDescuentoService
                .cambiarEstado(
                        idCupon,
                        request
                );
    }
}