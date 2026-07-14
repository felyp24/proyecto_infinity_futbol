package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.LogoutResponse;
import com.infinityfutbol.security.JwtCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    private final JwtCookieService jwtCookieService;

    public LogoutService(
            JwtCookieService jwtCookieService
    ) {
        this.jwtCookieService = jwtCookieService;
    }

    public LogoutResponse cerrarSesion(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        jwtCookieService.eliminarToken(response);

        SecurityContextHolder.clearContext();

        HttpSession session =
                request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return new LogoutResponse(
                "La sesión fue cerrada correctamente",
                "/login?logout=exitoso"
        );
    }
}