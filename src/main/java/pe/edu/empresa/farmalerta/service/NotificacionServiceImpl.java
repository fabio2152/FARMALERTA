package pe.edu.empresa.farmalerta.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.NivelAlerta;
import pe.edu.empresa.farmalerta.model.Notificacion;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.repository.NotificacionRepository;


@Service
public class NotificacionServiceImpl
        implements NotificacionService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final NotificacionRepository repository;

    private final UsuarioService usuarioService;

    private final AlertaService alertaService;

    private final FechaService fechaService;


    public NotificacionServiceImpl(
            NotificacionRepository repository,
            UsuarioService usuarioService,
            AlertaService alertaService,
            FechaService fechaService
    ) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
    }


    @Override
    public List<Notificacion> listar(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO);

        if (usuario.getRol() == Rol.GERENTE) {
            return repository.findBySucursalEmpresaIdOrderByFechaDescIdDesc(usuario.getEmpresa().getId());
        }

        return repository.findBySucursalIdOrderByFechaDescIdDesc(usuario.getSucursal().getId());
    }


    @Override
    public List<Notificacion> sinLeer(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO);

        if (usuario.getRol() == Rol.GERENTE) {
            return repository.findBySucursalEmpresaIdAndLeidaFalseOrderByFechaDescIdDesc(usuario.getEmpresa().getId());
        }

        return repository.findBySucursalIdAndLeidaFalseOrderByFechaDescIdDesc(usuario.getSucursal().getId());
    }


    @Override
    public void marcarLeida(Usuario usuario, Long id) {

        Notificacion notificacion = repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Notificación no encontrada"
                ));

        usuarioService.validarAccesoSucursal(usuario, notificacion.getSucursal());

        notificacion.setLeida(true);
        repository.save(notificacion);
    }


    @Override
    public boolean notificar(Lote lote, NivelAlerta nivel) {

        if (nivel == NivelAlerta.VERDE || repository.existsByLoteIdAndNivel(lote.getId(), nivel)) {
            return false;
        }

        repository.save(
            new Notificacion(
                lote.getSucursal(), lote, nivel, fechaService.hoy(), crearMensaje(lote, nivel), false
            )
        );

        return true;
    }


    private String crearMensaje(Lote lote, NivelAlerta nivel) {

        String producto = lote.getProducto().getNombreComercial() + " " + lote.getProducto().getConcentracion();
        String vencimiento = lote.getFechaVencimiento().format(FORMATO);

        return switch (nivel) {
            case VENCIDO -> "El lote " + lote.getNumeroLote() + " de " + producto + " venció el "
                + vencimiento + " y quedó bloqueado. Debe darse de baja.";
            case ULTIMO_DIA -> "El lote " + lote.getNumeroLote() + " de " + producto
                + " vence hoy. Solo se vende con confirmación del químico farmacéutico.";
            default -> "El lote " + lote.getNumeroLote() + " de " + producto + " entró en " + nivel
                + ": vence el " + vencimiento + " (" + alertaService.diasRestantes(lote) + " días).";
        };
    }
}
