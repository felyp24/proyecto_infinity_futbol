package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthWebController {


    @GetMapping("/")
    public String mostrarPaginaInicial() {
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(
                    name = "registro",
                    required = false
            )
            String registro,

            @RequestParam(
                    name = "logout",
                    required = false
            )
            String logout,

            Model model
    ) {
        model.addAttribute(
                "registroExitoso",
                "exitoso".equals(registro)
        );

        model.addAttribute(
                "logoutExitoso",
                "exitoso".equals(logout)
        );

        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "auth/registro";
    }
}