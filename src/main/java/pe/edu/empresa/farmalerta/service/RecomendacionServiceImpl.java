package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.RecomendacionRepository;


@Service
public class RecomendacionServiceImpl
        implements RecomendacionService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final RecomendacionRepository repository;

    private final LoteRepository loteRepository;

    private final PrediccionService prediccionService;

    private final AlertaService alertaService;

    private final VentaService ventaService;

    private final TransferenciaService transferenciaService;

    private final DevolucionService devolucionService;

    private final EmpresaService empresaService;

    private final UsuarioService usuarioService;

    private final FechaService fechaService;


    public RecomendacionServiceImpl(
            RecomendacionRepository repository,
            LoteRepository loteRepository,
            PrediccionService prediccionService,
            AlertaService alertaService,
            VentaService ventaService,
            TransferenciaService transferenciaService,
            DevolucionService devolucionService,
            EmpresaService empresaService,
            UsuarioService usuarioService,
            FechaService fechaService
    ) {
        this.repository = repository;
        this.loteRepository = loteRepository;
        this.prediccionService = prediccionService;
        this.alertaService = alertaService;
        this.ventaService = ventaService;
        this.transferenciaService = transferenciaService;
        this.devolucionService = devolucionService;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.fechaService = fechaService;
    }


    @Override
    public List<Recomendacion> listar(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        if (usuario.getRol() == Rol.GERENTE) {
            return repository.findByLoteSucursalEmpresaIdOrderByFechaDescIdDesc(usuario.getEmpresa().getId());
        }

        return repository.findByLoteSucursalIdOrderByFechaDescIdDesc(usuario.getSucursal().getId());
    }


    @Override
    public int generar(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        return generarTodas();
    }


    @Override
    @Transactional
    public int generarTodas() {

        eliminarPendientesSinStock();

        List<Lote> lotes = loteRepository.findByEstadoIn(
            List.of(EstadoLote.DISPONIBLE, EstadoLote.BLOQUEADO)
        );

        Map<String, Double> demandas = new HashMap<>();
        Map<String, Map<Long, Integer>> riesgos = new HashMap<>();
        int generadas = 0;

        for (Lote lote : lotes) {

            String clave = lote.getProducto().getId() + "-" + lote.getSucursal().getId();

            if (!demandas.containsKey(clave)) {
                Double demanda = prediccionService.demandaEsperada(lote.getProducto(), lote.getSucursal());
                demandas.put(clave, demanda);
                riesgos.put(
                    clave,
                    demanda == null ? Map.of() : prediccionService.unidadesEnRiesgo(lote.getProducto(), lote.getSucursal(), demanda)
                );
            }

            Recomendacion recomendacion = evaluar(lote, demandas.get(clave), riesgos.get(clave));

            if (guardarSiCorresponde(recomendacion)) {
                generadas++;
            }
        }

        return generadas;
    }


    @Override
    @Transactional
    public Recomendacion aceptar(Usuario usuario, Long id) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Recomendacion recomendacion = obtenerPendiente(usuario, id);
        Lote lote = recomendacion.getLote();
        int cantidad = Math.min(
            recomendacion.getUnidadesEnRiesgo() == null ? 0 : recomendacion.getUnidadesEnRiesgo(),
            lote.getCantidadDisponible() - lote.getCantidadReservada()
        );

        if (recomendacion.getAccion() == AccionRecomendada.TRANSFERIR) {
            transferenciaService.solicitar(usuario, lote.getId(), recomendacion.getSucursalSugerida().getId(), cantidad);
        }

        if (recomendacion.getAccion() == AccionRecomendada.DEVOLVER) {
            devolucionService.devolver(usuario, lote.getId(), cantidad, lote.getProveedor().getId());
        }

        recomendacion.setEstado(EstadoRecomendacion.ACEPTADA);
        recomendacion.setDecididaPor(usuario);

        return repository.save(recomendacion);
    }


    @Override
    public Recomendacion rechazar(Usuario usuario, Long id, String motivo) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Recomendacion recomendacion = obtenerPendiente(usuario, id);

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                "Indique el motivo del rechazo"
            );
        }

        recomendacion.setEstado(EstadoRecomendacion.RECHAZADA);
        recomendacion.setDecididaPor(usuario);
        recomendacion.setMotivoRechazo(motivo.trim());

        return repository.save(recomendacion);
    }


    private Recomendacion evaluar(Lote lote, Double demanda, Map<Long, Integer> riesgos) {

        NivelAlerta nivel = alertaService.calcularNivel(lote);
        int vendible = lote.getCantidadDisponible() - lote.getCantidadReservada();

        Recomendacion recomendacion = new Recomendacion(
            lote, fechaService.hoy(), AccionRecomendada.MANTENER, null,
            demanda == null ? null : BigDecimal.valueOf(demanda).setScale(2, RoundingMode.HALF_UP),
            null, null, BigDecimal.ZERO.setScale(2), null, demanda == null,
            EstadoRecomendacion.PENDIENTE, null, null
        );

        if (lote.getEstado() == EstadoLote.BLOQUEADO || nivel == NivelAlerta.VENCIDO) {
            recomendarBaja(recomendacion, vendible);
            return recomendacion;
        }

        if (demanda == null) {
            recomendarSinPrediccion(recomendacion, nivel);
            return recomendacion;
        }

        int riesgo = riesgos.getOrDefault(lote.getId(), 0);
        recomendacion.setUnidadesEnRiesgo(riesgo);

        String inicio = riesgo + " unidades no se venderán antes del vencimiento (demanda "
            + formatear(demanda) + "/día). ";

        if (riesgo == 0) {
            recomendacion.setJustificacion(
                "Con una demanda de " + formatear(demanda) + "/día, las " + vendible
                + " unidades se venderán antes del vencimiento (" + alertaService.diasRestantes(lote) + " días)."
            );
            return recomendacion;
        }

        if (recomendarTransferencia(recomendacion, riesgo, inicio)) {
            return recomendacion;
        }

        if (recomendarDevolucion(recomendacion, riesgo, inicio)) {
            return recomendacion;
        }

        if (nivel == NivelAlerta.AMARILLO || nivel == NivelAlerta.ROJO || nivel == NivelAlerta.ULTIMO_DIA) {
            BigDecimal descuento = ventaService.descuentoDeNivel(lote);
            BigDecimal precio = ventaService.precioFinal(lote);

            recomendacion.setAccion(AccionRecomendada.DESCUENTO);
            recomendacion.setDescuentoSugerido(descuento);
            recomendacion.setValorRecuperable(precio.multiply(BigDecimal.valueOf(riesgo)));
            recomendacion.setJustificacion(
                inicio + "Ninguna sucursal puede absorberlas y no hay canje posible. Vender con "
                + descuento.stripTrailingZeros().toPlainString() + " % de descuento (S/ " + precio + " por unidad)."
            );
            return recomendacion;
        }

        recomendacion.setJustificacion(
            inicio + "El lote todavía está en VERDE: se reevaluará cuando entre en AMARILLO."
        );

        return recomendacion;
    }


    private void recomendarBaja(Recomendacion recomendacion, int vendible) {

        Lote lote = recomendacion.getLote();
        String motivo = lote.getMotivoBloqueo() == null ? "VENCIMIENTO" : lote.getMotivoBloqueo().name();

        recomendacion.setAccion(AccionRecomendada.BAJA);
        recomendacion.setSinPrediccion(false);
        recomendacion.setUnidadesEnRiesgo(vendible);
        recomendacion.setJustificacion(
            "El lote está bloqueado (" + motivo + "): no se puede vender, transferir ni devolver. "
            + "Corresponde darlo de baja; pérdida estimada S/ "
            + lote.getCostoUnitario().multiply(BigDecimal.valueOf(vendible)) + "."
        );
    }


    private void recomendarSinPrediccion(Recomendacion recomendacion, NivelAlerta nivel) {

        Lote lote = recomendacion.getLote();
        int minimo = empresaService.obtenerConfiguracion(lote.getProducto().getEmpresa().getId()).getDiasHistorialMinimo();
        String inicio = "Sin predicción: hay " + prediccionService.diasHistorial(lote.getProducto(), lote.getSucursal())
            + " días de historial de ventas y se requieren " + minimo + ". ";

        if (nivel == NivelAlerta.VERDE) {
            recomendacion.setJustificacion(inicio + "El lote está en VERDE.");
            return;
        }

        BigDecimal descuento = ventaService.descuentoDeNivel(lote);

        recomendacion.setAccion(AccionRecomendada.DESCUENTO);
        recomendacion.setDescuentoSugerido(descuento);
        recomendacion.setJustificacion(
            inicio + "Según su nivel " + nivel + ", se recomienda vender con "
            + descuento.stripTrailingZeros().toPlainString() + " % de descuento."
        );
    }


    private boolean recomendarTransferencia(Recomendacion recomendacion, int riesgo, String inicio) {

        Lote lote = recomendacion.getLote();
        long dias = alertaService.diasRestantes(lote);
        int traslado = empresaService.obtenerConfiguracion(lote.getProducto().getEmpresa().getId()).getDiasTraslado();

        if (dias <= traslado) {
            return false;
        }

        Sucursal mejor = null;
        long mejorCapacidad = 0;

        for (Sucursal sucursal : empresaService.listarSucursales(lote.getSucursal().getEmpresa().getId())) {

            if (sucursal.getId().equals(lote.getSucursal().getId())) {
                continue;
            }

            Double demanda = prediccionService.demandaEsperada(lote.getProducto(), sucursal);

            if (demanda == null) {
                continue;
            }

            long capacidad = (long) Math.floor(demanda * (dias - traslado)) - stockVendible(lote.getProducto(), sucursal);

            if (mejor == null || capacidad > mejorCapacidad) {
                mejor = sucursal;
                mejorCapacidad = capacidad;
            }
        }

        if (mejor == null || mejorCapacidad < riesgo) {
            return false;
        }

        recomendacion.setAccion(AccionRecomendada.TRANSFERIR);
        recomendacion.setSucursalSugerida(mejor);
        recomendacion.setValorRecuperable(lote.getCostoUnitario().multiply(BigDecimal.valueOf(riesgo)));
        recomendacion.setJustificacion(
            inicio + mejor.getNombre() + " puede absorber " + mejorCapacidad + " unidades en "
            + (dias - traslado) + " días."
        );

        return true;
    }


    private boolean recomendarDevolucion(Recomendacion recomendacion, int riesgo, String inicio) {

        Lote lote = recomendacion.getLote();
        Proveedor proveedor = lote.getProveedor();
        long dias = alertaService.diasRestantes(lote);

        if (!proveedor.getAceptaCanje() || dias < proveedor.getDiasMinimosCanje()) {
            return false;
        }

        BigDecimal valor = lote.getCostoUnitario()
                .multiply(BigDecimal.valueOf(riesgo))
                .multiply(proveedor.getPorcentajeReconocido())
                .divide(CIEN, 2, RoundingMode.HALF_UP);

        recomendacion.setAccion(AccionRecomendada.DEVOLVER);
        recomendacion.setValorRecuperable(valor);
        recomendacion.setJustificacion(
            inicio + "Ninguna sucursal puede absorberlas. " + proveedor.getRazonSocial()
            + " acepta canje con " + proveedor.getDiasMinimosCanje() + " días mínimos y quedan " + dias
            + ": se recuperan S/ " + valor + "."
        );

        return true;
    }


    private boolean guardarSiCorresponde(Recomendacion nueva) {

        Lote lote = nueva.getLote();
        Recomendacion pendiente = repository.findFirstByLoteIdAndEstado(lote.getId(), EstadoRecomendacion.PENDIENTE).orElse(null);

        boolean yaDecidida = repository.existsByLoteIdAndAccionAndEstadoIn(
            lote.getId(),
            nueva.getAccion(),
            List.of(EstadoRecomendacion.ACEPTADA, EstadoRecomendacion.RECHAZADA)
        );

        if (yaDecidida) {
            if (pendiente != null) {
                repository.delete(pendiente);
            }
            return false;
        }

        if (pendiente != null) {
            nueva.setId(pendiente.getId());
        }

        repository.save(nueva);

        return true;
    }


    private void eliminarPendientesSinStock() {

        for (Recomendacion recomendacion : repository.findByEstado(EstadoRecomendacion.PENDIENTE)) {

            EstadoLote estado = recomendacion.getLote().getEstado();

            if (estado != EstadoLote.DISPONIBLE && estado != EstadoLote.BLOQUEADO) {
                repository.delete(recomendacion);
            }
        }
    }


    private Recomendacion obtenerPendiente(Usuario usuario, Long id) {

        Recomendacion recomendacion = repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Recomendación no encontrada"
                ));

        usuarioService.validarAccesoSucursal(usuario, recomendacion.getLote().getSucursal());

        if (recomendacion.getEstado() != EstadoRecomendacion.PENDIENTE) {
            throw new IllegalStateException(
                "La recomendación ya fue decidida"
            );
        }

        return recomendacion;
    }


    private int stockVendible(Producto producto, Sucursal sucursal) {

        int stock = 0;

        for (Lote lote : loteRepository.findBySucursalIdAndProductoIdAndEstadoOrderByFechaVencimientoAsc(
                sucursal.getId(), producto.getId(), EstadoLote.DISPONIBLE)) {
            stock += lote.getCantidadDisponible() - lote.getCantidadReservada();
        }

        return stock;
    }


    private String formatear(double valor) {

        return BigDecimal.valueOf(valor).setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }
}
