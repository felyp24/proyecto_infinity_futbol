package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoCancha;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cancha")
public class Cancha {

    @Id
    @Column(
            name = "id_cancha",
            length = 20
    )
    private String idCancha;

    @Column(
            name = "numero_cancha",
            nullable = false
    )
    private Integer numeroCancha;

    @Column(
            name = "tipo_superficie",
            length = 50
    )
    private String tipoSuperficie;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoCancha estado;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_sede",
            nullable = false
    )
    private Sede sede;

    public String getIdCancha() {
        return idCancha;
    }

    public void setIdCancha(
            String idCancha
    ) {
        this.idCancha = idCancha;
    }

    public Integer getNumeroCancha() {
        return numeroCancha;
    }

    public void setNumeroCancha(
            Integer numeroCancha
    ) {
        this.numeroCancha = numeroCancha;
    }

    public String getTipoSuperficie() {
        return tipoSuperficie;
    }

    public void setTipoSuperficie(
            String tipoSuperficie
    ) {
        this.tipoSuperficie = tipoSuperficie;
    }

    public EstadoCancha getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoCancha estado
    ) {
        this.estado = estado;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(
            Sede sede
    ) {
        this.sede = sede;
    }
}