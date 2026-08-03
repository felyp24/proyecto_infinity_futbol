package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReporteIngresoAdminWebController {

    @GetMapping("/admin/reportes/ingresos")
    public String mostrarReporteIngresos() {
        return "admin/reporte-ingresos";
    }
}