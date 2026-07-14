package com.infinityfutbol.service;

import com.infinityfutbol.dto.request.RegistroUsuarioRequest;
import com.infinityfutbol.dto.response.RegistroUsuarioResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Rol;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.UsuarioRol;
import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.entity.enums.NombreRol;
import com.infinityfutbol.repository.AlumnoRepository;
import com.infinityfutbol.repository.RolRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.repository.UsuarioRolRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroService(
            UsuarioRepository usuarioRepository,
            AlumnoRepository alumnoRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoRepository = alumnoRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegistroUsuarioResponse registrarUsuario(
            RegistroUsuarioRequest request
    ) {
        String username =
                request.username().trim();

        String correo =
                request.correo()
                        .trim()
                        .toLowerCase();

        String numeroDocumento =
                request.numeroDocumento().trim();

        validarPasswords(request);
        validarUsernameDisponible(username);
        validarCorreoDisponible(correo);
        validarDocumentoDisponible(numeroDocumento);

        Rol rolUsuario = buscarRolUsuario();

        String codigoGenerado =
                generarCodigoAleatorio();

        Usuario usuario = crearUsuario(
                request,
                username,
                correo,
                codigoGenerado
        );

        usuarioRepository.save(usuario);

        Alumno alumno = crearAlumno(
                request,
                numeroDocumento,
                codigoGenerado,
                usuario
        );

        alumnoRepository.save(alumno);

        UsuarioRol usuarioRol = crearUsuarioRol(
                usuario,
                rolUsuario
        );

        usuarioRolRepository.save(usuarioRol);

        return new RegistroUsuarioResponse(
                usuario.getIdUsuario(),
                alumno.getIdAlumno(),
                usuario.getUsername(),
                usuario.getCorreo(),
                "El usuario fue registrado correctamente"
        );
    }

    private Usuario crearUsuario(
            RegistroUsuarioRequest request,
            String username,
            String correo,
            String codigoGenerado
    ) {
        Usuario usuario = new Usuario();

        usuario.setIdUsuario(
                "USR-" + codigoGenerado
        );

        usuario.setUsername(username);
        usuario.setCorreo(correo);

        usuario.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        usuario.setEstado(
                EstadoUsuario.ACTIVO
        );

        return usuario;
    }

    private Alumno crearAlumno(
            RegistroUsuarioRequest request,
            String numeroDocumento,
            String codigoGenerado,
            Usuario usuario
    ) {
        Alumno alumno = new Alumno();

        alumno.setIdAlumno(
                "ALU-" + codigoGenerado
        );

        alumno.setUsuario(usuario);

        alumno.setNombres(
                request.nombres().trim()
        );

        alumno.setApellidos(
                request.apellidos().trim()
        );

        alumno.setTipoDocumento(
                request.tipoDocumento()
        );

        alumno.setNumeroDocumento(
                numeroDocumento
        );

        alumno.setFechaNacimiento(
                request.fechaNacimiento()
        );

        alumno.setTelefono(
                limpiarTextoOpcional(
                        request.telefono()
                )
        );

        alumno.setEstado(
                EstadoAlumno.ACTIVO
        );

        return alumno;
    }

    private UsuarioRol crearUsuarioRol(
            Usuario usuario,
            Rol rol
    ) {
        UsuarioRol usuarioRol = new UsuarioRol();

        usuarioRol.setIdUsuarioRol(
                generarIdUsuarioRol()
        );

        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);

        return usuarioRol;
    }

    private void validarPasswords(
            RegistroUsuarioRequest request
    ) {
        if (!request.password().equals(
                request.confirmarPassword()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las contraseñas no coinciden"
            );
        }
    }

    private void validarUsernameDisponible(
            String username
    ) {
        if (usuarioRepository
                .existsByUsernameIgnoreCase(username)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya está registrado"
            );
        }
    }

    private void validarCorreoDisponible(
            String correo
    ) {
        if (usuarioRepository
                .existsByCorreoIgnoreCase(correo)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado"
            );
        }
    }

    private void validarDocumentoDisponible(
            String numeroDocumento
    ) {
        if (alumnoRepository
                .existsByNumeroDocumento(numeroDocumento)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El número de documento ya está registrado"
            );
        }
    }

    private Rol buscarRolUsuario() {
        Rol rol = rolRepository
                .findByNombreRolIgnoreCase(
                        NombreRol.USUARIO.name()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "No se encontró el rol predeterminado USUARIO"
                        )
                );

        if (!Boolean.TRUE.equals(rol.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "El rol predeterminado USUARIO está inactivo"
            );
        }

        return rol;
    }

    private String generarCodigoAleatorio() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    private String generarIdUsuarioRol() {
        return "UROL-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }

    private String limpiarTextoOpcional(
            String texto
    ) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}