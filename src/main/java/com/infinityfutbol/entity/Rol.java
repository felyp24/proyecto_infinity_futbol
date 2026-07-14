package com.infinityfutbol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @Column(name = "id_rol", length = 20)
    private String idRol;

    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    private String nombreRol;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    @Column(name = "estado", nullable = false)
    private Boolean estado;

    public Rol() {
    }

    public Rol(
            String idRol,
            String nombreRol,
            String descripcion,
            Boolean estado
    ) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getIdRol() {
        return idRol;
    }

    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}