package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.MovimientoRepository;


@Service
public class LoteServiceImpl
        implements LoteService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LoteRepository repository;

    private final MovimientoRepository movimientoRepository;

    private final ProductoService productoService;

    private final ProveedorService proveedorService;

    private final EmpresaService empresaService;

    private final UsuarioService usuarioService;

    private final AlertaService alertaService;

    private final FechaService fechaService;


    public LoteServiceImpl(
            LoteRepository repository,
            MovimientoRepository movimientoRepository,
            ProductoService productoService,
            ProveedorService proveedorService,
            EmpresaService empresaService,
            UsuarioService usuarioService,
            AlertaService alertaService,
            FechaService fechaService
    ) {
        this.repository = repository;
        this.movimientoRepository = movimientoRepository;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
    }


    @Override
    public List<Lote> listar(Usuario usuario, NivelAlerta nivel, EstadoLote estado) {

        List<Lote> lotes = lotesVisibles(usuario);
        Map<Long, NivelAlerta> niveles = alertaService.niveles(lotes);
        List<Lote> resultado = new ArrayList<>();

        for (Lote lote : lotes) {

            boolean estadoCoincide = estado == null
                ? esActivo(lote)
                : lote.getEstado() == estado;

            boolean nivelCoincide = nivel == null || niveles.get(lote.getId()) == nivel;

            if (estadoCoincide && nivelCoincide) {
                resultado.add(lote);
            }
        }

        return resultado;
    }


    @Override
    public List<Lote> lotesActivos(Usuario usuario) {

        return listar(usuario, null, null);
    }


    @Override
    public Lote obtenerPorId(Usuario usuario, Long id) {

        Lote lote = repository
                .findById(id == null ? -1L : id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Lote no encontrado"
                ));

        usuarioService.validarAccesoSucursal(usuario, lote.getSucursal());

        return lote;
    }


    @Override
    public List<Movimiento> movimientos(Usuario usuario, Long loteId) {

        Lote lote = obtenerPorId(usuario, loteId);

        return movimientoRepository.findByLoteIdOrderByFechaHoraAsc(lote.getId());
    }


    @Override
    @Transactional
    public Lote ingresar(
            Usuario usuario,
            Long productoId,
            Long proveedorId,
            String numeroLote,
            LocalDate fechaVencimiento,
            String mesAnio,
            Integer cantidad,
            boolean enEmpaques,
            BigDecimal costoUnitario
    ) {

        usuarioService.validarRol(usuario, Rol.TECNICO, Rol.QUIMICO_FARMACEUTICO);

        Producto producto = productoService.obtenerPorId(usuario, productoId);
        Proveedor proveedor = proveedorService.obtenerPorId(usuario, proveedorId);
        LocalDate vencimiento = resolverFechaVencimiento(fechaVencimiento, mesAnio);

        validarIngreso(numeroLote, cantidad, costoUnitario);

        int unidades = enEmpaques
            ? cantidad * producto.getUnidadesPorEmpaque()
            : cantidad;

        Lote lote = new Lote(
            producto, usuario.getSucursal(), proveedor, numeroLote.trim(),
            vencimiento, unidades, unidades, 0, costoUnitario,
            fechaService.hoy(), EstadoLote.DISPONIBLE, null, null
        );

        validarVidaUtil(usuario, lote);

        Lote guardado = sumarOCrear(lote);

        movimientoRepository.save(
            new Movimiento(
                guardado, null, TipoMovimiento.INGRESO, unidades,
                guardado.getCostoUnitario(), BigDecimal.ZERO,
                guardado.getCostoUnitario().multiply(BigDecimal.valueOf(unidades)),
                usuario, fechaService.ahora(),
                guardado.getId().equals(lote.getId()) ? "Ingreso de lote" : "Ingreso adicional al lote existente",
                lote.getAutorizadoPor() != null ? "Vida útil menor a la mínima, autorizado por químico farmacéutico" : null
            )
        );

        return guardado;
    }


    private List<Lote> lotesVisibles(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO);

        if (usuario.getRol() == Rol.GERENTE) {
            return repository.findBySucursalEmpresaIdOrderByFechaVencimientoAsc(
                usuario.getEmpresa().getId()
            );
        }

        return repository.findBySucursalIdOrderByFechaVencimientoAsc(
            usuario.getSucursal().getId()
        );
    }


    private boolean esActivo(Lote lote) {

        return lote.getEstado() == EstadoLote.DISPONIBLE || lote.getEstado() == EstadoLote.BLOQUEADO;
    }


    private LocalDate resolverFechaVencimiento(LocalDate fechaVencimiento, String mesAnio) {

        if (mesAnio != null && !mesAnio.isBlank()) {
            try {
                return alertaService.fechaFinDeMes(YearMonth.parse(mesAnio.trim()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                    "El mes y año de vencimiento no es válido"
                );
            }
        }

        if (fechaVencimiento == null) {
            throw new IllegalArgumentException(
                "La fecha de vencimiento es obligatoria"
            );
        }

        return fechaVencimiento;
    }


    private void validarIngreso(String numeroLote, Integer cantidad, BigDecimal costoUnitario) {

        if (numeroLote == null || numeroLote.isBlank()) {
            throw new IllegalArgumentException(
                "El número de lote es obligatorio"
            );
        }

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                "La cantidad debe ser mayor que cero"
            );
        }

        if (costoUnitario == null || costoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "El costo unitario debe ser mayor que cero"
            );
        }
    }


    private void validarVidaUtil(Usuario usuario, Lote lote) {

        long dias = alertaService.diasRestantes(lote);

        if (dias <= 0) {
            throw new IllegalArgumentException(
                "No se puede ingresar un lote vencido ni uno que vence hoy"
            );
        }

        int minimo = empresaService
                .obtenerConfiguracion(usuario.getEmpresa().getId())
                .getVidaUtilMinimaIngreso();

        if (dias >= minimo) {
            return;
        }

        if (usuario.getRol() != Rol.QUIMICO_FARMACEUTICO) {
            throw new IllegalStateException(
                "El lote tiene " + dias + " días de vida útil y el mínimo es " + minimo
                + ". Solo un químico farmacéutico puede registrarlo"
            );
        }

        lote.setAutorizadoPor(usuario);
    }


    private Lote sumarOCrear(Lote lote) {

        Lote existente = repository
                .findBySucursalIdAndProductoIdAndNumeroLote(
                    lote.getSucursal().getId(),
                    lote.getProducto().getId(),
                    lote.getNumeroLote()
                )
                .orElse(null);

        if (existente == null) {
            return repository.save(lote);
        }

        if (!existente.getFechaVencimiento().equals(lote.getFechaVencimiento())) {
            throw new IllegalArgumentException(
                "El lote " + lote.getNumeroLote() + " ya existe con vencimiento "
                + existente.getFechaVencimiento().format(FORMATO) + "; la fecha no coincide"
            );
        }

        if (existente.getEstado() == EstadoLote.DADO_DE_BAJA || existente.getEstado() == EstadoLote.DEVUELTO) {
            throw new IllegalStateException(
                "El lote " + lote.getNumeroLote() + " ya fue dado de baja o devuelto"
            );
        }

        existente.setCantidadInicial(existente.getCantidadInicial() + lote.getCantidadInicial());
        existente.setCantidadDisponible(existente.getCantidadDisponible() + lote.getCantidadInicial());

        if (existente.getEstado() == EstadoLote.AGOTADO) {
            existente.setEstado(EstadoLote.DISPONIBLE);
        }

        if (lote.getAutorizadoPor() != null) {
            existente.setAutorizadoPor(lote.getAutorizadoPor());
        }

        return repository.save(existente);
    }
}
