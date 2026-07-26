package com.infinityfutbol.entity;

import com.infinityfutbol.entity.enums.EstadoComprobante;
import com.infinityfutbol.entity.enums.TipoComprobante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "comprobante",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_comprobante_serie_numero",
                        columnNames = {
                                "serie",
                                "numero"
                        }
                )
        }
)
public class Comprobante {

    @Id
    @Column(
            name = "id_comprobante",
            length = 20
    )
    private String idComprobante;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_pago",
            nullable = false,
            unique = true
    )
    private Pago pago;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_comprobante",
            nullable = false,
            length = 30
    )
    private TipoComprobante tipoComprobante;

    @Column(
            name = "serie",
            nullable = false,
            length = 10
    )
    private String serie;

    @Column(
            name = "numero",
            nullable = false,
            length = 20
    )
    private String numero;

    @Column(
            name = "fecha_emision",
            nullable = false
    )
    private LocalDateTime fechaEmision;

    @Column(
            name = "monto_total",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoComprobante estado;

    @PrePersist
    public void antesDeInsertar() {
        if (fechaEmision == null) {
            fechaEmision =
                    LocalDateTime.now();
        }

        if (tipoComprobante == null) {
            tipoComprobante =
                    TipoComprobante.BOLETA;
        }

        if (estado == null) {
            estado =
                    EstadoComprobante.EMITIDO;
        }
    }

    public String getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(
            String idComprobante
    ) {
        this.idComprobante =
                idComprobante;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(
            Pago pago
    ) {
        this.pago = pago;
    }

    public TipoComprobante getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(
            TipoComprobante tipoComprobante
    ) {
        this.tipoComprobante =
                tipoComprobante;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(
            String serie
    ) {
        this.serie = serie;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(
            String numero
    ) {
        this.numero = numero;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(
            LocalDateTime fechaEmision
    ) {
        this.fechaEmision =
                fechaEmision;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(
            BigDecimal montoTotal
    ) {
        this.montoTotal =
                montoTotal;
    }

    public EstadoComprobante getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoComprobante estado
    ) {
        this.estado = estado;
    }
}