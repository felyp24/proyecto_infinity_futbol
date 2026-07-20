package com.infinityfutbol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "cuenta_credito")
public class CuentaCredito {

    @Id
    @Column(
            name = "id_cuenta_credito",
            length = 20
    )
    private String idCuentaCredito;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_alumno",
            nullable = false,
            unique = true
    )
    private Alumno alumno;

    @Column(
            name = "saldo_actual",
            nullable = false
    )
    private Integer saldoActual;

    @Column(
            name = "fecha_actualizacion",
            nullable = false
    )
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void antesDeInsertar() {
        if (saldoActual == null) {
            saldoActual = 0;
        }

        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void antesDeActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public String getIdCuentaCredito() {
        return idCuentaCredito;
    }

    public void setIdCuentaCredito(
            String idCuentaCredito
    ) {
        this.idCuentaCredito = idCuentaCredito;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(
            Alumno alumno
    ) {
        this.alumno = alumno;
    }

    public Integer getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(
            Integer saldoActual
    ) {
        this.saldoActual = saldoActual;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(
            LocalDateTime fechaActualizacion
    ) {
        this.fechaActualizacion = fechaActualizacion;
    }
}