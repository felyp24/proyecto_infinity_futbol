package com.infinityfutbol.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class NavegacionGlobalControllerAdvice {

    @ModelAttribute
    public void agregarDatosNavegacion(
            Model model,
            Authentication authentication,
            HttpServletRequest request
    ) {
        boolean autenticado =
                authentication != null
                        && authentication.isAuthenticated()
                        && !(authentication
                        instanceof AnonymousAuthenticationToken);

        Set<String> autoridades =
                autenticado
                        ? authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .collect(
                                Collectors.toSet()
                        )
                        : Set.of();

        boolean esAdministrador =
                autoridades.contains(
                        "ROLE_ADMINISTRADOR"
                );

        boolean esCoordinador =
                autoridades.contains(
                        "ROLE_COORDINADOR"
                );

        boolean esEntrenador =
                autoridades.contains(
                        "ROLE_ENTRENADOR"
                );

        boolean esUsuario =
                autoridades.contains(
                        "ROLE_USUARIO"
                );

        List<String> nombresRoles =
                autoridades
                        .stream()
                        .map(rol ->
                                rol.replaceFirst(
                                        "^ROLE_",
                                        ""
                                )
                        )
                        .sorted()
                        .toList();

        String rutaInicio =
                determinarRutaInicio(
                        esAdministrador,
                        esCoordinador,
                        esEntrenador,
                        esUsuario
                );

        model.addAttribute(
                "navAutenticado",
                autenticado
        );

        model.addAttribute(
                "navEsAdministrador",
                esAdministrador
        );

        model.addAttribute(
                "navEsCoordinador",
                esCoordinador
        );

        model.addAttribute(
                "navEsEntrenador",
                esEntrenador
        );

        model.addAttribute(
                "navEsUsuario",
                esUsuario
        );

        model.addAttribute(
                "navPuedeGestionarClases",
                esAdministrador
                        || esCoordinador
        );

        model.addAttribute(
                "navRutaInicio",
                rutaInicio
        );

        model.addAttribute(
                "navUsuario",
                autenticado
                        ? authentication.getName()
                        : ""
        );

        model.addAttribute(
                "navRoles",
                nombresRoles
        );

        model.addAttribute(
                "navRutaActual",
                request.getRequestURI()
        );
    }

    private String determinarRutaInicio(
            boolean esAdministrador,
            boolean esCoordinador,
            boolean esEntrenador,
            boolean esUsuario
    ) {
        /*
         * El orden importa en cuentas que
         * tengan más de un rol.
         */

        if (esAdministrador) {
            return "/admin";
        }

        if (esCoordinador) {
            return "/coordinador";
        }

        if (esEntrenador) {
            return "/perfil";
        }

        if (esUsuario) {
            return "/inicio";
        }

        return "/perfil";
    }
}