package com.infinityfutbol.config;

import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.repository.AlumnoRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.infinityfutbol.entity.enums.TipoDocumento;

import java.time.LocalDate;

@Component
@Profile("dev")
@Order(2)
public class AlumnoPruebaInicializador implements CommandLineRunner {

    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;

    public AlumnoPruebaInicializador(
            AlumnoRepository alumnoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        boolean alumnoYaExiste = alumnoRepository
                .findByUsuario_IdUsuario("USR-PRUEBA-001")
                .isPresent();

        if (alumnoYaExiste) {
            System.out.println(
                    "El alumno de prueba ya existe"
            );
            return;
        }

        Usuario usuario = usuarioRepository
                .findById("USR-PRUEBA-001")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el usuario de prueba"
                        )
                );

        Alumno alumno = new Alumno();
        alumno.setIdAlumno("ALU-PRUEBA-001");
        alumno.setUsuario(usuario);
        alumno.setNombres("Usuario");
        alumno.setApellidos("De Prueba");
        alumno.setTipoDocumento(
                TipoDocumento.DNI
        );
        alumno.setNumeroDocumento("70000001");
        alumno.setFechaNacimiento(
                LocalDate.of(2000, 1, 15)
        );
        alumno.setTelefono("999111222");
        alumno.setEstado(EstadoAlumno.ACTIVO);

        alumnoRepository.save(alumno);

        System.out.println(
                "Alumno de prueba creado correctamente"
        );
    }
}