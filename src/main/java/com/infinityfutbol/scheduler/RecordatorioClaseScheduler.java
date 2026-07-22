package com.infinityfutbol.scheduler;

import com.infinityfutbol.service.NotificacionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecordatorioClaseScheduler {

    private final NotificacionService notificacionService;

    public RecordatorioClaseScheduler(
            NotificacionService notificacionService
    ) {
        this.notificacionService =
                notificacionService;
    }

    @Scheduled(
            fixedDelayString =
                    "${notificaciones.recordatorios.intervalo-ms:60000}"
    )
    public void procesarRecordatorios() {
        int cantidadProcesada =
                notificacionService
                        .activarRecordatoriosPendientes();

        if (cantidadProcesada > 0) {
            System.out.println(
                    "Recordatorios de clase activados: "
                            + cantidadProcesada
            );
        }
    }
}