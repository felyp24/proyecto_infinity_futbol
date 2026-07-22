package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoNotificacion;
import com.infinityfutbol.entity.enums.TipoNotificacion;
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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notificacion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notificacion_reserva_tipo",
                        columnNames = {
                                "id_reserva",
                                "tipo"
                        }
                )
        }
)
public class Notificacion {

    @Id
    @Column(
            name = "id_notificacion",
            length = 20
    )
    private String idNotificacion;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_alumno",
            nullable = false
    )
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    @Column(
            name = "titulo",
            nullable = false,
            length = 100
    )
    private String titulo;

    @Column(
            name = "mensaje",
            nullable = false,
            length = 255
    )
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo",
            nullable = false,
            length = 50
    )
    private TipoNotificacion tipo;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoNotificacion estado;

    @PrePersist
    public void antesDeInsertar() {
        if (estado == null) {
            estado = EstadoNotificacion.PENDIENTE;
        }
    }

    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(
            String idNotificacion
    ) {
        this.idNotificacion = idNotificacion;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(
            Alumno alumno
    ) {
        this.alumno = alumno;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(
            Reserva reserva
    ) {
        this.reserva = reserva;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(
            String titulo
    ) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(
            String mensaje
    ) {
        this.mensaje = mensaje;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(
            TipoNotificacion tipo
    ) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(
            LocalDateTime fechaProgramada
    ) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(
            LocalDateTime fechaEnvio
    ) {
        this.fechaEnvio = fechaEnvio;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoNotificacion estado
    ) {
        this.estado = estado;
    }
}