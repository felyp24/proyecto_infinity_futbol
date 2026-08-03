package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoCupon;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cupon_descuento")
public class CuponDescuento {

    @Id
    @Column(
            name = "id_cupon",
            length = 20
    )
    private String idCupon;

    @Column(
            name = "codigo",
            nullable = false,
            unique = true,
            length = 50
    )
    private String codigo;

    @Column(
            name = "porcentaje_descuento",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal porcentajeDescuento;

    @Column(
            name = "fecha_inicio",
            nullable = false
    )
    private LocalDate fechaInicio;

    @Column(
            name = "fecha_expiracion",
            nullable = false
    )
    private LocalDate fechaExpiracion;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoCupon estado;

    @PrePersist
    public void antesDeInsertar() {
        if (estado == null) {
            estado = EstadoCupon.ACTIVO;
        }
    }

    public String getIdCupon() {
        return idCupon;
    }

    public void setIdCupon(
            String idCupon
    ) {
        this.idCupon = idCupon;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(
            String codigo
    ) {
        this.codigo = codigo;
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(
            BigDecimal porcentajeDescuento
    ) {
        this.porcentajeDescuento =
                porcentajeDescuento;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(
            LocalDate fechaInicio
    ) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(
            LocalDate fechaExpiracion
    ) {
        this.fechaExpiracion =
                fechaExpiracion;
    }

    public EstadoCupon getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoCupon estado
    ) {
        this.estado = estado;
    }
}