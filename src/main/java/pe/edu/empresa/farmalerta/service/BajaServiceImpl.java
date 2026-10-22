package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.MovimientoRepository;
import pe.edu.empresa.farmalerta.repository.SalidaRepository;


@Service
public class BajaServiceImpl
        implements BajaService {

    private final LoteRepository loteRepository;

    private final SalidaRepository salidaRepository;

    private final MovimientoRepository movimientoRepository;

    private final UsuarioService usuarioService;

    private final AlertaService alertaService;

    private final FechaService fechaService;


    public BajaServiceImpl(
            LoteRepository loteRepository,
            SalidaRepository salidaRepository,
            MovimientoRepository movimientoRepository,
            UsuarioService usuarioService,
            AlertaService alertaService,
            FechaService fechaService
    ) {
        this.loteRepository = loteRepository;
        this.salidaRepository = salidaRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
    }


    @Override
    @Transactional
    public Movimiento darDeBaja(Usuario usuario, Long loteId, Integer cantidad, MotivoBaja motivo, String observacion) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Lote lote = loteRepository
                .findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Lote no encontrado"
                ));

        usuarioService.validarAccesoSucursal(usuario, lote.getSucursal());

        validarBaja(lote, cantidad, motivo, observacion);

        lote.setCantidadDisponible(lote.getCantidadDisponible() - cantidad);

        if (lote.getCantidadDisponible() == 0) {
            lote.setEstado(EstadoLote.DADO_DE_BAJA);
        }

        loteRepository.save(lote);

        Salida salida = salidaRepository.save(
            new Salida(
                lote.getSucursal(), usuario, fechaService.ahora(), TipoSalida.BAJA,
                EstadoSalida.REGISTRADA, null, null, null, lote.getProducto(), cantidad
            )
        );

        return movimientoRepository.save(
            new Movimiento(
                lote, salida, TipoMovimiento.BAJA, cantidad, lote.getCostoUnitario(), BigDecimal.ZERO,
                lote.getCostoUnitario().multiply(BigDecimal.valueOf(cantidad)), usuario,
                fechaService.ahora(), motivo.name(), observacion == null || observacion.isBlank() ? null : observacion.trim()
            )
        );
    }


    private void validarBaja(Lote lote, Integer cantidad, MotivoBaja motivo, String observacion) {

        if (lote.getEstado() != EstadoLote.DISPONIBLE && lote.getEstado() != EstadoLote.BLOQUEADO) {
            throw new IllegalStateException(
                "Solo se dan de baja lotes disponibles o bloqueados"
            );
        }

        if (motivo == null) {
            throw new IllegalArgumentException(
                "El motivo de la baja es obligatorio"
            );
        }

        int maximo = lote.getCantidadDisponible() - lote.getCantidadReservada();

        if (cantidad == null || cantidad <= 0 || cantidad > maximo) {
            throw new IllegalArgumentException(
                "La cantidad a dar de baja debe estar entre 1 y " + maximo
            );
        }

        boolean vencido = alertaService.calcularNivel(lote) == NivelAlerta.VENCIDO;

        if (!vencido && (observacion == null || observacion.isBlank())) {
            throw new IllegalArgumentException(
                "La baja de un lote no vencido exige una observación"
            );
        }
    }
}
