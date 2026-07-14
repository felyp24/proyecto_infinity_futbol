package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.AlumnoListaResponse;
import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.repository.AlumnoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.infinityfutbol.dto.request.ActualizarAlumnoAdminRequest;
import com.infinityfutbol.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.infinityfutbol.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;

    public AlumnoService(
            AlumnoRepository alumnoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<AlumnoListaResponse> listarAlumnos(
            Pageable pageable
    ) {
        return alumnoRepository
                .findAll(pageable)
                .map(this::convertirListaResponse);
    }

    private AlumnoListaResponse convertirListaResponse(
            Alumno alumno
    ) {
        return new AlumnoListaResponse(
                alumno.getIdAlumno(),
                alumno.getUsuario().getIdUsuario(),

                alumno.getNombres(),
                alumno.getApellidos(),

                alumno.getTipoDocumento(),
                alumno.getNumeroDocumento(),

                alumno.getFechaNacimiento(),
                alumno.getTelefono(),

                alumno.getUsuario().getCorreo(),

                alumno.getEstado(),
                alumno.getUsuario().getEstado(),

                alumno.getFechaRegistro()
        );
    }

    public AlumnoListaResponse obtenerAlumno(
            String idAlumno
    ) {
        Alumno alumno = buscarAlumno(idAlumno);

        return convertirListaResponse(alumno);
    }

    @Transactional
    public AlumnoListaResponse actualizarAlumno(
            String idAlumno,
            ActualizarAlumnoAdminRequest request
    ) {
        Alumno alumno = buscarAlumno(idAlumno);
        Usuario usuario = alumno.getUsuario();

        String numeroDocumento =
                request.numeroDocumento().trim();

        String correo =
                request.correo().trim().toLowerCase();

        validarDocumentoDisponible(
                numeroDocumento,
                idAlumno
        );

        validarCorreoDisponible(
                correo,
                usuario.getIdUsuario()
        );

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
                limpiarTextoOpcional(request.telefono())
        );

        usuario.setCorreo(correo);

        return convertirListaResponse(alumno);
    }
    private Alumno buscarAlumno(
            String idAlumno
    ) {
        return alumnoRepository
                .findById(idAlumno)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe el alumno solicitado"
                        )
                );
    }

    private void validarDocumentoDisponible(
            String numeroDocumento,
            String idAlumno
    ) {
        boolean documentoOcupado =
                alumnoRepository
                        .existsByNumeroDocumentoAndIdAlumnoNot(
                                numeroDocumento,
                                idAlumno
                        );

        if (documentoOcupado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El número de documento ya está registrado"
            );
        }
    }
    private void validarCorreoDisponible(
            String correo,
            String idUsuario
    ) {
        boolean correoOcupado =
                usuarioRepository
                        .existsByCorreoIgnoreCaseAndIdUsuarioNot(
                                correo,
                                idUsuario
                        );

        if (correoOcupado) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado por otro usuario"
            );
        }
    }
    private String limpiarTextoOpcional(
            String texto
    ) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    public Page<AlumnoListaResponse> buscarAlumnos(
            String texto,
            Pageable pageable
    ) {
        String criterio =
                texto == null
                        ? ""
                        : texto.trim();

        if (criterio.isBlank()) {
            return listarAlumnos(pageable);
        }

        return alumnoRepository
                .buscar(criterio, pageable)
                .map(this::convertirListaResponse);
    }
}