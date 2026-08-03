package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AsistenciaCoordinadorWebController {

    @GetMapping(
            "/coordinador/asistencias"
    )
    public String mostrarControlAsistencias() {
        return "coordinador/asistencias";
    }
}