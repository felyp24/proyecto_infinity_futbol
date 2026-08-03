package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Asistencia;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository
        extends JpaRepository<Asistencia, String> {

    /*
     * Obtiene las asistencias ya registradas
     * de una clase determinada.
     */
    @EntityGraph(attributePaths = {
            "reserva",
            "reserva.alumno",
            "reserva.alumno.usuario",
            "reserva.clase"
    })
    List<Asistencia>
    findByReserva_Clase_IdClase(
            String idClase
    );

    /*
     * Permite mostrar cuántas asistencias
     * ya fueron marcadas en una clase.
     */
    long countByReserva_Clase_IdClase(
            String idClase
    );

    /*
     * Consulta utilizada por el reporte
     * administrativo de asistencia.
     */
    @EntityGraph(attributePaths = {
            "reserva",
            "reserva.alumno",
            "reserva.alumno.usuario",
            "reserva.clase",
            "reserva.clase.cancha",
            "reserva.clase.cancha.sede",
            "reserva.clase.entrenador"
    })
    @Query("""
        SELECT a
        FROM Asistencia a
        WHERE a.reserva.clase.fechaClase
              BETWEEN :fechaInicio AND :fechaFin

          AND (
                :texto = ''

                OR LOWER(a.reserva.alumno.nombres)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(a.reserva.alumno.apellidos)
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(
                    CONCAT(
                        a.reserva.alumno.nombres,
                        CONCAT(
                            ' ',
                            a.reserva.alumno.apellidos
                        )
                    )
                )
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(
                    a.reserva.alumno.numeroDocumento
                )
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(
                    a.reserva.alumno.usuario.username
                )
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(
                    a.reserva.clase.titulo
                )
                    LIKE LOWER(CONCAT('%', :texto, '%'))

                OR LOWER(
                    a.reserva.clase.cancha.sede.nombre
                )
                    LIKE LOWER(CONCAT('%', :texto, '%'))
          )

        ORDER BY
            a.reserva.clase.fechaClase DESC,
            a.reserva.clase.horaInicio DESC,
            a.reserva.alumno.apellidos ASC,
            a.reserva.alumno.nombres ASC
        """)
    List<Asistencia> buscarParaReporte(
            @Param("fechaInicio")
            LocalDate fechaInicio,

            @Param("fechaFin")
            LocalDate fechaFin,

            @Param("texto")
            String texto
    );
}