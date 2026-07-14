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

        provider.setPasswordEncoder(passwordEncoder);

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

        http
                .authenticationProvider(
                        authenticationProvider
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(authorize -> authorize

                        /*
                         * Permite los despachos internos generados
                         * cuando ocurre un error o un forward.
                         */
                        .dispatcherTypeMatchers(
                                DispatcherType.ERROR,
                                DispatcherType.FORWARD
                        )
                        .permitAll()

                        /*
                         * Rutas públicas.
                         */
                        .requestMatchers(
                                "/",
                                "/login",
                                "/registro",
                                "/error",
                                "/favicon.ico",

                                "/api/auth/registro",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/csrf",

                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        /*
                         * Rutas exclusivas del administrador.
                         */
                        .requestMatchers(
                                "/admin/**",
                                "/api/admin/**"
                        )
                        .hasRole("ADMINISTRADOR")

                        /*
                         * El resto necesita autenticación.
                         */
                        .anyRequest()
                        .authenticated()
                )

                .exceptionHandling(exception -> exception

                        /*
                         * Se ejecuta cuando el usuario no está autenticado.
                         */
                        .authenticationEntryPoint(
                                (request,
                                 response,
                                 authenticationException) -> {

                                    if (esSolicitudApi(request.getRequestURI())) {
                                        escribirRespuestaJson(
                                                response,
                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                "Debes iniciar sesión nuevamente"
                                        );

                                        return;
                                    }

                                    response.sendRedirect("/login");
                                }
                        )

                        /*
                         * Se ejecuta cuando está autenticado,
                         * pero no tiene el rol requerido.
                         */
                        .accessDeniedHandler(
                                (request,
                                 response,
                                 accessDeniedException) -> {

                                    if (esSolicitudApi(request.getRequestURI())) {
                                        escribirRespuestaJson(
                                                response,
                                                HttpServletResponse.SC_FORBIDDEN,
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

                /*
                 * El login se procesa mediante /api/auth/login.
                 */
                .formLogin(
                        AbstractHttpConfigurer::disable
                )

                /*
                 * El logout se procesa mediante /api/auth/logout.
                 */
                .logout(
                        AbstractHttpConfigurer::disable
                )

                /*
                 * El filtro consulta la cookie ACCESS_TOKEN
                 * antes de los filtros tradicionales.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
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

        response.setStatus(estado);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

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
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}