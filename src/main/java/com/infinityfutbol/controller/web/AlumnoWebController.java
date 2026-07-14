package com.infinityfutbol.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/admin/alumnos")
public class AlumnoWebController {

    @GetMapping
    public String mostrarListaAlumnos() {
        return "admin/alumnos/lista";
    }

    @GetMapping("/{idAlumno}/editar")
    public String mostrarFormularioEdicion(
            @PathVariable String idAlumno,
            Model model
    ) {
        model.addAttribute("idAlumno", idAlumno);

        return "admin/alumnos/editar";
    }
}

