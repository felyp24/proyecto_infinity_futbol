package com.infinityfutbol.service;

import com.infinityfutbol.dto.response.MatriculadoDetalleResponse;
import com.infinityfutbol.dto.response.ReporteMatriculadosResponse;
import com.infinityfutbol.entity.enums.EstadoUsuario;
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
public class ReporteMatriculadosPdfService {

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

    private final ReporteMatriculadosService
            reporteMatriculadosService;

    public ReporteMatriculadosPdfService(
            ReporteMatriculadosService
                    reporteMatriculadosService
    ) {
        this.reporteMatriculadosService =
                reporteMatriculadosService;
    }

    public byte[] generarPdf(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String texto,
            EstadoUsuario estado
    ) {
        ReporteMatriculadosResponse reporte =
                reporteMatriculadosService
                        .generarReporte(
                                fechaInicio,
                                fechaFin,
                                texto,
                                estado
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
                    "Reporte de alumnos matriculados"
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
                    "No se pudo generar el reporte PDF de matriculados",
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
            ReporteMatriculadosResponse reporte
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
                        "REPORTE DE ALUMNOS MATRICULADOS",
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
                        "Periodo de registro: "
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
                reporte.estadoFiltro() != null
        ) {
            Paragraph filtroEstado =
                    new Paragraph(
                            "Estado: "
                                    + formatearEstado(
                                    reporte
                                            .estadoFiltro()
                                            .name()
                            ),

                            FontFactory.getFont(
                                    FontFactory.HELVETICA,
                                    9
                            )
                    );

            filtroEstado.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(filtroEstado);
        }

        if (
                reporte.textoBusqueda() != null
                        && !reporte
                        .textoBusqueda()
                        .isBlank()
        ) {
            Paragraph filtroBusqueda =
                    new Paragraph(
                            "Búsqueda: "
                                    + reporte.textoBusqueda(),

                            FontFactory.getFont(
                                    FontFactory.HELVETICA,
                                    9
                            )
                    );

            filtroBusqueda.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(filtroBusqueda);
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
            ReporteMatriculadosResponse reporte
    ) throws DocumentException {

        PdfPTable tabla =
                new PdfPTable(
                        new float[]{
                                1f,
                                1f,
                                1f
                        }
                );

        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(18);

        agregarCeldaResumen(
                tabla,
                "Total matriculados",
                String.valueOf(
                        reporte.totalMatriculados()
                )
        );

        agregarCeldaResumen(
                tabla,
                "Activos",
                String.valueOf(
                        reporte.matriculadosActivos()
                )
        );

        agregarCeldaResumen(
                tabla,
                "Inactivos",
                String.valueOf(
                        reporte.matriculadosInactivos()
                )
        );

        documento.add(tabla);
    }

    private void agregarCeldaResumen(
            PdfPTable tabla,
            String etiqueta,
            String valor
    ) {
        PdfPCell celda =
                new PdfPCell();

        celda.setPadding(10);

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        Paragraph titulo =
                new Paragraph(
                        etiqueta,
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                9
                        )
                );

        titulo.setAlignment(
                Element.ALIGN_CENTER
        );

        Paragraph contenido =
                new Paragraph(
                        valor,
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        contenido.setAlignment(
                Element.ALIGN_CENTER
        );

        celda.addElement(titulo);
        celda.addElement(contenido);

        tabla.addCell(celda);
    }

    private void agregarDetalle(
            Document documento,
            ReporteMatriculadosResponse reporte
    ) throws DocumentException {

        Paragraph subtitulo =
                new Paragraph(
                        "Detalle de alumnos matriculados",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                12
                        )
                );

        subtitulo.setSpacingAfter(8);

        documento.add(subtitulo);

        PdfPTable tabla =
                new PdfPTable(
                        new float[]{
                                1.2f,
                                2.1f,
                                1.3f,
                                1.3f,
                                2.1f,
                                1.4f,
                                1f
                        }
                );

        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSplitRows(true);

        agregarCabecera(
                tabla,
                "Fecha de registro"
        );

        agregarCabecera(
                tabla,
                "Alumno"
        );

        agregarCabecera(
                tabla,
                "Documento"
        );

        agregarCabecera(
                tabla,
                "Usuario"
        );

        agregarCabecera(
                tabla,
                "Correo"
        );

        agregarCabecera(
                tabla,
                "Teléfono"
        );

        agregarCabecera(
                tabla,
                "Estado"
        );

        if (
                reporte.matriculados() == null
                        || reporte.matriculados()
                        .isEmpty()
        ) {
            PdfPCell sinRegistros =
                    new PdfPCell(
                            new Phrase(
                                    "No se encontraron alumnos matriculados en el periodo seleccionado.",
                                    FontFactory.getFont(
                                            FontFactory.HELVETICA,
                                            9
                                    )
                            )
                    );

            sinRegistros.setColspan(7);
            sinRegistros.setPadding(14);

            sinRegistros.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );

            tabla.addCell(sinRegistros);

        } else {

            for (
                    MatriculadoDetalleResponse matriculado
                    : reporte.matriculados()
            ) {
                agregarCelda(
                        tabla,
                        matriculado.fechaRegistro() == null
                                ? "-"
                                : matriculado
                                .fechaRegistro()
                                .format(
                                        FORMATO_FECHA_HORA
                                )
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                matriculado.nombreCompleto()
                        )
                );

                agregarCelda(
                        tabla,
                        formatearDocumento(
                                matriculado
                        )
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                matriculado.username()
                        )
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                matriculado.correo()
                        )
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                matriculado.telefono()
                        )
                );

                agregarCeldaCentrada(
                        tabla,
                        matriculado.estadoUsuario() == null
                                ? "-"
                                : formatearEstado(
                                matriculado
                                        .estadoUsuario()
                                        .name()
                        )
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
                                valorSeguro(texto),
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
            MatriculadoDetalleResponse matriculado
    ) {
        String tipo =
                matriculado.tipoDocumento() == null
                        ? ""
                        : switch (
                        matriculado
                                .tipoDocumento()
                                .name()
                        ) {
                    case "DNI" ->
                            "DNI";

                    case "CARNET_EXTRANJERIA" ->
                            "CE";

                    default ->
                            matriculado
                                    .tipoDocumento()
                                    .name();
                };

        return (
                tipo
                        + " "
                        + valorSeguro(
                        matriculado.numeroDocumento()
                )
        ).trim();
    }

    private String formatearEstado(
            String estado
    ) {
        return switch (estado) {
            case "ACTIVO" ->
                    "Activo";

            case "INACTIVO" ->
                    "Inactivo";

            default ->
                    estado;
        };
    }

    private String valorSeguro(
            String valor
    ) {
        if (
                valor == null
                        || valor.isBlank()
        ) {
            return "-";
        }

        return valor;
    }

    private void agregarNota(
            Document documento
    ) throws DocumentException {

        Paragraph nota =
                new Paragraph(
                        "Se considera matriculado a todo alumno registrado en el sistema dentro del periodo seleccionado.",
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