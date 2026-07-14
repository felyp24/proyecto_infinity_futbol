package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.AlumnoListaResponse;
import com.infinityfutbol.service.AlumnoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.infinityfutbol.dto.request.ActualizarAlumnoAdminRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/admin/alumnos")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AlumnoAdminController {

    private final AlumnoService alumnoService;

    public AlumnoAdminController(
            AlumnoService alumnoService
    ) {
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public Page<AlumnoListaResponse> listarAlumnos(
            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String texto,

            @PageableDefault(
                    size = 10,
                    sort = "apellidos",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        return alumnoService.buscarAlumnos(
                texto,
                pageable
        );
    }
    @GetMapping("/{idAlumno}")
    public AlumnoListaResponse obtenerAlumno(
            @PathVariable String idAlumno
    ) {
        return alumnoService.obtenerAlumno(idAlumno);
    }

    @PutMapping("/{idAlumno}")
    public AlumnoListaResponse actualizarAlumno(
            @PathVariable String idAlumno,

            @Valid
            @RequestBody
            ActualizarAlumnoAdminRequest request
    ) {
        return alumnoService.actualizarAlumno(
                idAlumno,
                request
        );
    }
}