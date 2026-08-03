package com.infinityfutbol.repository;

import com.infinityfutbol.entity.CuponDescuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CuponDescuentoRepository
        extends JpaRepository<
        CuponDescuento,
        String
        > {

    Optional<CuponDescuento>
    findByCodigoIgnoreCase(
            String codigo
    );

    boolean existsByCodigoIgnoreCase(
            String codigo
    );

    @Query("""
        SELECT c
        FROM CuponDescuento c
        WHERE :texto = ''
           OR LOWER(c.codigo)
              LIKE LOWER(
                    CONCAT('%', :texto, '%')
              )
        ORDER BY
            c.fechaExpiracion DESC,
            c.codigo ASC
        """)
    List<CuponDescuento> buscarCupones(
            @Param("texto")
            String texto
    );
}