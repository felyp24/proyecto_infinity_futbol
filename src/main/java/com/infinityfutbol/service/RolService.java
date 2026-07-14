package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.RolResponse;
import com.infinityfutbol.dto.response.UsuarioRolResponse;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.UsuarioRol;
import com.infinityfutbol.repository.RolRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.repository.UsuarioRolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.infinityfutbol.dto.request.CambiarRolRequest;
import com.infinityfutbol.entity.Rol;
import java.util.UUID;
import com.infinityfutbol.entity.enums.NombreRol;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RolService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public RolService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    public List<UsuarioRolResponse> listarUsuariosConRoles() {

        List<Usuario> usuarios = usuarioRepository.findAll();

        List<UsuarioRol> asignaciones =
                usuarioRolRepository.findAll();

        Map<String, List<String>> rolesPorUsuario =
                asignaciones.stream()
                        .collect(Collectors.groupingBy(
                                asignacion ->
                                        asignacion
                                                .getUsuario()
                                                .getIdUsuario(),

                                Collectors.mapping(
                                        asignacion ->
                                                asignacion
                                                        .getRol()
                                                        .getNombreRol(),

                                        Collectors.toList()
                                )
                        ));

        return usuarios.stream()
                .map(usuario ->
                        convertirUsuarioResponse(
                                usuario,
                                rolesPorUsuario.getOrDefault(
                                        usuario.getIdUsuario(),
                                        List.of()
                                )
                        )
                )
                .toList();
    }

    public List<RolResponse> listarRolesActivos() {

        return rolRepository
                .findByEstadoTrueOrderByNombreRolAsc()
                .stream()
                .map(rol -> new RolResponse(
                        rol.getIdRol(),
                        rol.getNombreRol(),
                        rol.getDescripcion()
                ))
                .toList();
    }
    @Transactional
    public UsuarioRolResponse cambiarRol(
            String idUsuario,
            CambiarRolRequest request,
            String idUsuarioAutenticado
    ) {
        boolean modificaSuPropioUsuario =
                idUsuario.equals(idUsuarioAutenticado);

        boolean retiraRolAdministrador =
                request.rol() != NombreRol.ADMINISTRADOR;

        if (modificaSuPropioUsuario && retiraRolAdministrador) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No puedes retirar tu propio rol de administrador"
            );
        }

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe el usuario con ID: " + idUsuario
                        )
                );

        Rol nuevoRol = rolRepository
                .findByNombreRolIgnoreCase(request.rol().name())
                .filter(rol -> Boolean.TRUE.equals(rol.getEstado()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El rol seleccionado no existe o está inactivo"
                        )
                );

        List<UsuarioRol> asignacionesActuales =
                usuarioRolRepository
                        .findByUsuario_IdUsuario(idUsuario);

        boolean yaTieneEseRol =
                asignacionesActuales.size() == 1
                        && asignacionesActuales
                        .getFirst()
                        .getRol()
                        .getIdRol()
                        .equals(nuevoRol.getIdRol());

        if (!yaTieneEseRol) {

            usuarioRolRepository
                    .deleteByUsuario_IdUsuario(idUsuario);


            usuarioRolRepository.flush();

            UsuarioRol nuevaAsignacion = new UsuarioRol();
            nuevaAsignacion.setIdUsuarioRol(
                    generarIdUsuarioRol()
            );
            nuevaAsignacion.setUsuario(usuario);
            nuevaAsignacion.setRol(nuevoRol);

            usuarioRolRepository.save(nuevaAsignacion);
        }

        return convertirUsuarioResponse(
                usuario,
                List.of(nuevoRol.getNombreRol())
        );
    }

    private UsuarioRolResponse convertirUsuarioResponse(
            Usuario usuario,
            List<String> roles
    ) {
        return new UsuarioRolResponse(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getEstado(),
                usuario.getFechaCreacion(),
                roles
        );
    }
    private String generarIdUsuarioRol() {

        String idUsuarioRol;

        do {
            String uuid = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 20)
                    .toUpperCase();

            idUsuarioRol = "UROL-" + uuid;

        } while (
                usuarioRolRepository.existsById(idUsuarioRol)
        );

        return idUsuarioRol;
    }
}