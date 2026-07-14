package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthWebController {

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "auth/registro";
    }

    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(
                    name = "registro",
                    required = false
            )
            String registro,
            Model model
    ) {
        model.addAttribute(
                "registroExitoso",
                "exitoso".equals(registro)
        );

        return "auth/login";
    }
}