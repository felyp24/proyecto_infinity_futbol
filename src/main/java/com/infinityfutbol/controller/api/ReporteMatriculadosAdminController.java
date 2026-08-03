package com.infinityfutbol.controller.api;

import com.infinityfutbol.dto.response.ReporteMatriculadosResponse;
import com.infinityfutbol.entity.enums.EstadoUsuario;
import com.infinityfutbol.service.ReporteMatriculadosPdfService;
import com.infinityfutbol.service.ReporteMatriculadosService;
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
        "/api/admin/reportes/matriculados"
)
@PreAuthorize(
        "hasRole('ADMINISTRADOR')"
)
public class ReporteMatriculadosAdminController {

    private final ReporteMatriculadosService
            reporteMatriculadosService;

    private final ReporteMatriculadosPdfService
            reporteMatriculadosPdfService;

    public ReporteMatriculadosAdminController(
            ReporteMatriculadosService
                    reporteMatriculadosService,

            ReporteMatriculadosPdfService
                    reporteMatriculadosPdfService
    ) {
        this.reporteMatriculadosService =
                reporteMatriculadosService;

        this.reporteMatriculadosPdfService =
                reporteMatriculadosPdfService;
    }

    @GetMapping
    public ReporteMatriculadosResponse generarReporte(
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
            String texto,

            @RequestParam(
                    required = false
            )
            EstadoUsuario estado
    ) {
        return reporteMatriculadosService
                .generarReporte(
                        fechaInicio,
                        fechaFin,
                        texto,
                        estado
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
            String texto,

            @RequestParam(
                    required = false
            )
            EstadoUsuario estado
    ) {
        byte[] pdf =
                reporteMatriculadosPdfService
                        .generarPdf(
                                fechaInicio,
                                fechaFin,
                                texto,
                                estado
                        );

        String nombreArchivo =
                "reporte-matriculados-"
                        + fechaInicio
                        + "-a-"
                        + fechaFin
                        + ".pdf";

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + nombreArchivo
                                + "\""
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .contentLength(
                        pdf.length
                )
                .body(pdf);
    }
}