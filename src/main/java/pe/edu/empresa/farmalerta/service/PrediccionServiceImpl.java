package pe.edu.empresa.farmalerta.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.MovimientoRepository;


@Service
public class PrediccionServiceImpl
        implements PrediccionService {

    private final MovimientoRepository movimientoRepository;

    private final LoteRepository loteRepository;

    private final EmpresaService empresaService;

    private final AlertaService alertaService;

    private final FechaService fechaService;


    public PrediccionServiceImpl(
            MovimientoRepository movimientoRepository,
            LoteRepository loteRepository,
            EmpresaService empresaService,
            AlertaService alertaService,
            FechaService fechaService
    ) {
        this.movimientoRepository = movimientoRepository;
        this.loteRepository = loteRepository;
        this.empresaService = empresaService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
    }


    @Override
    public int diasHistorial(Producto producto, Sucursal sucursal) {

        LocalDate inicio = inicioHistorial(producto, sucursal);

        if (inicio == null) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(inicio, fechaService.hoy());
    }


    @Override
    public Double demandaEsperada(Producto producto, Sucursal sucursal) {

        ConfiguracionEmpresa configuracion = empresaService.obtenerConfiguracion(producto.getEmpresa().getId());
        LocalDate hoy = fechaService.hoy();
        LocalDate inicio = inicioHistorial(producto, sucursal);

        if (inicio == null) {
            return null;
        }

        int dias = (int) ChronoUnit.DAYS.between(inicio, hoy);

        if (dias < configuracion.getDiasHistorialMinimo()) {
            return null;
        }

        double[] ventasPorDia = new double[dias];

        List<Movimiento> ventas = movimientoRepository
                .findByTipoAndLoteProductoIdAndLoteSucursalIdAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                    TipoMovimiento.VENTA, producto.getId(), sucursal.getId(),
                    inicio.atStartOfDay(), hoy.atStartOfDay()
                );

        for (Movimiento venta : ventas) {
            int indice = (int) ChronoUnit.DAYS.between(inicio, venta.getFechaHora().toLocalDate());
            ventasPorDia[indice] += venta.getCantidad();
        }

        double alfa = configuracion.getAlfaPrediccion().doubleValue();
        double pronostico = ventasPorDia[0];

        for (int dia = 1; dia < dias; dia++) {
            pronostico = alfa * ventasPorDia[dia] + (1 - alfa) * pronostico;
        }

        return pronostico;
    }


    @Override
    public Map<Long, Integer> unidadesEnRiesgo(Producto producto, Sucursal sucursal, double demandaDiaria) {

        List<Lote> lotes = loteRepository.findBySucursalIdAndProductoIdAndEstadoOrderByFechaVencimientoAsc(
            sucursal.getId(),
            producto.getId(),
            EstadoLote.DISPONIBLE
        );

        List<Long> dias = new ArrayList<>();
        double[] restantes = new double[lotes.size()];
        long ultimoDia = 0;

        for (int i = 0; i < lotes.size(); i++) {
            Lote lote = lotes.get(i);
            dias.add(alertaService.diasRestantes(lote));
            restantes[i] = lote.getCantidadDisponible() - lote.getCantidadReservada();
            ultimoDia = Math.max(ultimoDia, dias.get(i));
        }

        for (long dia = 0; dia < ultimoDia; dia++) {

            double porVender = demandaDiaria;

            for (int i = 0; i < lotes.size() && porVender > 0; i++) {

                if (dias.get(i) > dia && restantes[i] > 0) {
                    double vendido = Math.min(restantes[i], porVender);
                    restantes[i] -= vendido;
                    porVender -= vendido;
                }
            }
        }

        Map<Long, Integer> riesgo = new HashMap<>();

        for (int i = 0; i < lotes.size(); i++) {
            riesgo.put(lotes.get(i).getId(), (int) Math.round(restantes[i]));
        }

        return riesgo;
    }


    private LocalDate inicioHistorial(Producto producto, Sucursal sucursal) {

        ConfiguracionEmpresa configuracion = empresaService.obtenerConfiguracion(producto.getEmpresa().getId());
        LocalDate hoy = fechaService.hoy();
        LocalDate desde = hoy.minusDays(configuracion.getDiasHistorial());

        Movimiento primeraVenta = movimientoRepository
                .findFirstByTipoAndLoteProductoIdAndLoteSucursalIdOrderByFechaHoraAsc(
                    TipoMovimiento.VENTA, producto.getId(), sucursal.getId()
                )
                .orElse(null);

        if (primeraVenta == null || !primeraVenta.getFechaHora().toLocalDate().isBefore(hoy)) {
            return null;
        }

        LocalDate inicio = primeraVenta.getFechaHora().toLocalDate();

        return inicio.isBefore(desde) ? desde : inicio;
    }
}
