package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.IngresoDetalleResponse;
import com.infinityfutbol.dto.response.ReporteIngresoResponse;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ReporteIngresoPdfService {

    private static final Locale LOCALE_PERU =
            new Locale("es", "PE");

    private static final DateTimeFormatter
            FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm",
                    LOCALE_PERU
            );

    private static final DateTimeFormatter
            FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy",
                    LOCALE_PERU
            );

    private final ReporteIngresoService
            reporteIngresoService;

    public ReporteIngresoPdfService(
            ReporteIngresoService
                    reporteIngresoService
    ) {
        this.reporteIngresoService =
                reporteIngresoService;
    }

    public byte[] generarPdf(
            java.time.LocalDate fechaInicio,
            java.time.LocalDate fechaFin
    ) {
        ReporteIngresoResponse reporte =
                reporteIngresoService
                        .generarReporte(
                                fechaInicio,
                                fechaFin
                        );

        ByteArrayOutputStream salida =
                new ByteArrayOutputStream();

        Document documento =
                new Document(
                        PageSize.A4.rotate(),
                        30,
                        30,
                        35,
                        35
                );

        try {
            PdfWriter.getInstance(
                    documento,
                    salida
            );

            documento.addTitle(
                    "Reporte de ingresos"
            );

            documento.addAuthor(
                    "Infinity Fútbol"
            );

            documento.open();

            agregarEncabezado(
                    documento,
                    reporte
            );

            agregarResumen(
                    documento,
                    reporte
            );

            agregarDetalle(
                    documento,
                    reporte
            );

            agregarPieInformativo(
                    documento
            );

        } catch (DocumentException exception) {

            throw new IllegalStateException(
                    "No se pudo generar el reporte PDF",
                    exception
            );

        } finally {

            if (documento.isOpen()) {
                documento.close();
            }
        }

        return salida.toByteArray();
    }

    private void agregarEncabezado(
            Document documento,
            ReporteIngresoResponse reporte
    ) throws DocumentException {

        Font fuenteTitulo =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18
                );

        Font fuenteSubtitulo =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10
                );

        Paragraph empresa =
                new Paragraph(
                        "INFINITY FÚTBOL",
                        fuenteTitulo
                );

        empresa.setAlignment(
                Element.ALIGN_CENTER
        );

        empresa.setSpacingAfter(6);

        documento.add(empresa);

        Paragraph titulo =
                new Paragraph(
                        "REPORTE DE INGRESOS",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                14
                        )
                );

        titulo.setAlignment(
                Element.ALIGN_CENTER
        );

        titulo.setSpacingAfter(8);

        documento.add(titulo);

        String periodo =
                "Periodo: "
                        + reporte.fechaInicio()
                        .format(
                                FORMATO_FECHA
                        )
                        + " - "
                        + reporte.fechaFin()
                        .format(
                                FORMATO_FECHA
                        );

        Paragraph parrafoPeriodo =
                new Paragraph(
                        periodo,
                        fuenteSubtitulo
                );

        parrafoPeriodo.setAlignment(
                Element.ALIGN_CENTER
        );

        documento.add(
                parrafoPeriodo
        );

        Paragraph generacion =
                new Paragraph(
                        "Generado el: "
                                + LocalDateTime.now()
                                .format(
                                        FORMATO_FECHA_HORA
                                ),
                        fuenteSubtitulo
                );

        generacion.setAlignment(
                Element.ALIGN_CENTER
        );

        generacion.setSpacingAfter(18);

        documento.add(
                generacion
        );
    }

    private void agregarResumen(
            Document documento,
            ReporteIngresoResponse reporte
    ) throws DocumentException {

        PdfPTable tablaResumen =
                new PdfPTable(
                        new float[]{
                                1.4f,
                                1.0f,
                                1.2f,
                                1.2f
                        }
                );

        tablaResumen.setWidthPercentage(100);
        tablaResumen.setSpacingAfter(18);

        agregarCeldaResumen(
                tablaResumen,
                "Total de ingresos",
                formatearMonto(
                        reporte.totalIngresos()
                )
        );

        agregarCeldaResumen(
                tablaResumen,
                "Pagos aprobados",
                String.valueOf(
                        reporte.cantidadPagos()
                )
        );

        agregarCeldaResumen(
                tablaResumen,
                "Créditos vendidos",
                String.valueOf(
                        reporte.totalCreditosVendidos()
                )
        );

        agregarCeldaResumen(
                tablaResumen,
                "Ticket promedio",
                formatearMonto(
                        reporte.ticketPromedio()
                )
        );

        documento.add(
                tablaResumen
        );
    }

    private void agregarCeldaResumen(
            PdfPTable tabla,
            String etiqueta,
            String valor
    ) {
        Font fuenteEtiqueta =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        9
                );

        Font fuenteValor =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        12
                );

        PdfPCell celda =
                new PdfPCell();

        celda.setPadding(10);
        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        Paragraph parrafoEtiqueta =
                new Paragraph(
                        etiqueta,
                        fuenteEtiqueta
                );

        parrafoEtiqueta.setAlignment(
                Element.ALIGN_CENTER
        );

        Paragraph parrafoValor =
                new Paragraph(
                        valor,
                        fuenteValor
                );

        parrafoValor.setAlignment(
                Element.ALIGN_CENTER
        );

        celda.addElement(
                parrafoEtiqueta
        );

        celda.addElement(
                parrafoValor
        );

        tabla.addCell(
                celda
        );
    }

    private void agregarDetalle(
            Document documento,
            ReporteIngresoResponse reporte
    ) throws DocumentException {

        Paragraph subtitulo =
                new Paragraph(
                        "Detalle de ingresos",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                12
                        )
                );

        subtitulo.setSpacingAfter(8);

        documento.add(
                subtitulo
        );

        PdfPTable tabla =
                new PdfPTable(
                        new float[]{
                                1.2f,
                                1.4f,
                                2.2f,
                                1.4f,
                                2.0f,
                                0.8f,
                                1.2f
                        }
                );

        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSplitRows(true);

        agregarCabecera(
                tabla,
                "Fecha"
        );

        agregarCabecera(
                tabla,
                "ID del pago"
        );

        agregarCabecera(
                tabla,
                "Cliente"
        );

        agregarCabecera(
                tabla,
                "Documento"
        );

        agregarCabecera(
                tabla,
                "Paquete"
        );

        agregarCabecera(
                tabla,
                "Créditos"
        );

        agregarCabecera(
                tabla,
                "Monto"
        );

        if (
                reporte.ingresos() == null
                        || reporte.ingresos()
                        .isEmpty()
        ) {
            PdfPCell celdaVacia =
                    new PdfPCell(
                            new Phrase(
                                    "No se registraron ingresos en el periodo seleccionado.",
                                    FontFactory.getFont(
                                            FontFactory.HELVETICA,
                                            9
                                    )
                            )
                    );

            celdaVacia.setColspan(7);
            celdaVacia.setPadding(14);
            celdaVacia.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );

            tabla.addCell(
                    celdaVacia
            );

        } else {

            for (
                    IngresoDetalleResponse ingreso
                    : reporte.ingresos()
            ) {
                agregarCelda(
                        tabla,
                        ingreso.fechaAprobacion()
                                == null
                                ? "-"
                                : ingreso
                                .fechaAprobacion()
                                .format(
                                        FORMATO_FECHA_HORA
                                )
                );

                agregarCelda(
                        tabla,
                        ingreso.idPago()
                );

                agregarCelda(
                        tabla,
                        ingreso.nombreCliente()
                );

                agregarCelda(
                        tabla,
                        formatearDocumento(
                                ingreso
                        )
                );

                agregarCelda(
                        tabla,
                        ingreso.nombrePaquete()
                );

                agregarCeldaCentrada(
                        tabla,
                        String.valueOf(
                                ingreso.cantidadCreditos()
                                        == null
                                        ? 0
                                        : ingreso
                                        .cantidadCreditos()
                        )
                );

                agregarCeldaDerecha(
                        tabla,
                        formatearMonto(
                                ingreso.montoTotal()
                        )
                );
            }
        }

        documento.add(
                tabla
        );
    }

    private void agregarCabecera(
            PdfPTable tabla,
            String texto
    ) {
        Font fuente =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        9
                );

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                fuente
                        )
                );

        celda.setPadding(7);

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        tabla.addCell(
                celda
        );
    }

    private void agregarCelda(
            PdfPTable tabla,
            String texto
    ) {
        PdfPCell celda =
                crearCelda(
                        texto
                );

        celda.setHorizontalAlignment(
                Element.ALIGN_LEFT
        );

        tabla.addCell(
                celda
        );
    }

    private void agregarCeldaCentrada(
            PdfPTable tabla,
            String texto
    ) {
        PdfPCell celda =
                crearCelda(
                        texto
                );

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        tabla.addCell(
                celda
        );
    }

    private void agregarCeldaDerecha(
            PdfPTable tabla,
            String texto
    ) {
        PdfPCell celda =
                crearCelda(
                        texto
                );

        celda.setHorizontalAlignment(
                Element.ALIGN_RIGHT
        );

        tabla.addCell(
                celda
        );
    }

    private PdfPCell crearCelda(
            String texto
    ) {
        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto == null
                                        ? "-"
                                        : texto,
                                FontFactory.getFont(
                                        FontFactory.HELVETICA,
                                        8
                                )
                        )
                );

        celda.setPadding(6);

        return celda;
    }

    private void agregarPieInformativo(
            Document documento
    ) throws DocumentException {

        Paragraph pie =
                new Paragraph(
                        "El reporte considera únicamente pagos con estado APROBADO.",
                        FontFactory.getFont(
                                FontFactory.HELVETICA,
                                8,
                                Font.ITALIC
                        )
                );

        pie.setSpacingBefore(12);

        documento.add(
                pie
        );
    }

    private String formatearDocumento(
            IngresoDetalleResponse ingreso
    ) {
        String tipo =
                switch (
                        ingreso.tipoDocumento() == null
                                ? ""
                                : ingreso.tipoDocumento()
                        ) {
                    case "CARNET_EXTRANJERIA" ->
                            "CE";

                    case "DNI" ->
                            "DNI";

                    default ->
                            ingreso.tipoDocumento() == null
                                    ? ""
                                    : ingreso.tipoDocumento();
                };

        return (
                tipo
                        + " "
                        + (
                        ingreso.numeroDocumento()
                                == null
                                ? ""
                                : ingreso
                                .numeroDocumento()
                )
        ).trim();
    }

    private String formatearMonto(
            BigDecimal monto
    ) {
        BigDecimal valor =
                monto == null
                        ? BigDecimal.ZERO
                        : monto;

        return String.format(
                Locale.US,
                "S/ %.2f",
                valor
        );
    }
}