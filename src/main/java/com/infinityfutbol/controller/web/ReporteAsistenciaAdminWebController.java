package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReporteAsistenciaAdminWebController {

    @GetMapping(
            "/admin/reportes/asistencias"
    )
    public String mostrarReporteAsistencias() {
        return "admin/reporte-asistencias";
    }
}