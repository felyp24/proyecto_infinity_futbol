package com.infinityfutbol.config;

import com.infinityfutbol.security.CustomUserDetailsService;
import com.infinityfutbol.security.JwtAuthenticationFilter;
import com.infinityfutbol.security.JwtCookieService;
import com.infinityfutbol.security.JwtService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        customUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {

        return authenticationConfiguration
                .getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            JwtCookieService jwtCookieService,
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService
    ) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtCookieService,
                        jwtService,
                        customUserDetailsService
                );

        CookieCsrfTokenRepository csrfTokenRepository =
                CookieCsrfTokenRepository
                        .withHttpOnlyFalse();

        csrfTokenRepository.setCookiePath("/");

        /*
         * Usamos un matcher explícito basado en la URI.
         * De esta manera /login siempre será reconocida
         * como ruta pública.
         */
        RequestMatcher rutasPublicas =
                request -> esRutaPublica(
                        request.getRequestURI()
                );

        http
                .authenticationProvider(
                        authenticationProvider
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                csrfTokenRepository
                        )
                )

                .authorizeHttpRequests(authorize ->
                        authorize

                                /*
                                 * Permitir errores y redirecciones internas.
                                 */
                                .dispatcherTypeMatchers(
                                        DispatcherType.ERROR,
                                        DispatcherType.FORWARD
                                )
                                .permitAll()

                                /*
                                 * Login, registro, autenticación,
                                 * CSRF y archivos estáticos.
                                 */
                                .requestMatchers(
                                        rutasPublicas
                                )
                                .permitAll()

                                /*
                                 * Administrador.
                                 */
                                .requestMatchers(
                                        "/admin",
                                        "/admin/**",
                                        "/api/admin/**"
                                )
                                .hasRole(
                                        "ADMINISTRADOR"
                                )

                                /*
                                 * Coordinador y administrador.
                                 */
                                .requestMatchers(
                                        "/coordinador",
                                        "/coordinador/**",
                                        "/api/coordinador/**"
                                )
                                .hasAnyRole(
                                        "COORDINADOR",
                                        "ADMINISTRADOR"
                                )

                                /*
                                 * Cliente.
                                 */
                                .requestMatchers(
                                        "/inicio",
                                        "/inicio/**",
                                        "/api/inicio/**"
                                )
                                .hasRole(
                                        "USUARIO"
                                )

                                /*
                                 * Perfil y otras rutas autenticadas.
                                 */
                                .anyRequest()
                                .authenticated()
                )

                .exceptionHandling(exception ->
                        exception

                                .authenticationEntryPoint(
                                        (
                                                request,
                                                response,
                                                authenticationException
                                        ) -> {

                                            String ruta =
                                                    request.getRequestURI();

                                            /*
                                             * Protección adicional:
                                             * una ruta pública nunca debe
                                             * redirigirse hacia ella misma.
                                             */
                                            if (esRutaPublica(ruta)) {
                                                response.setStatus(
                                                        HttpServletResponse
                                                                .SC_UNAUTHORIZED
                                                );

                                                return;
                                            }

                                            if (esSolicitudApi(ruta)) {
                                                escribirRespuestaJson(
                                                        response,
                                                        HttpServletResponse
                                                                .SC_UNAUTHORIZED,
                                                        "Debes iniciar sesión nuevamente"
                                                );

                                                return;
                                            }

                                            response.sendRedirect(
                                                    "/login"
                                            );
                                        }
                                )

                                .accessDeniedHandler(
                                        (
                                                request,
                                                response,
                                                accessDeniedException
                                        ) -> {

                                            String ruta =
                                                    request.getRequestURI();

                                            if (esSolicitudApi(ruta)) {
                                                escribirRespuestaJson(
                                                        response,
                                                        HttpServletResponse
                                                                .SC_FORBIDDEN,
                                                        "No tienes permiso para realizar esta operación"
                                                );

                                                return;
                                            }

                                            response.sendRedirect(
                                                    "/perfil?acceso=denegado"
                                            );
                                        }
                                )
                )


                .formLogin(
                        AbstractHttpConfigurer::disable
                )


                .logout(
                        AbstractHttpConfigurer::disable
                )

                /*
                 * Filtro JWT.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    private static boolean esRutaPublica(
            String ruta
    ) {
        if (ruta == null) {
            return false;
        }

        return ruta.equals("/")
                || ruta.equals("/login")
                || ruta.equals("/registro")
                || ruta.equals("/error")
                || ruta.equals("/favicon.ico")

                || ruta.equals("/api/auth/login")
                || ruta.equals("/api/auth/registro")
                || ruta.equals("/api/auth/logout")
                || ruta.equals("/api/csrf")

                || ruta.startsWith("/css/")
                || ruta.startsWith("/js/")
                || ruta.startsWith("/images/")
                || ruta.startsWith("/webjars/");
    }

    private static boolean esSolicitudApi(
            String ruta
    ) {
        return ruta != null
                && ruta.startsWith("/api/");
    }

    private static void escribirRespuestaJson(
            HttpServletResponse response,
            int estado,
            String detalle
    ) throws java.io.IOException {

        response.setStatus(
                estado
        );

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        response.getWriter().write(
                """
                {
                  "status": %d,
                  "detail": "%s"
                }
                """.formatted(
                        estado,
                        escaparJson(detalle)
                )
        );
    }

    private static String escaparJson(
            String texto
    ) {
        return texto
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                );
    }
}