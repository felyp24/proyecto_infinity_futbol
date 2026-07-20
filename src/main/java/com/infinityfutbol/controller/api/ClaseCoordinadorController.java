package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.request.CrearClaseRequest;
import com.infinityfutbol.dto.response.CanchaOpcionResponse;
import com.infinityfutbol.dto.response.ClaseResponse;
import com.infinityfutbol.dto.response.EntrenadorOpcionResponse;
import com.infinityfutbol.service.ClaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coordinador/clases")
@PreAuthorize(
        "hasAnyRole('COORDINADOR', 'ADMINISTRADOR')"
)
public class ClaseCoordinadorController {

    private final ClaseService claseService;

    public ClaseCoordinadorController(
            ClaseService claseService
    ) {
        this.claseService = claseService;
    }

    @GetMapping("/canchas")
    public List<CanchaOpcionResponse> listarCanchas() {
        return claseService.listarCanchasDisponibles();
    }

    @GetMapping("/entrenadores")
    public List<EntrenadorOpcionResponse> listarEntrenadores() {
        return claseService.listarEntrenadoresActivos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClaseResponse crearClase(
            @Valid
            @RequestBody
            CrearClaseRequest request
    ) {
        return claseService.crearClase(request);
    }

    @GetMapping
    public List<ClaseResponse> listarClases() {
        return claseService.listarClasesProgramadas();
    }
}