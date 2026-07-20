package com.infinityfutbol.config;

import com.infinityfutbol.entity.Cancha;
import com.infinityfutbol.entity.Distrito;
import com.infinityfutbol.entity.Entrenador;
import com.infinityfutbol.entity.Rol;
import com.infinityfutbol.entity.Sede;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.UsuarioRol;
import com.infinityfutbol.entity.enums.EstadoCancha;
import com.infinityfutbol.entity.enums.EstadoEntrenador;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.repository.CanchaRepository;
import com.infinityfutbol.repository.DistritoRepository;
import com.infinityfutbol.repository.EntrenadorRepository;
import com.infinityfutbol.repository.RolRepository;
import com.infinityfutbol.repository.SedeRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.repository.UsuarioRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@Order(3)
public class DatosClasesPruebaInicializador
        implements CommandLineRunner {

    private final DistritoRepository distritoRepository;
    private final SedeRepository sedeRepository;
    private final CanchaRepository canchaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosClasesPruebaInicializador(
            DistritoRepository distritoRepository,
            SedeRepository sedeRepository,
            CanchaRepository canchaRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            EntrenadorRepository entrenadorRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.distritoRepository = distritoRepository;
        this.sedeRepository = sedeRepository;
        this.canchaRepository = canchaRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crearSedeYCancha();
        crearCoordinador();
        crearEntrenador();
    }

    private void crearSedeYCancha() {
        Distrito distrito = distritoRepository
                .findById("JM")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el distrito JM"
                        )
                );

        Sede sede = sedeRepository
                .findById("SED-JM-001")
                .orElseGet(() -> {
                    Sede nuevaSede = new Sede();

                    nuevaSede.setIdSede("SED-JM-001");
                    nuevaSede.setNombre("Sede Jesús María");
                    nuevaSede.setDireccion(
                            "Av. Principal 123"
                    );
                    nuevaSede.setReferencia(
                            "Cerca del parque principal"
                    );
                    nuevaSede.setDistrito(distrito);
                    nuevaSede.setEstado(true);

                    return sedeRepository.save(nuevaSede);
                });

        if (!canchaRepository.existsById("CAN-JM-001")) {
            Cancha cancha = new Cancha();

            cancha.setIdCancha("CAN-JM-001");
            cancha.setNumeroCancha(1);
            cancha.setTipoSuperficie("Sintética");
            cancha.setEstado(EstadoCancha.DISPONIBLE);
            cancha.setSede(sede);

            canchaRepository.save(cancha);
        }
    }

    private void crearCoordinador() {
        Usuario coordinador = usuarioRepository
                .findById("USR-COORD-001")
                .orElseGet(() -> {
                    Usuario usuario = new Usuario();

                    usuario.setIdUsuario("USR-COORD-001");
                    usuario.setUsername("coordinador");
                    usuario.setCorreo(
                            "coordinador@infinityfutbol.pe"
                    );
                    usuario.setPassword(
                            passwordEncoder.encode(
                                    "Coordinador123*"
                            )
                    );
                    usuario.setEstado(EstadoUsuario.ACTIVO);

                    return usuarioRepository.save(usuario);
                });

        Rol rolCoordinador = rolRepository
                .findByNombreRolIgnoreCase("COORDINADOR")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el rol COORDINADOR"
                        )
                );

        asignarRol(
                coordinador,
                rolCoordinador,
                "UROL-COORD-001"
        );
    }

    private void crearEntrenador() {
        Usuario usuarioEntrenador = usuarioRepository
                .findById("USR-ENT-001")
                .orElseGet(() -> {
                    Usuario usuario = new Usuario();

                    usuario.setIdUsuario("USR-ENT-001");
                    usuario.setUsername("entrenador");
                    usuario.setCorreo(
                            "entrenador@infinityfutbol.pe"
                    );
                    usuario.setPassword(
                            passwordEncoder.encode(
                                    "Entrenador123*"
                            )
                    );
                    usuario.setEstado(EstadoUsuario.ACTIVO);

                    return usuarioRepository.save(usuario);
                });

        Rol rolEntrenador = rolRepository
                .findByNombreRolIgnoreCase("ENTRENADOR")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el rol ENTRENADOR"
                        )
                );

        asignarRol(
                usuarioEntrenador,
                rolEntrenador,
                "UROL-ENT-001"
        );

        if (!entrenadorRepository.existsById(
                "ENT-PRUEBA-001"
        )) {
            Entrenador entrenador = new Entrenador();

            entrenador.setIdEntrenador(
                    "ENT-PRUEBA-001"
            );
            entrenador.setUsuario(usuarioEntrenador);
            entrenador.setNombres("Miguel Ángel");
            entrenador.setApellidos("Gómez Torres");
            entrenador.setTelefono("987654321");
            entrenador.setEspecialidad(
                    "Entrenamiento técnico"
            );
            entrenador.setEstado(
                    EstadoEntrenador.ACTIVO
            );

            entrenadorRepository.save(entrenador);
        }
    }

    private void asignarRol(
            Usuario usuario,
            Rol rol,
            String idUsuarioRol
    ) {
        boolean yaTieneRol =
                usuarioRolRepository
                        .existsByUsuario_IdUsuarioAndRol_IdRol(
                                usuario.getIdUsuario(),
                                rol.getIdRol()
                        );

        if (yaTieneRol) {
            return;
        }

        UsuarioRol usuarioRol = new UsuarioRol();

        usuarioRol.setIdUsuarioRol(idUsuarioRol);
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);

        usuarioRolRepository.save(usuarioRol);
    }
}