package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.ReporteAsistenciaResponse;
import com.infinityfutbol.service.ReporteAsistenciaPdfService;
import com.infinityfutbol.service.ReporteAsistenciaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(
        "/api/admin/reportes/asistencias"
)
@PreAuthorize(
        "hasRole('ADMINISTRADOR')"
)
public class ReporteAsistenciaAdminController {

    private final ReporteAsistenciaService
            reporteAsistenciaService;

    private final ReporteAsistenciaPdfService
            reporteAsistenciaPdfService;

    public ReporteAsistenciaAdminController(
            ReporteAsistenciaService
                    reporteAsistenciaService,

            ReporteAsistenciaPdfService
                    reporteAsistenciaPdfService
    ) {
        this.reporteAsistenciaService =
                reporteAsistenciaService;

        this.reporteAsistenciaPdfService =
                reporteAsistenciaPdfService;
    }

    @GetMapping
    public ReporteAsistenciaResponse
    generarReporte(
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaInicio,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaFin,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String texto
    ) {
        return reporteAsistenciaService
                .generarReporte(
                        fechaInicio,
                        fechaFin,
                        texto
                );
    }

    @GetMapping(
            value = "/pdf",
            produces =
                    MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaInicio,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaFin,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String texto
    ) {
        byte[] pdf =
                reporteAsistenciaPdfService
                        .generarPdf(
                                fechaInicio,
                                fechaFin,
                                texto
                        );

        String nombre =
                "reporte-asistencias-"
                        + fechaInicio
                        + "-a-"
                        + fechaFin
                        + ".pdf";

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + nombre
                                + "\""
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .contentLength(pdf.length)
                .body(pdf);
    }
}