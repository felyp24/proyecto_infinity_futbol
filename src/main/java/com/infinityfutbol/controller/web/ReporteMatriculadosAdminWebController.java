package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReporteMatriculadosAdminWebController {

    @GetMapping(
            "/admin/reportes/matriculados"
    )
    public String mostrarReporteMatriculados() {
        return "admin/reporte-matriculados";
    }
}