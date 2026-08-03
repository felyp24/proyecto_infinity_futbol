package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.AsistenciaDetalleResponse;
import com.infinityfutbol.dto.response.ReporteAsistenciaResponse;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReporteAsistenciaPdfService {

    private static final DateTimeFormatter
            FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
            );

    private static final DateTimeFormatter
            FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    private final ReporteAsistenciaService
            reporteAsistenciaService;

    public ReporteAsistenciaPdfService(
            ReporteAsistenciaService
                    reporteAsistenciaService
    ) {
        this.reporteAsistenciaService =
                reporteAsistenciaService;
    }

    public byte[] generarPdf(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String texto
    ) {
        ReporteAsistenciaResponse reporte =
                reporteAsistenciaService
                        .generarReporte(
                                fechaInicio,
                                fechaFin,
                                texto
                        );

        ByteArrayOutputStream salida =
                new ByteArrayOutputStream();

        Document documento =
                new Document(
                        PageSize.A4.rotate(),
                        25,
                        25,
                        30,
                        30
                );

        try {
            PdfWriter.getInstance(
                    documento,
                    salida
            );

            documento.addTitle(
                    "Reporte de asistencia"
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

            agregarNota(
                    documento
            );

        } catch (DocumentException exception) {

            throw new IllegalStateException(
                    "No se pudo generar el PDF de asistencia",
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
            ReporteAsistenciaResponse reporte
    ) throws DocumentException {

        Paragraph empresa =
                new Paragraph(
                        "INFINITY FÚTBOL",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                18
                        )
                );

        empresa.setAlignment(
                Element.ALIGN_CENTER
        );

        empresa.setSpacingAfter(5);

        documento.add(empresa);

        Paragraph titulo =
                new Paragraph(
                        "REPORTE DE ASISTENCIA",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                14
                        )
                );

        titulo.setAlignment(
                Element.ALIGN_CENTER
        );

        titulo.setSpacingAfter(7);

        documento.add(titulo);

        Paragraph periodo =
                new Paragraph(
                        "Periodo: "
                                + reporte.fechaInicio()
                                .format(FORMATO_FECHA)
                                + " - "
                                + reporte.fechaFin()
                                .format(FORMATO_FECHA),

                        FontFactory.getFont(
                                FontFactory.HELVETICA,
                                10
                        )
                );

        periodo.setAlignment(
                Element.ALIGN_CENTER
        );

        documento.add(periodo);

        if (
                reporte.textoBusqueda() != null
                        && !reporte
                        .textoBusqueda()
                        .isBlank()
        ) {
            Paragraph filtro =
                    new Paragraph(
                            "Filtro aplicado: "
                                    + reporte.textoBusqueda(),

                            FontFactory.getFont(
                                    FontFactory.HELVETICA,
                                    9
                            )
                    );

            filtro.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(filtro);
        }

        Paragraph fechaGeneracion =
                new Paragraph(
                        "Generado el: "
                                + LocalDateTime.now()
                                .format(
                                        FORMATO_FECHA_HORA
                                ),

                        FontFactory.getFont(
                                FontFactory.HELVETICA,
                                9
                        )
                );

        fechaGeneracion.setAlignment(
                Element.ALIGN_CENTER
        );

        fechaGeneracion.setSpacingAfter(16);

        documento.add(fechaGeneracion);
    }

    private void agregarResumen(
            Document documento,
            ReporteAsistenciaResponse reporte
    ) throws DocumentException {

        PdfPTable tabla =
                new PdfPTable(
                        new float[]{
                                1f,
                                1f,
                                1f,
                                1f,
                                1f,
                                1f,
                                1.2f
                        }
                );

        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(18);

        agregarResumenCelda(
                tabla,
                "Registros",
                String.valueOf(
                        reporte.totalRegistros()
                )
        );

        agregarResumenCelda(
                tabla,
                "Alumnos",
                String.valueOf(
                        reporte.totalAlumnos()
                )
        );

        agregarResumenCelda(
                tabla,
                "Presentes",
                String.valueOf(
                        reporte.presentes()
                )
        );

        agregarResumenCelda(
                tabla,
                "Tardanzas",
                String.valueOf(
                        reporte.tardanzas()
                )
        );

        agregarResumenCelda(
                tabla,
                "Ausentes",
                String.valueOf(
                        reporte.ausentes()
                )
        );

        agregarResumenCelda(
                tabla,
                "Justificadas",
                String.valueOf(
                        reporte.justificadas()
                )
        );

        agregarResumenCelda(
                tabla,
                "% asistencia",
                reporte.porcentajeAsistencia()
                        .toPlainString()
                        + "%"
        );

        documento.add(tabla);
    }

    private void agregarResumenCelda(
            PdfPTable tabla,
            String titulo,
            String valor
    ) {
        PdfPCell celda =
                new PdfPCell();

        celda.setPadding(8);

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        Paragraph etiqueta =
                new Paragraph(
                        titulo,
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                8
                        )
                );

        etiqueta.setAlignment(
                Element.ALIGN_CENTER
        );

        Paragraph contenido =
                new Paragraph(
                        valor,
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                11
                        )
                );

        contenido.setAlignment(
                Element.ALIGN_CENTER
        );

        celda.addElement(etiqueta);
        celda.addElement(contenido);

        tabla.addCell(celda);
    }

    private void agregarDetalle(
            Document documento,
            ReporteAsistenciaResponse reporte
    ) throws DocumentException {

        Paragraph titulo =
                new Paragraph(
                        "Detalle de asistencias",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                12
                        )
                );

        titulo.setSpacingAfter(8);

        documento.add(titulo);

        PdfPTable tabla =
                new PdfPTable(
                        new float[]{
                                1f,
                                2f,
                                1.2f,
                                1.8f,
                                1.7f,
                                1.1f,
                                1.2f,
                                2f
                        }
                );

        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSplitRows(true);

        agregarCabecera(tabla, "Fecha");
        agregarCabecera(tabla, "Alumno");
        agregarCabecera(tabla, "Documento");
        agregarCabecera(tabla, "Clase");
        agregarCabecera(tabla, "Sede / cancha");
        agregarCabecera(tabla, "Estado");
        agregarCabecera(tabla, "Marcación");
        agregarCabecera(tabla, "Observación");

        if (
                reporte.asistencias() == null
                        || reporte.asistencias().isEmpty()
        ) {
            PdfPCell sinRegistros =
                    new PdfPCell(
                            new Phrase(
                                    "No se encontraron registros de asistencia.",
                                    FontFactory.getFont(
                                            FontFactory.HELVETICA,
                                            9
                                    )
                            )
                    );

            sinRegistros.setColspan(8);
            sinRegistros.setPadding(14);

            sinRegistros.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );

            tabla.addCell(sinRegistros);

        } else {

            for (
                    AsistenciaDetalleResponse asistencia
                    : reporte.asistencias()
            ) {
                agregarCelda(
                        tabla,
                        asistencia.fechaClase()
                                .format(FORMATO_FECHA)
                );

                agregarCelda(
                        tabla,
                        asistencia.nombreAlumno()
                );

                agregarCelda(
                        tabla,
                        formatearDocumento(
                                asistencia
                        )
                );

                agregarCelda(
                        tabla,
                        asistencia.tituloClase()
                );

                agregarCelda(
                        tabla,
                        asistencia.nombreSede()
                                + " / Cancha "
                                + asistencia.numeroCancha()
                );

                agregarCeldaCentrada(
                        tabla,
                        formatearEstado(
                                asistencia
                                        .estadoAsistencia()
                                        .name()
                        )
                );

                agregarCeldaCentrada(
                        tabla,
                        asistencia.horaMarcacion()
                                == null
                                ? "-"
                                : asistencia
                                .horaMarcacion()
                                .format(
                                        FORMATO_FECHA_HORA
                                )
                );

                agregarCelda(
                        tabla,
                        asistencia.observacion()
                                == null
                                || asistencia
                                .observacion()
                                .isBlank()
                                ? "-"
                                : asistencia.observacion()
                );
            }
        }

        documento.add(tabla);
    }

    private void agregarCabecera(
            PdfPTable tabla,
            String texto
    ) {
        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                FontFactory.getFont(
                                        FontFactory.HELVETICA_BOLD,
                                        8
                                )
                        )
                );

        celda.setPadding(7);

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        tabla.addCell(celda);
    }

    private void agregarCelda(
            PdfPTable tabla,
            String texto
    ) {
        PdfPCell celda =
                crearCelda(texto);

        celda.setHorizontalAlignment(
                Element.ALIGN_LEFT
        );

        tabla.addCell(celda);
    }

    private void agregarCeldaCentrada(
            PdfPTable tabla,
            String texto
    ) {
        PdfPCell celda =
                crearCelda(texto);

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        tabla.addCell(celda);
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
                                        7.5f
                                )
                        )
                );

        celda.setPadding(5);

        return celda;
    }

    private String formatearDocumento(
            AsistenciaDetalleResponse asistencia
    ) {
        String tipo =
                switch (
                        asistencia.tipoDocumento() == null
                                ? ""
                                : asistencia.tipoDocumento()
                        ) {
                    case "DNI" ->
                            "DNI";

                    case "CARNET_EXTRANJERIA" ->
                            "CE";

                    default ->
                            asistencia.tipoDocumento() == null
                                    ? ""
                                    : asistencia.tipoDocumento();
                };

        return (
                tipo
                        + " "
                        + asistencia.numeroDocumento()
        ).trim();
    }

    private String formatearEstado(
            String estado
    ) {
        return switch (estado) {
            case "PRESENTE" ->
                    "Presente";

            case "AUSENTE" ->
                    "Ausente";

            case "TARDANZA" ->
                    "Tardanza";

            case "JUSTIFICADA" ->
                    "Justificada";

            default ->
                    estado;
        };
    }

    private void agregarNota(
            Document documento
    ) throws DocumentException {

        Paragraph nota =
                new Paragraph(
                        "El porcentaje considera PRESENTE y TARDANZA como asistencias efectivas.",
                        FontFactory.getFont(
                                FontFactory.HELVETICA,
                                8,
                                Font.ITALIC
                        )
                );

        nota.setSpacingBefore(10);

        documento.add(nota);
    }
}