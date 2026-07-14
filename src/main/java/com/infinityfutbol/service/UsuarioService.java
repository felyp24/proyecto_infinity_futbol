package com.infinityfutbol.service;

import com.infinityfutbol.dto.request.ActualizarCredencialesRequest;
import com.infinityfutbol.dto.response.CredencialesResponse;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.infinityfutbol.dto.request.CambiarEstadoUsuarioRequest;
import com.infinityfutbol.dto.response.UsuarioEstadoResponse;
import com.infinityfutbol.entity.enums.EstadoUsuario;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CredencialesResponse actualizarCredenciales(
            String idUsuario,
            ActualizarCredencialesRequest request
    ) {
        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe el usuario autenticado"
                        )
                );

        validarPasswordActual(
                request.passwordActual(),
                usuario.getPassword()
        );

        String nuevoUsername =
                request.nuevoUsername().trim();

        validarUsernameDisponible(
                nuevoUsername,
                idUsuario
        );

        boolean cambioUsername =
                !usuario.getUsername()
                        .equals(nuevoUsername);

        boolean cambioPassword =
                seSolicitoCambioPassword(request);

        if (!cambioUsername && !cambioPassword) {
            return new CredencialesResponse(
                    usuario.getIdUsuario(),
                    usuario.getUsername(),
                    "No se detectaron cambios en las credenciales",
                    false
            );
        }

        if (cambioUsername) {
            usuario.setUsername(nuevoUsername);
        }

        if (cambioPassword) {
            validarNuevaPassword(request);

            usuario.setPassword(
                    passwordEncoder.encode(
                            request.nuevaPassword()
                    )
            );
        }

        usuarioRepository.save(usuario);

        return new CredencialesResponse(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                "Las credenciales se actualizaron correctamente",
                true
        );
    }

    private void validarPasswordActual(
            String passwordActual,
            String passwordGuardada
    ) {
        boolean coincide = passwordEncoder.matches(
                passwordActual,
                passwordGuardada
        );

        if (!coincide) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "La contraseña actual es incorrecta"
            );
        }
    }

    private void validarUsernameDisponible(
            String nuevoUsername,
            String idUsuario
    ) {
        boolean usernameOcupado =
                usuarioRepository
                        .existsByUsernameIgnoreCaseAndIdUsuarioNot(
                                nuevoUsername,
                                idUsuario
                        );

        if (usernameOcupado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya está registrado"
            );
        }
    }

    private boolean seSolicitoCambioPassword(
            ActualizarCredencialesRequest request
    ) {
        return tieneContenido(request.nuevaPassword())
                || tieneContenido(request.confirmarPassword());
    }

    private void validarNuevaPassword(
            ActualizarCredencialesRequest request
    ) {
        String nuevaPassword = request.nuevaPassword();
        String confirmarPassword = request.confirmarPassword();

        if (!tieneContenido(nuevaPassword)
                || !tieneContenido(confirmarPassword)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe ingresar y confirmar la nueva contraseña"
            );
        }

        if (nuevaPassword.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nueva contraseña debe tener al menos 8 caracteres"
            );
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nueva contraseña y su confirmación no coinciden"
            );
        }
    }

    private boolean tieneContenido(String texto) {
        return texto != null && !texto.isBlank();
    }
    @Transactional
    public UsuarioEstadoResponse cambiarEstadoUsuario(
            String idUsuario,
            CambiarEstadoUsuarioRequest request,
            String idUsuarioAutenticado
    ) {
        boolean intentaDeshabilitarSuPropiaCuenta =
                idUsuario.equals(idUsuarioAutenticado)
                        && request.estado() == EstadoUsuario.INACTIVO;

        if (intentaDeshabilitarSuPropiaCuenta) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No puedes deshabilitar tu propia cuenta"
            );
        }

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe el usuario solicitado"
                        )
                );

        EstadoUsuario nuevoEstado = request.estado();

        if (usuario.getEstado() == nuevoEstado) {
            return new UsuarioEstadoResponse(
                    usuario.getIdUsuario(),
                    usuario.getUsername(),
                    usuario.getEstado(),
                    obtenerMensajeSinCambios(nuevoEstado)
            );
        }

        usuario.setEstado(nuevoEstado);

        return new UsuarioEstadoResponse(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getEstado(),
                obtenerMensajeActualizacion(nuevoEstado)
        );
    }
    private String obtenerMensajeActualizacion(
            EstadoUsuario estado
    ) {
        if (estado == EstadoUsuario.INACTIVO) {
            return "El usuario fue deshabilitado correctamente";
        }

        return "El usuario fue habilitado correctamente";
    }

    private String obtenerMensajeSinCambios(
            EstadoUsuario estado
    ) {
        if (estado == EstadoUsuario.INACTIVO) {
            return "El usuario ya se encuentra deshabilitado";
        }

        return "El usuario ya se encuentra habilitado";
    }
}