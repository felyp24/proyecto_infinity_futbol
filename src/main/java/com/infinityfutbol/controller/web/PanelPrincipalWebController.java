package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PanelPrincipalWebController {

    /*
     * Página principal del administrador.
     */
    @GetMapping("/admin")
    public String mostrarInicioAdministrador() {
        return "admin/inicio";
    }

    /*
     * Página principal del coordinador.
     *
     * El administrador también puede entrar porque
     * SecurityConfig le permite acceder a /coordinador/**.
     */
    @GetMapping("/coordinador")
    public String mostrarInicioCoordinador() {
        return "coordinador/inicio";
    }
}