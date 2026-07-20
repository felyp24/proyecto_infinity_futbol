package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoReserva;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reserva",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reserva_alumno_clase",
                        columnNames = {
                                "id_alumno",
                                "id_clase"
                        }
                )
        }
)
public class Reserva {

    @Id
    @Column(
            name = "id_reserva",
            length = 20
    )
    private String idReserva;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_alumno",
            nullable = false
    )
    private Alumno alumno;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_clase",
            nullable = false
    )
    private Clase clase;

    @Column(
            name = "fecha_reserva",
            nullable = false
    )
    private LocalDateTime fechaReserva;

    @Column(
            name = "creditos_usados",
            nullable = false
    )
    private Integer creditosUsados;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoReserva estado;

    @PrePersist
    public void antesDeInsertar() {
        if (fechaReserva == null) {
            fechaReserva = LocalDateTime.now();
        }

        if (creditosUsados == null) {
            creditosUsados = 1;
        }

        if (estado == null) {
            estado = EstadoReserva.CONFIRMADA;
        }
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(
            String idReserva
    ) {
        this.idReserva = idReserva;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(
            Alumno alumno
    ) {
        this.alumno = alumno;
    }

    public Clase getClase() {
        return clase;
    }

    public void setClase(
            Clase clase
    ) {
        this.clase = clase;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(
            LocalDateTime fechaReserva
    ) {
        this.fechaReserva = fechaReserva;
    }

    public Integer getCreditosUsados() {
        return creditosUsados;
    }

    public void setCreditosUsados(
            Integer creditosUsados
    ) {
        this.creditosUsados = creditosUsados;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoReserva estado
    ) {
        this.estado = estado;
    }
}