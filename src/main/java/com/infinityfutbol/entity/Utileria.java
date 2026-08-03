package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoUtileria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "utileria")
public class Utileria {

    @Id
    @Column(
            name = "id_utileria",
            length = 20
    )
    private String idUtileria;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_sede",
            nullable = false
    )
    private Sede sede;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100
    )
    private String nombre;

    @Column(
            name = "categoria",
            nullable = false,
            length = 50
    )
    private String categoria;

    @Column(
            name = "unidad_medida",
            nullable = false,
            length = 30
    )
    private String unidadMedida;

    @Column(
            name = "cantidad_actual",
            nullable = false
    )
    private Integer cantidadActual;

    @Column(
            name = "stock_minimo",
            nullable = false
    )
    private Integer stockMinimo;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoUtileria estado;

    @Column(
            name = "observacion",
            length = 255
    )
    private String observacion;

    @Column(
            name = "fecha_registro",
            nullable = false
    )
    private LocalDateTime fechaRegistro;

    @Column(
            name = "fecha_actualizacion",
            nullable = false
    )
    private LocalDateTime fechaActualizacion;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_usuario_registro",
            nullable = false
    )
    private Usuario usuarioRegistro;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_usuario_actualizacion",
            nullable = false
    )
    private Usuario usuarioActualizacion;

    @PrePersist
    public void antesDeInsertar() {
        LocalDateTime ahora =
                LocalDateTime.now();

        if (cantidadActual == null) {
            cantidadActual = 0;
        }

        if (stockMinimo == null) {
            stockMinimo = 0;
        }

        if (estado == null) {
            estado = EstadoUtileria.ACTIVO;
        }

        if (unidadMedida == null) {
            unidadMedida = "UNIDAD";
        }

        if (fechaRegistro == null) {
            fechaRegistro = ahora;
        }

        fechaActualizacion = ahora;
    }

    @PreUpdate
    public void antesDeActualizar() {
        fechaActualizacion =
                LocalDateTime.now();
    }

    public String getIdUtileria() {
        return idUtileria;
    }

    public void setIdUtileria(
            String idUtileria
    ) {
        this.idUtileria = idUtileria;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(
            Sede sede
    ) {
        this.sede = sede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(
            String nombre
    ) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(
            String categoria
    ) {
        this.categoria = categoria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(
            String unidadMedida
    ) {
        this.unidadMedida = unidadMedida;
    }

    public Integer getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(
            Integer cantidadActual
    ) {
        this.cantidadActual = cantidadActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(
            Integer stockMinimo
    ) {
        this.stockMinimo = stockMinimo;
    }

    public EstadoUtileria getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoUtileria estado
    ) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(
            String observacion
    ) {
        this.observacion = observacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(
            LocalDateTime fechaRegistro
    ) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(
            LocalDateTime fechaActualizacion
    ) {
        this.fechaActualizacion =
                fechaActualizacion;
    }

    public Usuario getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(
            Usuario usuarioRegistro
    ) {
        this.usuarioRegistro =
                usuarioRegistro;
    }

    public Usuario getUsuarioActualizacion() {
        return usuarioActualizacion;
    }

    public void setUsuarioActualizacion(
            Usuario usuarioActualizacion
    ) {
        this.usuarioActualizacion =
                usuarioActualizacion;
    }
}