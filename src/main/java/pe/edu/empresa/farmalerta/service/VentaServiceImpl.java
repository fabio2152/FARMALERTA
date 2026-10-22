package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.MovimientoRepository;
import pe.edu.empresa.farmalerta.repository.SalidaRepository;


@Service
public class VentaServiceImpl
        implements VentaService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final SalidaRepository repository;

    private final LoteRepository loteRepository;

    private final MovimientoRepository movimientoRepository;

    private final ProductoService productoService;

    private final EmpresaService empresaService;

    private final UsuarioService usuarioService;

    private final AlertaService alertaService;

    private final FechaService fechaService;


    public VentaServiceImpl(
            SalidaRepository repository,
            LoteRepository loteRepository,
            MovimientoRepository movimientoRepository,
            ProductoService productoService,
            EmpresaService empresaService,
            UsuarioService usuarioService,
            AlertaService alertaService,
            FechaService fechaService
    ) {
        this.repository = repository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoService = productoService;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
    }


    @Override
    @Transactional
    public Salida registrar(Usuario usuario, Long productoId, Integer cantidad, Integer diasTratamiento) {

        usuarioService.validarRol(usuario, Rol.TECNICO, Rol.QUIMICO_FARMACEUTICO);

        Producto producto = productoService.obtenerPorId(usuario, productoId);

        validarVenta(cantidad, diasTratamiento);

        ConfiguracionEmpresa configuracion = obtenerConfiguracion(producto);

        List<String> notas = new ArrayList<>();
        Map<Lote, Integer> plan = armarPlan(usuario.getSucursal(), producto, cantidad, diasTratamiento, configuracion, notas);

        Salida salida = new Salida(
            usuario.getSucursal(), usuario, fechaService.ahora(), TipoSalida.VENTA,
            EstadoSalida.REGISTRADA, diasTratamiento, null, null, producto, cantidad
        );

        Lote loteUltimoDia = buscarLoteUltimoDia(plan, configuracion);

        if (loteUltimoDia != null) {
            salida.setEstado(EstadoSalida.PENDIENTE_CONFIRMACION);
            salida.setAdvertencias(
                "Incluye el lote " + loteUltimoDia.getNumeroLote() + ", que vence hoy "
                + loteUltimoDia.getFechaVencimiento().format(FORMATO)
                + ". Requiere la confirmación de un químico farmacéutico; todavía no se descontó stock."
            );
            return repository.save(salida);
        }

        repository.save(salida);
        salida.setAdvertencias(unir(notas, aplicarPlan(salida, plan, usuario, configuracion)));

        return repository.save(salida);
    }


    @Override
    public List<Salida> listar(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO);

        if (usuario.getRol() == Rol.GERENTE) {
            return repository.findBySucursalEmpresaIdOrderByFechaHoraDesc(usuario.getEmpresa().getId());
        }

        return repository.findBySucursalIdOrderByFechaHoraDesc(usuario.getSucursal().getId());
    }


    @Override
    public Salida obtenerPorId(Usuario usuario, Long id) {

        Salida salida = repository
                .findById(id == null ? -1L : id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Salida no encontrada"
                ));

        usuarioService.validarAccesoSucursal(usuario, salida.getSucursal());

        return salida;
    }


    @Override
    public List<Movimiento> movimientos(Usuario usuario, Long salidaId) {

        Salida salida = obtenerPorId(usuario, salidaId);

        return movimientoRepository.findBySalidaIdOrderByIdAsc(salida.getId());
    }


    @Override
    public List<Salida> pendientes(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        return repository.findBySucursalIdAndEstadoOrderByFechaHoraAsc(
            usuario.getSucursal().getId(),
            EstadoSalida.PENDIENTE_CONFIRMACION
        );
    }


    @Override
    @Transactional
    public Salida confirmar(Usuario usuario, Long id) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Salida salida = obtenerPendiente(usuario, id);

        ConfiguracionEmpresa configuracion = obtenerConfiguracion(salida.getProducto());

        List<String> notas = new ArrayList<>();
        Map<Lote, Integer> plan = armarPlan(
            salida.getSucursal(), salida.getProducto(), salida.getCantidadSolicitada(),
            salida.getDiasTratamiento(), configuracion, notas
        );

        salida.setEstado(EstadoSalida.REGISTRADA);
        salida.setConfirmadaPor(usuario);
        salida.setAdvertencias(unir(notas, aplicarPlan(salida, plan, usuario, configuracion)));

        return repository.save(salida);
    }


    @Override
    public Salida rechazar(Usuario usuario, Long id) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Salida salida = obtenerPendiente(usuario, id);

        salida.setEstado(EstadoSalida.RECHAZADA);
        salida.setConfirmadaPor(usuario);
        salida.setAdvertencias(salida.getAdvertencias() + "\nRechazada por el químico farmacéutico.");

        return repository.save(salida);
    }


    @Override
    public BigDecimal descuentoDeNivel(Lote lote) {

        return descuento(alertaService.calcularNivel(lote), obtenerConfiguracion(lote.getProducto()));
    }


    @Override
    public BigDecimal precioFinal(Lote lote) {

        return precioConDescuento(lote, alertaService.calcularNivel(lote), obtenerConfiguracion(lote.getProducto()));
    }


    @Override
    public List<Lote> ofertas(Long empresaId) {

        ConfiguracionEmpresa configuracion = empresaService.obtenerConfiguracion(empresaId);

        List<Lote> lotes = loteRepository.findBySucursalEmpresaIdAndEstadoOrderByFechaVencimientoAsc(
            empresaId,
            EstadoLote.DISPONIBLE
        );

        List<Lote> ofertas = new ArrayList<>();

        for (Lote lote : lotes) {

            NivelAlerta nivel = alertaService.calcularNivel(lote);

            boolean nivelEnOferta = nivel == NivelAlerta.AMARILLO
                || nivel == NivelAlerta.ROJO
                || (nivel == NivelAlerta.ULTIMO_DIA && configuracion.getPermitirVentaUltimoDia());

            if (
                nivelEnOferta &&
                lote.getProducto().getCondicionVenta() == CondicionVenta.SIN_RECETA &&
                cantidadVendible(lote) > 0
            ) {
                ofertas.add(lote);
            }
        }

        return ofertas;
    }


    private Map<Lote, Integer> armarPlan(
            Sucursal sucursal,
            Producto producto,
            int cantidad,
            Integer diasTratamiento,
            ConfiguracionEmpresa configuracion,
            List<String> notas
    ) {

        List<Lote> lotes = loteRepository.findBySucursalIdAndProductoIdAndEstadoOrderByFechaVencimientoAsc(
            sucursal.getId(),
            producto.getId(),
            EstadoLote.DISPONIBLE
        );

        List<Lote> vendibles = new ArrayList<>();
        Lote mejorSinCobertura = null;
        int stockVendible = 0;

        for (Lote lote : lotes) {

            NivelAlerta nivel = alertaService.calcularNivel(lote);

            if (nivel == NivelAlerta.VENCIDO || cantidadVendible(lote) <= 0) {
                continue;
            }

            if (nivel == NivelAlerta.ULTIMO_DIA && !configuracion.getPermitirVentaUltimoDia()) {
                continue;
            }

            if (diasTratamiento != null && alertaService.diasRestantes(lote) + 1 < diasTratamiento) {
                mejorSinCobertura = lote;
                notas.add(
                    "Se omitió el lote " + lote.getNumeroLote() + " (válido hasta el "
                    + lote.getFechaVencimiento().format(FORMATO) + ") porque no cubre los "
                    + diasTratamiento + " días de tratamiento."
                );
                continue;
            }

            vendibles.add(lote);
            stockVendible += cantidadVendible(lote);
        }

        validarStock(cantidad, diasTratamiento, stockVendible, vendibles, mejorSinCobertura);

        Map<Lote, Integer> plan = new LinkedHashMap<>();
        int pendiente = cantidad;

        for (Lote lote : vendibles) {

            if (pendiente == 0) {
                break;
            }

            int tomar = Math.min(pendiente, cantidadVendible(lote));
            plan.put(lote, tomar);
            pendiente -= tomar;
        }

        return plan;
    }


    private String aplicarPlan(
            Salida salida,
            Map<Lote, Integer> plan,
            Usuario usuario,
            ConfiguracionEmpresa configuracion
    ) {

        List<String> advertencias = new ArrayList<>();

        for (Map.Entry<Lote, Integer> parte : plan.entrySet()) {

            Lote lote = parte.getKey();
            int cantidad = parte.getValue();
            NivelAlerta nivel = alertaService.calcularNivel(lote);

            BigDecimal precioLista = lote.getProducto().getPrecioVenta();
            BigDecimal precio = precioConDescuento(lote, nivel, configuracion);
            BigDecimal descuentoAplicado = precioLista
                    .subtract(precio)
                    .multiply(CIEN)
                    .divide(precioLista, 2, RoundingMode.HALF_UP);

            lote.setCantidadDisponible(lote.getCantidadDisponible() - cantidad);

            if (lote.getCantidadDisponible() == 0) {
                lote.setEstado(EstadoLote.AGOTADO);
            }

            loteRepository.save(lote);

            movimientoRepository.save(
                new Movimiento(
                    lote, salida, TipoMovimiento.VENTA, cantidad, precio, descuentoAplicado,
                    precio.multiply(BigDecimal.valueOf(cantidad)), usuario, fechaService.ahora(),
                    "Venta", nivel == NivelAlerta.VERDE ? null : "Nivel " + nivel
                )
            );

            String vencimiento = lote.getFechaVencimiento().format(FORMATO);

            if (nivel == NivelAlerta.ULTIMO_DIA) {
                advertencias.add(
                    "Producto válido solo hasta hoy, " + vencimiento
                    + ". Debe usarse hoy. No consumir después de esta fecha."
                );
            } else if (nivel == NivelAlerta.AMARILLO || nivel == NivelAlerta.ROJO) {
                advertencias.add(
                    "Lote " + lote.getNumeroLote() + ": vence el " + vencimiento
                    + ". Informe al paciente la fecha de vencimiento."
                );
            }
        }

        return advertencias.isEmpty() ? null : String.join("\n", advertencias);
    }


    private BigDecimal precioConDescuento(Lote lote, NivelAlerta nivel, ConfiguracionEmpresa configuracion) {

        BigDecimal precioLista = lote.getProducto().getPrecioVenta();
        BigDecimal descuento = descuento(nivel, configuracion);

        BigDecimal precio = precioLista
                .multiply(CIEN.subtract(descuento))
                .divide(CIEN, 2, RoundingMode.HALF_UP);

        if (descuento.signum() > 0 && !configuracion.getPermitirVentaBajoCosto()) {

            BigDecimal piso = lote.getCostoUnitario().min(precioLista);

            if (precio.compareTo(piso) < 0) {
                precio = piso;
            }
        }

        return precio.setScale(2, RoundingMode.HALF_UP);
    }


    private BigDecimal descuento(NivelAlerta nivel, ConfiguracionEmpresa configuracion) {

        return switch (nivel) {
            case AMARILLO -> configuracion.getDescuentoAmarillo();
            case ROJO -> configuracion.getDescuentoRojo();
            case ULTIMO_DIA -> configuracion.getDescuentoUltimoDia();
            default -> BigDecimal.ZERO;
        };
    }


    private Lote buscarLoteUltimoDia(Map<Lote, Integer> plan, ConfiguracionEmpresa configuracion) {

        if (!configuracion.getPermitirVentaUltimoDia()) {
            return null;
        }

        for (Lote lote : plan.keySet()) {
            if (alertaService.calcularNivel(lote) == NivelAlerta.ULTIMO_DIA) {
                return lote;
            }
        }

        return null;
    }


    private Salida obtenerPendiente(Usuario usuario, Long id) {

        Salida salida = obtenerPorId(usuario, id);

        if (salida.getEstado() != EstadoSalida.PENDIENTE_CONFIRMACION) {
            throw new IllegalStateException(
                "La venta no está pendiente de confirmación"
            );
        }

        return salida;
    }


    private String unir(List<String> notas, String advertencias) {

        List<String> todas = new ArrayList<>(notas);

        if (advertencias != null) {
            todas.add(advertencias);
        }

        return todas.isEmpty() ? null : String.join("\n", todas);
    }


    private ConfiguracionEmpresa obtenerConfiguracion(Producto producto) {

        return empresaService.obtenerConfiguracion(producto.getEmpresa().getId());
    }


    private int cantidadVendible(Lote lote) {

        return lote.getCantidadDisponible() - lote.getCantidadReservada();
    }


    private void validarVenta(Integer cantidad, Integer diasTratamiento) {

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                "La cantidad debe ser mayor que cero"
            );
        }

        if (diasTratamiento != null && diasTratamiento < 1) {
            throw new IllegalArgumentException(
                "Los días de tratamiento deben ser al menos 1"
            );
        }
    }


    private void validarStock(
            int cantidad,
            Integer diasTratamiento,
            int stockVendible,
            List<Lote> vendibles,
            Lote mejorSinCobertura
    ) {

        if (stockVendible >= cantidad) {
            return;
        }

        if (vendibles.isEmpty() && mejorSinCobertura != null) {
            throw new IllegalStateException(
                "Ningún lote cubre un tratamiento de " + diasTratamiento + " días. El mejor lote ("
                + mejorSinCobertura.getNumeroLote() + ") solo es válido hasta el "
                + mejorSinCobertura.getFechaVencimiento().format(FORMATO) + "."
            );
        }

        String mensaje = "Stock vendible insuficiente: hay " + stockVendible
            + " unidades y se solicitaron " + cantidad + ".";

        if (mejorSinCobertura != null) {
            mensaje += " Se excluyeron lotes que no cubren el tratamiento de " + diasTratamiento + " días.";
        }

        throw new IllegalStateException(mensaje);
    }
}
