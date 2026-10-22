package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.MovimientoRepository;
import pe.edu.empresa.farmalerta.repository.SalidaRepository;


@Service
public class DevolucionServiceImpl
        implements DevolucionService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final LoteRepository loteRepository;

    private final SalidaRepository salidaRepository;

    private final MovimientoRepository movimientoRepository;

    private final ProveedorService proveedorService;

    private final UsuarioService usuarioService;

    private final AlertaService alertaService;

    private final FechaService fechaService;


    public DevolucionServiceImpl(
            LoteRepository loteRepository,
            SalidaRepository salidaRepository,
            MovimientoRepository movimientoRepository,
            ProveedorService proveedorService,
            UsuarioService usuarioService,
            AlertaService alertaService,
            FechaService fechaService
    ) {
        this.loteRepository = loteRepository;
        this.salidaRepository = salidaRepository;
        this.movimientoRepository = movimientoRepository;
        this.proveedorService = proveedorService;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
    }


    @Override
    @Transactional
    public Movimiento devolver(Usuario usuario, Long loteId, Integer cantidad, Long proveedorId) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Lote lote = loteRepository
                .findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Lote no encontrado"
                ));

        usuarioService.validarAccesoSucursal(usuario, lote.getSucursal());

        Proveedor proveedor = proveedorService.obtenerPorId(usuario, proveedorId);

        validarDevolucion(lote, proveedor, cantidad);

        BigDecimal precio = lote.getCostoUnitario()
                .multiply(proveedor.getPorcentajeReconocido())
                .divide(CIEN, 2, RoundingMode.HALF_UP);

        BigDecimal valorRecuperado = lote.getCostoUnitario()
                .multiply(BigDecimal.valueOf(cantidad))
                .multiply(proveedor.getPorcentajeReconocido())
                .divide(CIEN, 2, RoundingMode.HALF_UP);

        lote.setCantidadDisponible(lote.getCantidadDisponible() - cantidad);

        if (lote.getCantidadDisponible() == 0) {
            lote.setEstado(EstadoLote.DEVUELTO);
        }

        loteRepository.save(lote);

        Salida salida = salidaRepository.save(
            new Salida(
                lote.getSucursal(), usuario, fechaService.ahora(), TipoSalida.DEVOLUCION,
                EstadoSalida.REGISTRADA, null, null, null, lote.getProducto(), cantidad
            )
        );

        return movimientoRepository.save(
            new Movimiento(
                lote, salida, TipoMovimiento.DEVOLUCION_PROVEEDOR, cantidad, precio, BigDecimal.ZERO,
                valorRecuperado, usuario, fechaService.ahora(), "Canje por vencimiento",
                "Devuelto a " + proveedor.getRazonSocial() + " (" + proveedor.getPorcentajeReconocido() + " % reconocido)"
            )
        );
    }


    private void validarDevolucion(Lote lote, Proveedor proveedor, Integer cantidad) {

        if (lote.getEstado() != EstadoLote.DISPONIBLE) {
            throw new IllegalStateException(
                "Solo se devuelven lotes disponibles; un lote bloqueado solo se da de baja"
            );
        }

        if (!proveedor.getId().equals(lote.getProveedor().getId())) {
            throw new IllegalArgumentException(
                "Solo se puede devolver al proveedor original del lote: " + lote.getProveedor().getRazonSocial()
            );
        }

        if (!proveedor.getAceptaCanje()) {
            throw new IllegalStateException(
                "El proveedor " + proveedor.getRazonSocial() + " no acepta canje"
            );
        }

        long dias = alertaService.diasRestantes(lote);

        if (dias < proveedor.getDiasMinimosCanje()) {
            throw new IllegalStateException(
                "El lote tiene " + dias + " días restantes y el proveedor exige al menos "
                + proveedor.getDiasMinimosCanje() + " para el canje"
            );
        }

        int maximo = lote.getCantidadDisponible() - lote.getCantidadReservada();

        if (cantidad == null || cantidad <= 0 || cantidad > maximo) {
            throw new IllegalArgumentException(
                "La cantidad a devolver debe estar entre 1 y " + maximo
            );
        }
    }
}
