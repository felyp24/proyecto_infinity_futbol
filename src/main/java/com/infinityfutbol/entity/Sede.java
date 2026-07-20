package com.infinityfutbol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sede")
public class Sede {

    @Id
    @Column(
            name = "id_sede",
            length = 20
    )
    private String idSede;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100
    )
    private String nombre;

    @Column(
            name = "direccion",
            nullable = false,
            length = 150
    )
    private String direccion;

    @Column(
            name = "referencia",
            length = 150
    )
    private String referencia;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_distrito",
            nullable = false
    )
    private Distrito distrito;

    @Column(
            name = "estado",
            nullable = false
    )
    private Boolean estado;

    public String getIdSede() {
        return idSede;
    }

    public void setIdSede(
            String idSede
    ) {
        this.idSede = idSede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(
            String nombre
    ) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(
            String direccion
    ) {
        this.direccion = direccion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(
            String referencia
    ) {
        this.referencia = referencia;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(
            Distrito distrito
    ) {
        this.distrito = distrito;
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