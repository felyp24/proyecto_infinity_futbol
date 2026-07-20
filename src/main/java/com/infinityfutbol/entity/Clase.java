package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoClase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "clase")
public class Clase {

    @Id
    @Column(
            name = "id_clase",
            length = 20
    )
    private String idClase;

    @Column(
            name = "titulo",
            nullable = false,
            length = 100
    )
    private String titulo;

    @Column(
            name = "descripcion",
            length = 255
    )
    private String descripcion;

    @Column(
            name = "fecha_clase",
            nullable = false
    )
    private LocalDate fechaClase;

    @Column(
            name = "hora_inicio",
            nullable = false
    )
    private LocalTime horaInicio;

    @Column(
            name = "hora_fin",
            nullable = false
    )
    private LocalTime horaFin;

    @Column(
            name = "cupo_maximo",
            nullable = false
    )
    private Integer cupoMaximo;

    @Column(
            name = "cupo_disponible",
            nullable = false
    )
    private Integer cupoDisponible;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoClase estado;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_cancha",
            nullable = false
    )
    private Cancha cancha;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_entrenador",
            nullable = false
    )
    private Entrenador entrenador;

    @PrePersist
    public void antesDeInsertar() {
        if (estado == null) {
            estado = EstadoClase.PROGRAMADA;
        }

        if (cupoDisponible == null) {
            cupoDisponible = cupoMaximo;
        }
    }

    public String getIdClase() {
        return idClase;
    }

    public void setIdClase(
            String idClase
    ) {
        this.idClase = idClase;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(
            String titulo
    ) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(
            String descripcion
    ) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaClase() {
        return fechaClase;
    }

    public void setFechaClase(
            LocalDate fechaClase
    ) {
        this.fechaClase = fechaClase;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(
            LocalTime horaInicio
    ) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(
            LocalTime horaFin
    ) {
        this.horaFin = horaFin;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(
            Integer cupoMaximo
    ) {
        this.cupoMaximo = cupoMaximo;
    }

    public Integer getCupoDisponible() {
        return cupoDisponible;
    }

    public void setCupoDisponible(
            Integer cupoDisponible
    ) {
        this.cupoDisponible = cupoDisponible;
    }

    public EstadoClase getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoClase estado
    ) {
        this.estado = estado;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public void setCancha(
            Cancha cancha
    ) {
        this.cancha = cancha;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(
            Entrenador entrenador
    ) {
        this.entrenador = entrenador;
    }
}