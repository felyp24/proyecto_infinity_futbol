package com.infinityfutbol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "paquete_credito")
public class PaqueteCredito {

    @Id
    @Column(
            name = "id_paquete_credito",
            length = 20
    )
    private String idPaqueteCredito;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100
    )
    private String nombre;

    @Column(
            name = "cantidad_creditos",
            nullable = false
    )
    private Integer cantidadCreditos;

    @Column(
            name = "precio",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal precio;

    @Column(
            name = "dias_vigencia",
            nullable = false
    )
    private Integer diasVigencia;

    @Column(
            name = "estado",
            nullable = false
    )
    private Boolean estado;

    public String getIdPaqueteCredito() {
        return idPaqueteCredito;
    }

    public void setIdPaqueteCredito(
            String idPaqueteCredito
    ) {
        this.idPaqueteCredito =
                idPaqueteCredito;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(
            String nombre
    ) {
        this.nombre = nombre;
    }

    public Integer getCantidadCreditos() {
        return cantidadCreditos;
    }

    public void setCantidadCreditos(
            Integer cantidadCreditos
    ) {
        this.cantidadCreditos =
                cantidadCreditos;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(
            BigDecimal precio
    ) {
        this.precio = precio;
    }

    public Integer getDiasVigencia() {
        return diasVigencia;
    }

    public void setDiasVigencia(
            Integer diasVigencia
    ) {
        this.diasVigencia =
                diasVigencia;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(
            Boolean estado
    ) {
        this.estado = estado;
    }
}