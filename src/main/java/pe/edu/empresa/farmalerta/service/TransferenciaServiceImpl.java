package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.MovimientoRepository;
import pe.edu.empresa.farmalerta.repository.SalidaRepository;
import pe.edu.empresa.farmalerta.repository.TransferenciaRepository;


@Service
public class TransferenciaServiceImpl
        implements TransferenciaService {

    private final TransferenciaRepository repository;

    private final LoteRepository loteRepository;

    private final SalidaRepository salidaRepository;

    private final MovimientoRepository movimientoRepository;

    private final EmpresaService empresaService;

    private final UsuarioService usuarioService;

    private final AlertaService alertaService;

    private final FechaService fechaService;


    public TransferenciaServiceImpl(
            TransferenciaRepository repository,
            LoteRepository loteRepository,
            SalidaRepository salidaRepository,
            MovimientoRepository movimientoRepository,
            EmpresaService empresaService,
            UsuarioService usuarioService,
            AlertaService alertaService,
            FechaService fechaService
    ) {
        this.repository = repository;
        this.loteRepository = loteRepository;
        this.salidaRepository = salidaRepository;
        this.movimientoRepository = movimientoRepository;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
    }


    @Override
    public List<Transferencia> listar(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO);

        if (usuario.getRol() == Rol.GERENTE) {
            return repository.findBySucursalOrigenEmpresaIdOrderByFechaSolicitudDesc(usuario.getEmpresa().getId());
        }

        Long sucursalId = usuario.getSucursal().getId();

        return repository.findBySucursalOrigenIdOrSucursalDestinoIdOrderByFechaSolicitudDesc(sucursalId, sucursalId);
    }


    @Override
    public List<Sucursal> destinosPosibles(Usuario usuario) {

        List<Sucursal> destinos = new ArrayList<>();

        for (Sucursal sucursal : empresaService.listarSucursales(usuario.getEmpresa().getId())) {
            if (usuario.getSucursal() == null || !sucursal.getId().equals(usuario.getSucursal().getId())) {
                destinos.add(sucursal);
            }
        }

        return destinos;
    }


    @Override
    @Transactional
    public Transferencia solicitar(Usuario usuario, Long loteId, Long sucursalDestinoId, Integer cantidad) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Lote lote = loteRepository
                .findById(loteId == null ? -1L : loteId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Lote no encontrado"
                ));

        usuarioService.validarAccesoSucursal(usuario, lote.getSucursal());

        Sucursal destino = empresaService.obtenerSucursal(sucursalDestinoId);

        validarSolicitud(lote, destino, cantidad);

        lote.setCantidadReservada(lote.getCantidadReservada() + cantidad);
        loteRepository.save(lote);

        return repository.save(
            new Transferencia(
                lote, lote.getSucursal(), destino, cantidad, EstadoTransferencia.SOLICITADA,
                usuario, null, fechaService.ahora(), null
            )
        );
    }


    @Override
    @Transactional
    public Transferencia recibir(Usuario usuario, Long id) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO);

        Transferencia transferencia = obtenerSolicitada(id);

        if (!usuario.getSucursal().getId().equals(transferencia.getSucursalDestino().getId())) {
            throw new IllegalStateException(
                "Solo un usuario de la sucursal de destino puede recibir la transferencia"
            );
        }

        Lote origen = transferencia.getLote();
        int cantidad = transferencia.getCantidad();

        if (origen.getEstado() != EstadoLote.DISPONIBLE || alertaService.calcularNivel(origen) == NivelAlerta.VENCIDO) {
            throw new IllegalStateException(
                "El lote de origen está bloqueado o vencido; la sucursal de origen debe cancelar la transferencia"
            );
        }

        origen.setCantidadDisponible(origen.getCantidadDisponible() - cantidad);
        origen.setCantidadReservada(origen.getCantidadReservada() - cantidad);

        if (origen.getCantidadDisponible() == 0) {
            origen.setEstado(EstadoLote.AGOTADO);
        }

        loteRepository.save(origen);

        Lote destino = sumarOCrearEnDestino(origen, transferencia.getSucursalDestino(), cantidad);

        Salida salida = salidaRepository.save(
            new Salida(
                origen.getSucursal(), transferencia.getSolicitadaPor(), fechaService.ahora(), TipoSalida.TRANSFERENCIA,
                EstadoSalida.REGISTRADA, null, null, usuario, origen.getProducto(), cantidad
            )
        );

        BigDecimal valor = origen.getCostoUnitario().multiply(BigDecimal.valueOf(cantidad));

        movimientoRepository.save(
            new Movimiento(
                origen, salida, TipoMovimiento.TRANSFERENCIA_SALIDA, cantidad, origen.getCostoUnitario(),
                BigDecimal.ZERO, valor, usuario, fechaService.ahora(), "Transferencia",
                "Hacia " + transferencia.getSucursalDestino().getNombre()
            )
        );

        movimientoRepository.save(
            new Movimiento(
                destino, null, TipoMovimiento.TRANSFERENCIA_ENTRADA, cantidad, origen.getCostoUnitario(),
                BigDecimal.ZERO, valor, usuario, fechaService.ahora(), "Transferencia",
                "Desde " + transferencia.getSucursalOrigen().getNombre()
            )
        );

        transferencia.setEstado(EstadoTransferencia.RECIBIDA);
        transferencia.setRecibidaPor(usuario);
        transferencia.setFechaRecepcion(fechaService.ahora());

        return repository.save(transferencia);
    }


    @Override
    @Transactional
    public Transferencia cancelar(Usuario usuario, Long id) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Transferencia transferencia = obtenerSolicitada(id);

        if (!usuario.getSucursal().getId().equals(transferencia.getSucursalOrigen().getId())) {
            throw new IllegalStateException(
                "Solo el químico farmacéutico de la sucursal de origen puede cancelarla"
            );
        }

        Lote lote = transferencia.getLote();
        lote.setCantidadReservada(lote.getCantidadReservada() - transferencia.getCantidad());
        loteRepository.save(lote);

        transferencia.setEstado(EstadoTransferencia.CANCELADA);

        return repository.save(transferencia);
    }


    private Transferencia obtenerSolicitada(Long id) {

        Transferencia transferencia = repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Transferencia no encontrada"
                ));

        if (transferencia.getEstado() != EstadoTransferencia.SOLICITADA) {
            throw new IllegalStateException(
                "La transferencia ya fue recibida o cancelada"
            );
        }

        return transferencia;
    }


    private void validarSolicitud(Lote lote, Sucursal destino, Integer cantidad) {

        if (!destino.getEmpresa().getId().equals(lote.getSucursal().getEmpresa().getId())) {
            throw new IllegalArgumentException(
                "Solo se puede transferir entre sucursales de la misma empresa"
            );
        }

        if (destino.getId().equals(lote.getSucursal().getId())) {
            throw new IllegalArgumentException(
                "La sucursal de destino debe ser distinta de la de origen"
            );
        }

        if (lote.getEstado() != EstadoLote.DISPONIBLE) {
            throw new IllegalStateException(
                "Solo se transfieren lotes disponibles"
            );
        }

        long dias = alertaService.diasRestantes(lote);
        int traslado = empresaService
                .obtenerConfiguracion(lote.getSucursal().getEmpresa().getId())
                .getDiasTraslado();

        if (dias <= traslado) {
            throw new IllegalStateException(
                "El lote vence en " + dias + " días y el traslado toma " + traslado + "; no se puede transferir"
            );
        }

        int maximo = lote.getCantidadDisponible() - lote.getCantidadReservada();

        if (cantidad == null || cantidad <= 0 || cantidad > maximo) {
            throw new IllegalArgumentException(
                "La cantidad a transferir debe estar entre 1 y " + maximo
            );
        }
    }


    private Lote sumarOCrearEnDestino(Lote origen, Sucursal sucursalDestino, int cantidad) {

        Lote existente = loteRepository
                .findBySucursalIdAndProductoIdAndNumeroLote(
                    sucursalDestino.getId(),
                    origen.getProducto().getId(),
                    origen.getNumeroLote()
                )
                .orElse(null);

        if (existente == null) {
            return loteRepository.save(
                new Lote(
                    origen.getProducto(), sucursalDestino, origen.getProveedor(), origen.getNumeroLote(),
                    origen.getFechaVencimiento(), cantidad, cantidad, 0, origen.getCostoUnitario(),
                    fechaService.hoy(), EstadoLote.DISPONIBLE, null, null
                )
            );
        }

        if (
            !existente.getFechaVencimiento().equals(origen.getFechaVencimiento()) ||
            existente.getEstado() == EstadoLote.DADO_DE_BAJA ||
            existente.getEstado() == EstadoLote.DEVUELTO
        ) {
            throw new IllegalStateException(
                "En el destino ya existe el lote " + origen.getNumeroLote() + " con otra fecha o ya cerrado"
            );
        }

        existente.setCantidadInicial(existente.getCantidadInicial() + cantidad);
        existente.setCantidadDisponible(existente.getCantidadDisponible() + cantidad);

        if (existente.getEstado() == EstadoLote.AGOTADO) {
            existente.setEstado(EstadoLote.DISPONIBLE);
        }

        return loteRepository.save(existente);
    }
}
