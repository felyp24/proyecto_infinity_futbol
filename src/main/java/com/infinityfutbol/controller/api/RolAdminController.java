package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.RolResponse;
import com.infinityfutbol.dto.response.UsuarioRolResponse;
import com.infinityfutbol.service.RolService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.infinityfutbol.dto.request.CambiarRolRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.infinityfutbol.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class RolAdminController {

    private final RolService rolService;

    public RolAdminController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping("/usuarios")
    public List<UsuarioRolResponse> listarUsuariosConRoles() {
        return rolService.listarUsuariosConRoles();
    }

    @GetMapping("/roles")
    public List<RolResponse> listarRolesActivos() {
        return rolService.listarRolesActivos();
    }

    @PutMapping("/usuarios/{idUsuario}/rol")
    public UsuarioRolResponse cambiarRol(
            @PathVariable String idUsuario,
            @Valid @RequestBody CambiarRolRequest request,
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado
    ) {
        return rolService.cambiarRol(
                idUsuario,
                request,
                usuarioAutenticado.getIdUsuario()
        );
    }
}