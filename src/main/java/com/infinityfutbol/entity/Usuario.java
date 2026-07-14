package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name = "id_usuario", length = 20)
    private String idUsuario;

    @Column(
            name = "username",
            nullable = false,
            unique = true,
            length = 50
    )
    private String username;

    @Column(
            name = "password",
            nullable = false,
            length = 255
    )
    private String password;

    @Column(
            name = "correo",
            nullable = false,
            unique = true,
            length = 100
    )
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoUsuario estado;

    @Column(
            name = "fecha_creacion",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime fechaCreacion;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    public Usuario() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}