package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Utileria;
import com.infinityfutbol.entity.enums.EstadoUtileria;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtileriaRepository
        extends JpaRepository<Utileria, String> {

    boolean existsBySede_IdSedeAndNombreIgnoreCase(
            String idSede,
            String nombre
    );

    boolean existsBySede_IdSedeAndNombreIgnoreCaseAndIdUtileriaNot(
            String idSede,
            String nombre,
            String idUtileria
    );

    @Override
    @EntityGraph(attributePaths = {
            "sede",
            "sede.distrito",
            "usuarioRegistro",
            "usuarioActualizacion"
    })
    Optional<Utileria> findById(
            String idUtileria
    );

    @EntityGraph(attributePaths = {
            "sede",
            "sede.distrito",
            "usuarioRegistro",
            "usuarioActualizacion"
    })
    @Query("""
        SELECT u
        FROM Utileria u

        WHERE (
            :idSede = ''
            OR u.sede.idSede = :idSede
        )

        AND (
            :estadoFiltro IS NULL
            OR u.estado = :estadoFiltro
        )

        AND (
            :texto = ''

            OR LOWER(u.nombre)
                LIKE LOWER(
                    CONCAT('%', :texto, '%')
                )

            OR LOWER(u.categoria)
                LIKE LOWER(
                    CONCAT('%', :texto, '%')
                )

            OR LOWER(u.sede.nombre)
                LIKE LOWER(
                    CONCAT('%', :texto, '%')
                )

            OR LOWER(
                COALESCE(u.observacion, '')
            )
                LIKE LOWER(
                    CONCAT('%', :texto, '%')
                )
        )

        ORDER BY
            u.sede.nombre ASC,
            u.nombre ASC
        """)
    List<Utileria> buscarUtileria(
            @Param("idSede")
            String idSede,

            @Param("texto")
            String texto,

            @Param("estadoFiltro")
            EstadoUtileria estadoFiltro
    );
}