package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClaseCoordinadorWebController {

    @GetMapping("/coordinador/clases")
    public String mostrarGestionClases() {
        return "coordinador/clases";
    }
}