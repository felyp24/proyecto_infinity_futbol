package com.infinityfutbol.config;

import com.infinityfutbol.entity.Rol;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.UsuarioRol;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.repository.RolRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import com.infinityfutbol.repository.UsuarioRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;

@Component
@Profile("dev")
public class AdministradorInicializador implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorInicializador(
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

        System.out.println(
                "=== INICIALIZADOR DE ADMINISTRADOR EJECUTADO ==="
        );

        if (usuarioRepository.existsById("USR-ADMIN-001")) {
            System.out.println("El administrador temporal ya existe");
            return;
        }

        Rol rolAdministrador = rolRepository.findById("ROL-ADM")
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el rol ROL-ADM"
                ));

        Usuario administrador = new Usuario();
        administrador.setIdUsuario("USR-ADMIN-001");
        administrador.setUsername("admin");
        administrador.setPassword(
                passwordEncoder.encode("Admin123*")
        );
        administrador.setCorreo("admin@infinityfutbol.pe");
        administrador.setEstado(EstadoUsuario.ACTIVO);

        usuarioRepository.save(administrador);

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setIdUsuarioRol("UROL-ADMIN-001");
        usuarioRol.setUsuario(administrador);
        usuarioRol.setRol(rolAdministrador);

        usuarioRolRepository.save(usuarioRol);

        System.out.println(
                "Administrador temporal creado correctamente"
        );
    }
}