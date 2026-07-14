package com.infinityfutbol.config;

import com.infinityfutbol.entity.Rol;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.UsuarioRol;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.repository.RolRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.repository.UsuarioRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.annotation.Order;

@Component
@Profile("dev")
@Order(1)
public class UsuarioPruebaInicializador implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioPruebaInicializador(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (usuarioRepository.existsById("USR-PRUEBA-001")) {
            System.out.println("El usuario de prueba ya existe");
            return;
        }

        Rol rolUsuario = rolRepository.findById("ROL-USU")
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el rol ROL-USU"
                ));

        Usuario usuario = new Usuario();
        usuario.setIdUsuario("USR-PRUEBA-001");
        usuario.setUsername("usuario.prueba");
        usuario.setPassword(
                passwordEncoder.encode("Usuario123*")
        );
        usuario.setCorreo(
                "usuario.prueba@infinityfutbol.pe"
        );
        usuario.setEstado(EstadoUsuario.ACTIVO);

        usuarioRepository.save(usuario);

        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setIdUsuarioRol("UROL-PRUEBA-001");
        asignacion.setUsuario(usuario);
        asignacion.setRol(rolUsuario);

        usuarioRolRepository.save(asignacion);

        System.out.println(
                "Usuario de prueba creado correctamente"
        );
    }
}