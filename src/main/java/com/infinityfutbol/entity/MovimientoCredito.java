package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.TipoMovimientoCredito;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_credito")
public class MovimientoCredito {

    @Id
    @Column(
            name = "id_movimiento_credito",
            length = 25
    )
    private String idMovimientoCredito;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_cuenta_credito",
            nullable = false
    )
    private CuentaCredito cuentaCredito;

    /*
     * Por ahora guardamos únicamente el identificador del pago.
     * La relación con Pago se podrá agregar cuando construyamos
     * el módulo de compra de créditos.
     */
    @Column(
            name = "id_pago",
            length = 20
    )
    private String idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_movimiento",
            nullable = false,
            length = 30
    )
    private TipoMovimientoCredito tipoMovimiento;

    @Column(
            name = "cantidad",
            nullable = false
    )
    private Integer cantidad;

    @Column(
            name = "fecha_movimiento",
            nullable = false
    )
    private LocalDateTime fechaMovimiento;

    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;

    @Column(
            name = "descripcion",
            length = 255
    )
    private String descripcion;

    @PrePersist
    public void antesDeInsertar() {
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now();
        }
    }

    public String getIdMovimientoCredito() {
        return idMovimientoCredito;
    }

    public void setIdMovimientoCredito(
            String idMovimientoCredito
    ) {
        this.idMovimientoCredito = idMovimientoCredito;
    }

    public CuentaCredito getCuentaCredito() {
        return cuentaCredito;
    }

    public void setCuentaCredito(
            CuentaCredito cuentaCredito
    ) {
        this.cuentaCredito = cuentaCredito;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(
            String idPago
    ) {
        this.idPago = idPago;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(
            Reserva reserva
    ) {
        this.reserva = reserva;
    }

    public TipoMovimientoCredito getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(
            TipoMovimientoCredito tipoMovimiento
    ) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(
            Integer cantidad
    ) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(
            LocalDateTime fechaMovimiento
    ) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(
            LocalDate fechaExpiracion
    ) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(
            String descripcion
    ) {
        this.descripcion = descripcion;
    }
}