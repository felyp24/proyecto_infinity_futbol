package com.infinityfutbol.repository;

import com.infinityfutbol.entity.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, String> {

    @EntityGraph(attributePaths = "usuario")
    Optional<Alumno> findByUsuario_IdUsuario(String idUsuario);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdAlumnoNot(
            String numeroDocumento,
            String idAlumno
    );

    @Override
    @EntityGraph(attributePaths = "usuario")
    Page<Alumno> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "usuario")
    @Query("""
        SELECT a
        FROM Alumno a
        WHERE LOWER(a.nombres)
                  LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(a.apellidos)
                  LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(a.numeroDocumento)
                  LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(a.usuario.correo)
                  LIKE LOWER(CONCAT('%', :texto, '%'))
        """)
    Page<Alumno> buscar(
            @Param("texto") String texto,
            Pageable pageable
    );

    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<Alumno> findById(String idAlumno);
}