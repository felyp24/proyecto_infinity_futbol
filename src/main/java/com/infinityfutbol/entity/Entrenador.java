package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoEntrenador;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entrenador")
public class Entrenador {

    @Id
    @Column(
            name = "id_entrenador",
            length = 20
    )
    private String idEntrenador;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_usuario",
            nullable = false,
            unique = true
    )
    private Usuario usuario;

    @Column(
            name = "nombres",
            nullable = false,
            length = 100
    )
    private String nombres;

    @Column(
            name = "apellidos",
            nullable = false,
            length = 100
    )
    private String apellidos;

    @Column(
            name = "telefono",
            length = 20
    )
    private String telefono;

    @Column(
            name = "especialidad",
            length = 100
    )
    private String especialidad;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoEntrenador estado;

    public String getIdEntrenador() {
        return idEntrenador;
    }

    public void setIdEntrenador(
            String idEntrenador
    ) {
        this.idEntrenador = idEntrenador;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            Usuario usuario
    ) {
        this.usuario = usuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(
            String nombres
    ) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(
            String apellidos
    ) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(
            String telefono
    ) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(
            String especialidad
    ) {
        this.especialidad = especialidad;
    }

    public EstadoEntrenador getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoEntrenador estado
    ) {
        this.estado = estado;
    }
}