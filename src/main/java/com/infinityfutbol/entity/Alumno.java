package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoAlumno;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import com.infinityfutbol.entity.enums.TipoDocumento;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alumno")
public class Alumno {

    @Id
    @Column(name = "id_alumno", length = 20)
    private String idAlumno;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_usuario",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_alumno_usuario")
    )
    private Usuario usuario;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_documento",
            nullable = false,
            length = 30
    )
    private TipoDocumento tipoDocumento;

    @Column(
            name = "numero_documento",
            nullable = false,
            unique = true,
            length = 20
    )
    private String numeroDocumento;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(
            name = "fecha_registro",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAlumno estado;

    public Alumno() {
    }

    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(
            TipoDocumento tipoDocumento
    ) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public EstadoAlumno getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlumno estado) {
        this.estado = estado;
    }
}