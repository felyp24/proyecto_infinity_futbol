package com.infinityfutbol.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "usuario_rol",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_rol",
                        columnNames = {"id_usuario", "id_rol"}
                )
        }
)
public class UsuarioRol {

    @Id
    @Column(name = "id_usuario_rol", length = 25)
    private String idUsuarioRol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_usuario",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_rol_usuario")
    )
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_rol",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_rol_rol")
    )
    private Rol rol;

    public UsuarioRol() {
    }

    public UsuarioRol(String idUsuarioRol, Usuario usuario, Rol rol) {
        this.idUsuarioRol = idUsuarioRol;
        this.usuario = usuario;
        this.rol = rol;
    }

    public String getIdUsuarioRol() {
        return idUsuarioRol;
    }

    public void setIdUsuarioRol(String idUsuarioRol) {
        this.idUsuarioRol = idUsuarioRol;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}