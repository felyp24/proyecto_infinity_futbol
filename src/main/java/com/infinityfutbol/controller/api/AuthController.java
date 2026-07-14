package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.request.RegistroUsuarioRequest;
import com.infinityfutbol.dto.response.RegistroUsuarioResponse;
import com.infinityfutbol.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.infinityfutbol.dto.request.LoginRequest;
import com.infinityfutbol.dto.response.LoginResponse;
import com.infinityfutbol.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import com.infinityfutbol.dto.response.LogoutResponse;
import com.infinityfutbol.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginService loginService;
    private final RegistroService registroService;
    private final LogoutService logoutService;

    public AuthController(
            RegistroService registroService,
            LoginService loginService,
            LogoutService logoutService
    ) {
        this.registroService = registroService;
        this.loginService = loginService;
        this.logoutService = logoutService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroUsuarioResponse registrarUsuario(
            @Valid
            @RequestBody
            RegistroUsuarioRequest request
    ) {
        return registroService.registrarUsuario(request);
    }

    @PostMapping("/login")
    public LoginResponse iniciarSesion(
            @Valid
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        return loginService.iniciarSesion(
                request,
                response
        );
    }

    @PostMapping("/logout")
    public LogoutResponse cerrarSesion(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return logoutService.cerrarSesion(
                request,
                response
        );
    }
}