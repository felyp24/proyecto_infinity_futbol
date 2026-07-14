package com.infinityfutbol.controller.web;

import com.infinityfutbol.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/roles")
public class RolWebController {

    @GetMapping
    public String mostrarVistaRoles(
            @AuthenticationPrincipal
            CustomUserDetails usuarioAutenticado,
            Model model
    ) {
        model.addAttribute(
                "idUsuarioActual",
                usuarioAutenticado.getIdUsuario()
        );

        return "admin/roles";
    }
}