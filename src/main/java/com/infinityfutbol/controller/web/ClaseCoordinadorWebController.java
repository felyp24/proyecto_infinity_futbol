package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClaseCoordinadorWebController {

    /*
     * Calendario de clases disponible para
     * coordinadores y administradores.
     */
    @GetMapping("/coordinador/calendario")
    public String mostrarCalendarioClases() {
        return "coordinador/calendario";
    }

    /*
     * Formulario y tabla para crear o modificar clases.
     */
    @GetMapping("/coordinador/clases")
    public String mostrarGestionClases() {
        return "coordinador/clases";
    }
}