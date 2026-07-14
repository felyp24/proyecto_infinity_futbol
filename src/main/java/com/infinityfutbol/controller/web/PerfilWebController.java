package com.infinityfutbol.controller.web;

import com.infinityfutbol.dto.response.PerfilResponse;
import com.infinityfutbol.security.CustomUserDetails;
import com.infinityfutbol.service.PerfilService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class PerfilWebController {

    private final PerfilService perfilService;

    public PerfilWebController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,
            Model model
    ) {
        PerfilResponse perfil = perfilService.obtenerPerfil(
                usuarioAutenticado.getIdUsuario()
        );

        model.addAttribute("perfil", perfil);

        return "perfil/perfil";
    }

    @GetMapping("/perfil/editar")
    public String mostrarFormularioEdicion(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,
            Model model
    ) {
        PerfilResponse perfil = perfilService.obtenerPerfil(
                usuarioAutenticado.getIdUsuario()
        );

        if (perfil.idAlumno() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La cuenta no tiene un perfil de alumno asociado"
            );
        }

        model.addAttribute("perfil", perfil);

        return "perfil/editar-perfil";
    }
    @GetMapping("/perfil/seguridad")
    public String mostrarSeguridad(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,
            Model model
    ) {
        model.addAttribute(
                "usernameActual",
                usuarioAutenticado.getUsername()
        );

        return "perfil/seguridad";
    }
}