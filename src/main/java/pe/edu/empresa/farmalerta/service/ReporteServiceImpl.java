package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.MovimientoRepository;


@Service
public class ReporteServiceImpl
        implements ReporteService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final MovimientoRepository movimientoRepository;

    private final UsuarioService usuarioService;


    public ReporteServiceImpl(
            MovimientoRepository movimientoRepository,
            UsuarioService usuarioService
    ) {
        this.movimientoRepository = movimientoRepository;
        this.usuarioService = usuarioService;
    }


    @Override
    public Map<String, Object> reporteMensual(Usuario usuario, YearMonth mes) {

        List<Movimiento> movimientos = movimientos(usuario, mes.atDay(1), mes.atEndOfMonth());

        BigDecimal perdida = BigDecimal.ZERO.setScale(2);
        BigDecimal recuperado = BigDecimal.ZERO.setScale(2);
        BigDecimal transferido = BigDecimal.ZERO.setScale(2);
        BigDecimal ventasConDescuento = BigDecimal.ZERO.setScale(2);
        BigDecimal ventasTotales = BigDecimal.ZERO.setScale(2);
        int unidadesVencidas = 0;
        Map<String, BigDecimal> perdidaPorProducto = new HashMap<>();

        for (Movimiento movimiento : movimientos) {

            BigDecimal monto = movimiento.getMontoTotal();

            switch (movimiento.getTipo()) {
                case BAJA -> {
                    perdida = perdida.add(monto);
                    perdidaPorProducto.merge(nombreProducto(movimiento.getLote().getProducto()), monto, BigDecimal::add);

                    if (MotivoBaja.VENCIDO.name().equals(movimiento.getMotivo())) {
                        unidadesVencidas += movimiento.getCantidad();
                    }
                }
                case DEVOLUCION_PROVEEDOR -> recuperado = recuperado.add(monto);
                case TRANSFERENCIA_SALIDA -> transferido = transferido.add(monto);
                case VENTA -> {
                    ventasTotales = ventasTotales.add(monto);

                    if (movimiento.getDescuentoAplicado().signum() > 0) {
                        ventasConDescuento = ventasConDescuento.add(monto);
                    }
                }
                default -> {
                }
            }
        }

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("perdidaBajas", perdida);
        reporte.put("valorRecuperado", recuperado);
        reporte.put("valorTransferido", transferido);
        reporte.put("ventasConDescuento", ventasConDescuento);
        reporte.put("ventasTotales", ventasTotales);
        reporte.put("unidadesVencidas", unidadesVencidas);
        reporte.put("topPerdidas", cincoMayores(perdidaPorProducto));

        return reporte;
    }


    @Override
    public String actaBajasCsv(Usuario usuario, LocalDate desde, LocalDate hasta) {

        if (desde == null || hasta == null || hasta.isBefore(desde)) {
            throw new IllegalArgumentException(
                "Indique un rango de fechas válido"
            );
        }

        StringBuilder csv = new StringBuilder("﻿");

        csv.append("fecha,sucursal,producto,principio activo,forma farmacéutica,número de lote,")
           .append("fecha de vencimiento,cantidad,costo unitario,pérdida,motivo,observación,responsable\n");

        for (Movimiento movimiento : movimientos(usuario, desde, hasta)) {

            if (movimiento.getTipo() != TipoMovimiento.BAJA) {
                continue;
            }

            Lote lote = movimiento.getLote();

            List<String> columnas = List.of(
                movimiento.getFechaHora().format(FECHA_HORA),
                lote.getSucursal().getNombre(),
                nombreProducto(lote.getProducto()),
                lote.getProducto().getPrincipioActivo(),
                lote.getProducto().getFormaFarmaceutica().name(),
                lote.getNumeroLote(),
                lote.getFechaVencimiento().format(FECHA),
                movimiento.getCantidad().toString(),
                movimiento.getPrecioUnitario().toPlainString(),
                movimiento.getMontoTotal().toPlainString(),
                movimiento.getMotivo(),
                movimiento.getObservacion() == null ? "" : movimiento.getObservacion(),
                movimiento.getUsuario().getNombreCompleto()
            );

            List<String> escapadas = new ArrayList<>();

            for (String columna : columnas) {
                escapadas.add("\"" + columna.replace("\"", "\"\"") + "\"");
            }

            csv.append(String.join(",", escapadas)).append("\n");
        }

        return csv.toString();
    }


    private List<Movimiento> movimientos(Usuario usuario, LocalDate desde, LocalDate hasta) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        if (usuario.getRol() == Rol.GERENTE) {
            return movimientoRepository.findByLoteSucursalEmpresaIdAndFechaHoraGreaterThanEqualAndFechaHoraLessThanOrderByFechaHoraAsc(
                usuario.getEmpresa().getId(), desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay()
            );
        }

        return movimientoRepository.findByLoteSucursalIdAndFechaHoraGreaterThanEqualAndFechaHoraLessThanOrderByFechaHoraAsc(
            usuario.getSucursal().getId(), desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay()
        );
    }


    private Map<String, BigDecimal> cincoMayores(Map<String, BigDecimal> perdidas) {

        Map<String, BigDecimal> resultado = new LinkedHashMap<>();

        perdidas.entrySet()
                .stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .forEach(entrada -> resultado.put(entrada.getKey(), entrada.getValue()));

        return resultado;
    }


    private String nombreProducto(Producto producto) {

        return producto.getNombreComercial() + " " + producto.getConcentracion();
    }
}
