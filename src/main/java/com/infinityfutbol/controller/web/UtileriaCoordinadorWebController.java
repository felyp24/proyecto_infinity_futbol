package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UtileriaCoordinadorWebController {

    @GetMapping("/coordinador/utileria")
    public String mostrarGestionUtileria() {
        return "coordinador/utileria";
    }
}