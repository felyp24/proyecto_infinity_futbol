package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.PerfilResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.repository.AlumnoRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.repository.UsuarioRolRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.infinityfutbol.dto.request.ActualizarPerfilRequest;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public PerfilService(
            UsuarioRepository usuarioRepository,
            AlumnoRepository alumnoRepository,
            UsuarioRolRepository usuarioRolRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoRepository = alumnoRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    public PerfilResponse obtenerPerfil(String idUsuario) {

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe el usuario autenticado"
                        )
                );

        Alumno alumno = alumnoRepository
                .findByUsuario_IdUsuario(idUsuario)
                .orElse(null);

        List<String> roles = usuarioRolRepository
                .findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(asignacion ->
                        asignacion.getRol().getNombreRol()
                )
                .distinct()
                .toList();

        return convertirPerfilResponse(
                usuario,
                alumno,
                roles
        );
    }

    private PerfilResponse convertirPerfilResponse(
            Usuario usuario,
            Alumno alumno,
            List<String> roles
    ) {
        return new PerfilResponse(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getEstado(),
                usuario.getFechaCreacion(),
                usuario.getUltimoAcceso(),

                alumno != null
                        ? alumno.getIdAlumno()
                        : null,

                alumno != null
                        ? alumno.getNombres()
                        : null,

                alumno != null
                        ? alumno.getApellidos()
                        : null,

                alumno != null
                        ? alumno.getTipoDocumento()
                        : null,

                alumno != null
                        ? alumno.getNumeroDocumento()
                        : null,

                alumno != null
                        ? alumno.getFechaNacimiento()
                        : null,

                alumno != null
                        ? alumno.getTelefono()
                        : null,

                alumno != null
                        ? alumno.getEstado()
                        : null,

                roles
        );
    }
    @Transactional
    public PerfilResponse actualizarPerfil(
            String idUsuario,
            ActualizarPerfilRequest request
    ) {
        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe el usuario autenticado"
                        )
                );

        Alumno alumno = alumnoRepository
                .findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "La cuenta no tiene un perfil de alumno asociado"
                        )
                );

        String numeroDocumento =
                request.numeroDocumento().trim();

        boolean documentoOcupado =
                alumnoRepository
                        .existsByNumeroDocumentoAndIdAlumnoNot(
                                numeroDocumento,
                                alumno.getIdAlumno()
                        );

        if (documentoOcupado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El número de documento ya pertenece a otro alumno"
            );
        }

        alumno.setNombres(request.nombres().trim());
        alumno.setApellidos(request.apellidos().trim());

        alumno.setTipoDocumento(
                request.tipoDocumento()
        );

        alumno.setNumeroDocumento(numeroDocumento);
        alumno.setFechaNacimiento(request.fechaNacimiento());

        alumno.setTelefono(
                normalizarTextoOpcional(request.telefono())
        );

        List<String> roles = usuarioRolRepository
                .findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(asignacion ->
                        asignacion.getRol().getNombreRol()
                )
                .distinct()
                .toList();

        return convertirPerfilResponse(
                usuario,
                alumno,
                roles
        );
    }
    private String normalizarTextoOpcional(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}