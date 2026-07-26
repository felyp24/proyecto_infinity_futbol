package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "pago",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pago_preferencia_externa",
                        columnNames = {
                                "id_preferencia_externa"
                        }
                ),
                @UniqueConstraint(
                        name = "uk_pago_externo",
                        columnNames = {
                                "id_pago_externo"
                        }
                )
        }
)
public class Pago {

    @Id
    @Column(
            name = "id_pago",
            length = 20
    )
    private String idPago;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_alumno",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_pago_alumno"
            )
    )
    private Alumno alumno;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_paquete_credito",
            nullable = false,
            foreignKey = @ForeignKey(
                    name =
                            "fk_pago_paquete_credito"
            )
    )
    private PaqueteCredito paqueteCredito;

    /*
     * Por ahora se guarda solamente el ID.
     * La entidad CuponDescuento se creará cuando
     * desarrollemos el módulo de cupones.
     */
    @Column(
            name = "id_cupon",
            length = 20
    )
    private String idCupon;

    @Column(
            name = "fecha_pago",
            nullable = false
    )
    private LocalDateTime fechaPago;

    @Column(
            name = "fecha_aprobacion"
    )
    private LocalDateTime fechaAprobacion;

    @Column(
            name = "fecha_actualizacion",
            nullable = false
    )
    private LocalDateTime fechaActualizacion;

    @Column(
            name = "monto_bruto",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal montoBruto;

    @Column(
            name = "monto_descuento",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal montoDescuento;

    @Column(
            name = "monto_total",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal montoTotal;

    @Column(
            name = "moneda",
            nullable = false,
            length = 3
    )
    private String moneda;

    @Column(
            name = "metodo_pago",
            nullable = false,
            length = 50
    )
    private String metodoPago;

    @Column(
            name = "proveedor_pago",
            nullable = false,
            length = 30
    )
    private String proveedorPago;

    @Column(
            name = "id_preferencia_externa",
            unique = true,
            length = 100
    )
    private String idPreferenciaExterna;

    @Column(
            name = "id_pago_externo",
            unique = true,
            length = 100
    )
    private String idPagoExterno;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado_pago",
            nullable = false,
            length = 30
    )
    private EstadoPago estadoPago;

    @Column(
            name = "estado_detalle",
            length = 100
    )
    private String estadoDetalle;

    @PrePersist
    public void antesDeInsertar() {
        LocalDateTime ahora =
                LocalDateTime.now();

        if (fechaPago == null) {
            fechaPago = ahora;
        }

        if (fechaActualizacion == null) {
            fechaActualizacion = ahora;
        }

        if (montoDescuento == null) {
            montoDescuento =
                    BigDecimal.ZERO;
        }

        if (moneda == null) {
            moneda = "PEN";
        }

        if (metodoPago == null) {
            metodoPago = "MERCADO_PAGO";
        }

        if (proveedorPago == null) {
            proveedorPago =
                    "MERCADO_PAGO";
        }

        if (estadoPago == null) {
            estadoPago =
                    EstadoPago.PENDIENTE;
        }
    }

    @PreUpdate
    public void antesDeActualizar() {
        fechaActualizacion =
                LocalDateTime.now();
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(
            String idPago
    ) {
        this.idPago = idPago;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(
            Alumno alumno
    ) {
        this.alumno = alumno;
    }

    public PaqueteCredito getPaqueteCredito() {
        return paqueteCredito;
    }

    public void setPaqueteCredito(
            PaqueteCredito paqueteCredito
    ) {
        this.paqueteCredito =
                paqueteCredito;
    }

    public String getIdCupon() {
        return idCupon;
    }

    public void setIdCupon(
            String idCupon
    ) {
        this.idCupon = idCupon;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(
            LocalDateTime fechaPago
    ) {
        this.fechaPago = fechaPago;
    }

    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(
            LocalDateTime fechaAprobacion
    ) {
        this.fechaAprobacion =
                fechaAprobacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(
            LocalDateTime fechaActualizacion
    ) {
        this.fechaActualizacion =
                fechaActualizacion;
    }

    public BigDecimal getMontoBruto() {
        return montoBruto;
    }

    public void setMontoBruto(
            BigDecimal montoBruto
    ) {
        this.montoBruto = montoBruto;
    }

    public BigDecimal getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(
            BigDecimal montoDescuento
    ) {
        this.montoDescuento =
                montoDescuento;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(
            BigDecimal montoTotal
    ) {
        this.montoTotal = montoTotal;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(
            String moneda
    ) {
        this.moneda = moneda;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(
            String metodoPago
    ) {
        this.metodoPago = metodoPago;
    }

    public String getProveedorPago() {
        return proveedorPago;
    }

    public void setProveedorPago(
            String proveedorPago
    ) {
        this.proveedorPago =
                proveedorPago;
    }

    public String getIdPreferenciaExterna() {
        return idPreferenciaExterna;
    }

    public void setIdPreferenciaExterna(
            String idPreferenciaExterna
    ) {
        this.idPreferenciaExterna =
                idPreferenciaExterna;
    }

    public String getIdPagoExterno() {
        return idPagoExterno;
    }

    public void setIdPagoExterno(
            String idPagoExterno
    ) {
        this.idPagoExterno =
                idPagoExterno;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(
            EstadoPago estadoPago
    ) {
        this.estadoPago = estadoPago;
    }

    public String getEstadoDetalle() {
        return estadoDetalle;
    }

    public void setEstadoDetalle(
            String estadoDetalle
    ) {
        this.estadoDetalle =
                estadoDetalle;
    }
}