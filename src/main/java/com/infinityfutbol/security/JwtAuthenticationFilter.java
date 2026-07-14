package com.infinityfutbol.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtCookieService jwtCookieService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    private final SecurityContextLogoutHandler logoutHandler =
            new SecurityContextLogoutHandler();

    public JwtAuthenticationFilter(
            JwtCookieService jwtCookieService,
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtCookieService = jwtCookieService;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Optional<String> tokenOptional =
                jwtCookieService.obtenerToken(request);

        /*
         * Si existe una cookie JWT, siempre se vuelve a validar
         * contra la información actual de la base de datos.
         */
        if (tokenOptional.isPresent()) {

            boolean autenticado = autenticarDesdeToken(
                    tokenOptional.get(),
                    request
            );

            if (!autenticado) {
                invalidarAcceso(request, response);
            }

        } else {

            /*
             * Si no existe JWT, comprobamos la autenticación
             * recuperada desde JSESSIONID.
             */
            validarAutenticacionExistente(
                    request,
                    response
            );
        }

        filterChain.doFilter(request, response);
    }

    private boolean autenticarDesdeToken(
            String token,
            HttpServletRequest request
    ) {
        try {
            String username =
                    jwtService.extraerUsername(token);

            CustomUserDetails userDetails =
                    (CustomUserDetails)
                            customUserDetailsService
                                    .loadUserByUsername(username);

            boolean tokenValido =
                    jwtService.esTokenValido(
                            token,
                            userDetails
                    );

            if (!tokenValido || !userDetails.isEnabled()) {
                return false;
            }

            establecerAutenticacion(
                    userDetails,
                    request
            );

            return true;

        } catch (
                JwtException
                | UsernameNotFoundException
                | IllegalArgumentException exception
        ) {
            return false;
        }
    }

    private void validarAutenticacionExistente(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication
                instanceof AnonymousAuthenticationToken) {

            return;
        }

        try {
            CustomUserDetails userDetailsActualizados =
                    (CustomUserDetails)
                            customUserDetailsService
                                    .loadUserByUsername(
                                            authentication.getName()
                                    );

            if (!userDetailsActualizados.isEnabled()) {
                invalidarAcceso(request, response);
                return;
            }

            /*
             * También actualiza inmediatamente los roles
             * cuando fueron modificados mediante la HU30.
             */
            establecerAutenticacion(
                    userDetailsActualizados,
                    request
            );

        } catch (UsernameNotFoundException exception) {
            invalidarAcceso(request, response);
        }
    }

    private void establecerAutenticacion(
            CustomUserDetails userDetails,
            HttpServletRequest request
    ) {
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource()
                        .buildDetails(request)
        );

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private void invalidarAcceso(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        jwtCookieService.eliminarToken(response);

        if (authentication != null) {
            logoutHandler.logout(
                    request,
                    response,
                    authentication
            );

            return;
        }

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }
}