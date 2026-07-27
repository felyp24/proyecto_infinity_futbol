package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreditoAdminWebController {

    @GetMapping("/admin/creditos")
    public String mostrarGestorCreditos() {
        return "admin/creditos";
    }

    @GetMapping("/admin/creditos/historial")
    public String mostrarHistorialCreditos() {
        return "admin/creditos-historial";
    }
}