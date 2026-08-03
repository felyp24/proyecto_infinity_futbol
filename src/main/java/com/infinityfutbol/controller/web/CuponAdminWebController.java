package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CuponAdminWebController {

    @GetMapping("/admin/cupones")
    public String mostrarGestionCupones() {
        return "admin/cupones";
    }
}