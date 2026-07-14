package com.infinityfutbol.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Service
public class JwtCookieService {

    private final String cookieName;
    private final long expirationMinutes;
    private final boolean cookieSecure;

    public JwtCookieService(
            @Value("${security.jwt.cookie-name}")
            String cookieName,

            @Value("${security.jwt.expiration-minutes}")
            long expirationMinutes,

            @Value("${security.jwt.cookie-secure}")
            boolean cookieSecure
    ) {
        this.cookieName = cookieName;
        this.expirationMinutes = expirationMinutes;
        this.cookieSecure = cookieSecure;
    }

    public void agregarToken(
            HttpServletResponse response,
            String token
    ) {
        ResponseCookie cookie = ResponseCookie
                .from(cookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(expirationMinutes))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    public Optional<String> obtenerToken(
            HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie ->
                        cookieName.equals(cookie.getName())
                )
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    public void eliminarToken(
            HttpServletResponse response
    ) {
        ResponseCookie cookie = ResponseCookie
                .from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }
}