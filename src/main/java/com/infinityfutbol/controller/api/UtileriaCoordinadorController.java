package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.request.GuardarUtileriaRequest;
import com.infinityfutbol.dto.response.InventarioUtileriaResponse;
import com.infinityfutbol.dto.response.SedeOpcionResponse;
import com.infinityfutbol.dto.response.UtileriaResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.UtileriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coordinador/utileria")
@PreAuthorize(
        "hasAnyRole('COORDINADOR', 'ADMINISTRADOR')"
)
public class UtileriaCoordinadorController {

    private final UtileriaService
            utileriaService;

    public UtileriaCoordinadorController(
            UtileriaService utileriaService
    ) {
        this.utileriaService =
                utileriaService;
    }

    @GetMapping("/sedes")
    public List<SedeOpcionResponse>
    listarSedes() {
        return utileriaService
                .listarSedesActivas();
    }

    @GetMapping
    public InventarioUtileriaResponse listar(
            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String idSede,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String texto,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String situacion,

            @RequestParam(
                    required = false,
                    defaultValue = "false"
            )
            boolean incluirInactivos
    ) {
        return utileriaService.listarUtileria(
                idSede,
                texto,
                situacion,
                incluirInactivos
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtileriaResponse crear(
            @Valid
            @RequestBody
            GuardarUtileriaRequest request,

            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return utileriaService.crear(
                request,

                usuarioAutenticado
                        .getIdUsuario()
        );
    }

    @PutMapping("/{idUtileria}")
    public UtileriaResponse actualizar(
            @PathVariable
            String idUtileria,

            @Valid
            @RequestBody
            GuardarUtileriaRequest request,

            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return utileriaService.actualizar(
                idUtileria,
                request,

                usuarioAutenticado
                        .getIdUsuario()
        );
    }

    @DeleteMapping("/{idUtileria}")
    public UtileriaResponse eliminar(
            @PathVariable
            String idUtileria,

            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return utileriaService
                .eliminarLogicamente(
                        idUtileria,

                        usuarioAutenticado
                                .getIdUsuario()
                );
    }

    @PatchMapping("/{idUtileria}/restaurar")
    public UtileriaResponse restaurar(
            @PathVariable
            String idUtileria,

            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return utileriaService.restaurar(
                idUtileria,

                usuarioAutenticado
                        .getIdUsuario()
        );
    }
}