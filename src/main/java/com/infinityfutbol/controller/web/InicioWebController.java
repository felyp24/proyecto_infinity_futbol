package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioWebController {

    @GetMapping("/inicio")
    public String mostrarInicio() {
        return "inicio/inicio";
    }
}