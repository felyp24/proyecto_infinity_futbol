package com.infinityfutbol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "distrito")
public class Distrito {

    @Id
    @Column(
            name = "id_distrito",
            length = 10
    )
    private String idDistrito;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100
    )
    private String nombre;

    @Column(
            name = "estado",
            nullable = false
    )
    private Boolean estado;

    public String getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(
            String idDistrito
    ) {
        this.idDistrito = idDistrito;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(
            String nombre
    ) {
        this.nombre = nombre;
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