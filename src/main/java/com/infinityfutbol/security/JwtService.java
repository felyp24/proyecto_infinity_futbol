package com.infinityfutbol.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMinutes;
    private final String issuer;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes,
            @Value("${security.jwt.issuer}") String issuer
    ) {
        this.secret = secret;
        this.expirationMinutes = expirationMinutes;
        this.issuer = issuer;
    }

    public String generarToken(CustomUserDetails userDetails) {

        Instant ahora = Instant.now();
        Instant expiracion = ahora.plus(
                expirationMinutes,
                ChronoUnit.MINUTES
        );

        return Jwts.builder()
                .issuer(issuer)
                .subject(userDetails.getIdUsuario())
                .claim("username", userDetails.getUsername())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(obtenerClaveFirma())
                .compact();
    }

    public String extraerIdUsuario(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).get(
                "username",
                String.class
        );
    }

    public boolean esTokenValido(
            String token,
            CustomUserDetails userDetails
    ) {
        try {
            Claims claims = extraerClaims(token);

            String idUsuario = claims.getSubject();
            String username = claims.get(
                    "username",
                    String.class
            );

            return idUsuario.equals(userDetails.getIdUsuario())
                    && username.equals(userDetails.getUsername())
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClaveFirma())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obtenerClaveFirma() {
        byte[] claveDecodificada =
                Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(claveDecodificada);
    }
}