package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ComprobanteAdminWebController {

    @GetMapping("/admin/comprobantes")
    public String mostrarComprobantes() {
        return "admin/comprobantes";
    }
}