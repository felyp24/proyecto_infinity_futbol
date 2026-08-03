package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoAsistencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "asistencia")
public class Asistencia {

    @Id
    @Column(
            name = "id_asistencia",
            length = 20
    )
    private String idAsistencia;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_reserva",
            nullable = false,
            unique = true
    )
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado_asistencia",
            nullable = false,
            length = 30
    )
    private EstadoAsistencia estadoAsistencia;

    @Column(name = "hora_marcacion")
    private LocalDateTime horaMarcacion;

    @Column(
            name = "observacion",
            length = 255
    )
    private String observacion;

    public Asistencia() {
    }

    public String getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(
            String idAsistencia
    ) {
        this.idAsistencia = idAsistencia;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(
            Reserva reserva
    ) {
        this.reserva = reserva;
    }

    public EstadoAsistencia getEstadoAsistencia() {
        return estadoAsistencia;
    }

    public void setEstadoAsistencia(
            EstadoAsistencia estadoAsistencia
    ) {
        this.estadoAsistencia =
                estadoAsistencia;
    }

    public LocalDateTime getHoraMarcacion() {
        return horaMarcacion;
    }

    public void setHoraMarcacion(
            LocalDateTime horaMarcacion
    ) {
        this.horaMarcacion =
                horaMarcacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(
            String observacion
    ) {
        this.observacion = observacion;
    }
}