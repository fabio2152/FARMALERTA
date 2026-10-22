package pe.edu.empresa.farmalerta.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.AlertaSanitariaRepository;
import pe.edu.empresa.farmalerta.repository.LoteRepository;


@Service
public class AlertaSanitariaServiceImpl
        implements AlertaSanitariaService {

    private final AlertaSanitariaRepository repository;

    private final LoteRepository loteRepository;

    private final ProductoService productoService;

    private final UsuarioService usuarioService;

    private final AlertaService alertaService;

    private final FechaService fechaService;


    public AlertaSanitariaServiceImpl(
            AlertaSanitariaRepository repository,
            LoteRepository loteRepository,
            ProductoService productoService,
            UsuarioService usuarioService,
            AlertaService alertaService,
            FechaService fechaService
    ) {
        this.repository = repository;
        this.loteRepository = loteRepository;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
    }


    @Override
    public List<AlertaSanitaria> listar(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        return repository.findByEmpresaIdOrderByFechaRegistroDescIdDesc(usuario.getEmpresa().getId());
    }


    @Override
    @Transactional
    public int registrar(Usuario usuario, Long productoId, String numeroLote, String documentoReferencia) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        Producto producto = productoService.obtenerPorId(usuario, productoId);

        validarTexto(numeroLote, "El número de lote es obligatorio");
        validarTexto(documentoReferencia, "El documento de referencia es obligatorio");

        repository.save(
            new AlertaSanitaria(
                usuario.getEmpresa(), producto, numeroLote.trim(), documentoReferencia.trim(),
                fechaService.hoy(), usuario, false, null
            )
        );

        int bloqueados = 0;

        for (Lote lote : loteRepository.findByProductoIdAndNumeroLote(producto.getId(), numeroLote.trim())) {

            if (lote.getEstado() == EstadoLote.DISPONIBLE) {
                lote.setEstado(EstadoLote.BLOQUEADO);
                lote.setMotivoBloqueo(MotivoBloqueo.ALERTA_SANITARIA);
                loteRepository.save(lote);
                bloqueados++;
            }
        }

        return bloqueados;
    }


    @Override
    @Transactional
    public int levantar(Usuario usuario, Long id, String documentoLevantamiento) {

        usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

        AlertaSanitaria alerta = repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Alerta sanitaria no encontrada"
                ));

        usuarioService.validarAccesoEmpresa(usuario, alerta.getEmpresa());

        if (alerta.getLevantada()) {
            throw new IllegalStateException(
                "La alerta ya fue levantada"
            );
        }

        validarTexto(documentoLevantamiento, "Indique el documento que levanta la alerta");

        alerta.setLevantada(true);
        alerta.setDocumentoLevantamiento(documentoLevantamiento.trim());
        repository.save(alerta);

        int liberados = 0;

        for (Lote lote : loteRepository.findByProductoIdAndNumeroLote(alerta.getProducto().getId(), alerta.getNumeroLote())) {

            if (lote.getEstado() != EstadoLote.BLOQUEADO || lote.getMotivoBloqueo() != MotivoBloqueo.ALERTA_SANITARIA) {
                continue;
            }

            if (alertaService.calcularNivel(lote) == NivelAlerta.VENCIDO) {
                lote.setMotivoBloqueo(MotivoBloqueo.VENCIMIENTO);
            } else {
                lote.setEstado(EstadoLote.DISPONIBLE);
                lote.setMotivoBloqueo(null);
                liberados++;
            }

            loteRepository.save(lote);
        }

        return liberados;
    }


    private void validarTexto(String valor, String mensaje) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
