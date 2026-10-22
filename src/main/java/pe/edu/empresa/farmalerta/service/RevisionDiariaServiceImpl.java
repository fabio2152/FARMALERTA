package pe.edu.empresa.farmalerta.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.EstadoLote;
import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.MotivoBloqueo;
import pe.edu.empresa.farmalerta.model.NivelAlerta;
import pe.edu.empresa.farmalerta.repository.LoteRepository;


@Service
public class RevisionDiariaServiceImpl
        implements RevisionDiariaService {

    private final LoteRepository loteRepository;

    private final AlertaService alertaService;

    private final NotificacionService notificacionService;


    public RevisionDiariaServiceImpl(
            LoteRepository loteRepository,
            AlertaService alertaService,
            NotificacionService notificacionService
    ) {
        this.loteRepository = loteRepository;
        this.alertaService = alertaService;
        this.notificacionService = notificacionService;
    }


    @Scheduled(cron = "0 5 0 * * *", zone = "America/Lima")
    public void ejecutarProgramada() {

        ejecutar();
    }


    @Override
    @Transactional
    public String ejecutar() {

        List<Lote> lotes = loteRepository.findByEstadoIn(
            List.of(EstadoLote.DISPONIBLE, EstadoLote.BLOQUEADO)
        );

        int bloqueados = 0;
        int notificaciones = 0;

        for (Lote lote : lotes) {

            NivelAlerta nivel = alertaService.calcularNivel(lote);

            if (lote.getEstado() == EstadoLote.DISPONIBLE && nivel == NivelAlerta.VENCIDO) {
                lote.setEstado(EstadoLote.BLOQUEADO);
                lote.setMotivoBloqueo(MotivoBloqueo.VENCIMIENTO);
                loteRepository.save(lote);
                bloqueados++;
            }

            if (notificacionService.notificar(lote, nivel)) {
                notificaciones++;
            }
        }

        return "Revisión diaria: " + bloqueados + " lotes bloqueados por vencimiento y "
            + notificaciones + " notificaciones nuevas.";
    }
}
