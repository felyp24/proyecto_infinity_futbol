package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.request.GuardarAsistenciasRequest;
import com.infinityfutbol.dto.response.AlumnoAsistenciaResponse;
import com.infinityfutbol.dto.response.ClaseAsistenciaResponse;
import com.infinityfutbol.dto.response.GuardarAsistenciasResponse;
import com.infinityfutbol.service.AsistenciaCoordinadorService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(
        "/api/coordinador/asistencias"
)
@PreAuthorize(
        "hasAnyRole('COORDINADOR', 'ADMINISTRADOR')"
)
public class AsistenciaCoordinadorController {

    private final AsistenciaCoordinadorService
            asistenciaCoordinadorService;

    public AsistenciaCoordinadorController(
            AsistenciaCoordinadorService
                    asistenciaCoordinadorService
    ) {
        this.asistenciaCoordinadorService =
                asistenciaCoordinadorService;
    }

    @GetMapping("/clases")
    public List<ClaseAsistenciaResponse>
    listarClases(
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fecha
    ) {
        return asistenciaCoordinadorService
                .listarClases(
                        fecha
                );
    }

    @GetMapping(
            "/clases/{idClase}/alumnos"
    )
    public List<AlumnoAsistenciaResponse>
    listarAlumnos(
            @PathVariable
            String idClase
    ) {
        return asistenciaCoordinadorService
                .listarAlumnosClase(
                        idClase
                );
    }

    @PutMapping(
            "/clases/{idClase}"
    )
    public GuardarAsistenciasResponse
    guardarAsistencias(
            @PathVariable
            String idClase,

            @Valid
            @RequestBody
            GuardarAsistenciasRequest request
    ) {
        return asistenciaCoordinadorService
                .guardarAsistencias(
                        idClase,
                        request
                );
    }
}