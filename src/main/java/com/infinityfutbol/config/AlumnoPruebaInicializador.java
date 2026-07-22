package com.infinityfutbol.config;

import com.infinityfutbol.entity.Alumno;
import com.infinityfutbol.entity.CuentaCredito;
import com.infinityfutbol.entity.Usuario;
import com.infinityfutbol.entity.enums.EstadoAlumno;
import com.infinityfutbol.entity.enums.TipoDocumento;
import com.infinityfutbol.repository.AlumnoRepository;
import com.infinityfutbol.repository.CuentaCreditoRepository;
import com.infinityfutbol.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Profile("dev")
@Order(2)
public class AlumnoPruebaInicializador
        implements CommandLineRunner {

    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuentaCreditoRepository cuentaCreditoRepository;

    public AlumnoPruebaInicializador(
            AlumnoRepository alumnoRepository,
            UsuarioRepository usuarioRepository,
            CuentaCreditoRepository cuentaCreditoRepository
    ) {
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cuentaCreditoRepository =
                cuentaCreditoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Alumno alumno = alumnoRepository
                .findByUsuario_IdUsuario(
                        "USR-PRUEBA-001"
                )
                .orElseGet(this::crearAlumnoPrueba);

        crearCuentaCreditoSiNoExiste(alumno);
    }

    private Alumno crearAlumnoPrueba() {

        Usuario usuario = usuarioRepository
                .findById("USR-PRUEBA-001")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe el usuario de prueba"
                        )
                );

        Alumno alumno = new Alumno();

        alumno.setIdAlumno(
                "ALU-PRUEBA-001"
        );

        alumno.setUsuario(usuario);
        alumno.setNombres("Usuario");
        alumno.setApellidos("De Prueba");

        alumno.setTipoDocumento(
                TipoDocumento.DNI
        );

        alumno.setNumeroDocumento(
                "70000001"
        );

        alumno.setFechaNacimiento(
                LocalDate.of(
                        2000,
                        1,
                        15
                )
        );

        alumno.setTelefono(
                "999111222"
        );

        alumno.setEstado(
                EstadoAlumno.ACTIVO
        );

        Alumno alumnoGuardado =
                alumnoRepository.save(alumno);

        System.out.println(
                "Alumno de prueba creado correctamente"
        );

        return alumnoGuardado;
    }

    private void crearCuentaCreditoSiNoExiste(
            Alumno alumno
    ) {

        boolean cuentaYaExiste =
                cuentaCreditoRepository
                        .existsByAlumno_IdAlumno(
                                alumno.getIdAlumno()
                        );

        if (cuentaYaExiste) {
            System.out.println(
                    "La cuenta de créditos de prueba ya existe"
            );

            return;
        }

        CuentaCredito cuentaCredito =
                new CuentaCredito();

        cuentaCredito.setIdCuentaCredito(
                "CTC-PRUEBA-001"
        );

        cuentaCredito.setAlumno(alumno);

        /*
         * Crédito inicial exclusivo para pruebas.
         * Los usuarios registrados normalmente empiezan con 0.
         */
        cuentaCredito.setSaldoActual(5);

        cuentaCreditoRepository.save(
                cuentaCredito
        );

        System.out.println(
                "Cuenta de créditos de prueba creada con 5 créditos"
        );
    }
}