package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioWebController {

    @GetMapping("/inicio")
    public String mostrarInicio() {
        return "inicio/inicio";
    }

    /*
     * Conservamos temporalmente esta ruta para que
     * enlaces antiguos no produzcan un error 404.
     */
    @GetMapping("/inicio/reservas")
    public String redirigirReservasAlInicio() {
        return "redirect:/inicio";
    }
}