package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.empresa.farmalerta.model.ConfiguracionEmpresa;
import pe.edu.empresa.farmalerta.model.Empresa;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Sucursal;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.repository.ConfiguracionEmpresaRepository;
import pe.edu.empresa.farmalerta.repository.EmpresaRepository;
import pe.edu.empresa.farmalerta.repository.SucursalRepository;


@Service
public class EmpresaServiceImpl
        implements EmpresaService {

    private final EmpresaRepository repository;

    private final SucursalRepository sucursalRepository;

    private final ConfiguracionEmpresaRepository configuracionRepository;

    private final UsuarioService usuarioService;


    public EmpresaServiceImpl(
            EmpresaRepository repository,
            SucursalRepository sucursalRepository,
            ConfiguracionEmpresaRepository configuracionRepository,
            UsuarioService usuarioService
    ) {
        this.repository = repository;
        this.sucursalRepository = sucursalRepository;
        this.configuracionRepository = configuracionRepository;
        this.usuarioService = usuarioService;
    }


    @Override
    public List<Empresa> listarTodas() {

        return repository.findAllByOrderByRazonSocialAsc();
    }


    @Override
    public Empresa obtenerPorId(Long id) {

        return repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Empresa no encontrada"
                ));
    }


    @Override
    @Transactional
    public Empresa registrar(Usuario actual, Empresa empresa, Sucursal sucursal, Usuario gerente) {

        usuarioService.validarRol(actual, Rol.ADMIN_PLATAFORMA);

        validarEmpresa(empresa);
        validarSucursal(sucursal);

        Empresa guardada = repository.save(empresa);

        configuracionRepository.save(crearConfiguracionPorDefecto(guardada));

        sucursal.setEmpresa(guardada);
        sucursalRepository.save(sucursal);

        gerente.setRol(Rol.GERENTE);
        gerente.setEmpresa(guardada);
        gerente.setSucursal(null);
        usuarioService.crearCuenta(gerente);

        return guardada;
    }


    @Override
    public Sucursal agregarSucursal(Usuario actual, Long empresaId, Sucursal sucursal) {

        usuarioService.validarRol(actual, Rol.ADMIN_PLATAFORMA);

        validarSucursal(sucursal);

        sucursal.setEmpresa(obtenerPorId(empresaId));

        return sucursalRepository.save(sucursal);
    }


    @Override
    public List<Sucursal> listarSucursales(Long empresaId) {

        return sucursalRepository.findByEmpresaIdOrderByNombreAsc(empresaId);
    }


    @Override
    public Sucursal obtenerSucursal(Long id) {

        return sucursalRepository
                .findById(id == null ? -1L : id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sucursal no encontrada"
                ));
    }


    @Override
    public ConfiguracionEmpresa obtenerConfiguracion(Long empresaId) {

        return configuracionRepository
                .findByEmpresaId(empresaId)
                .orElseThrow(() -> new IllegalStateException(
                    "La empresa no tiene configuración"
                ));
    }


    @Override
    public ConfiguracionEmpresa actualizarConfiguracion(Usuario actual, ConfiguracionEmpresa datos) {

        usuarioService.validarRol(actual, Rol.GERENTE);

        validarConfiguracion(datos);

        ConfiguracionEmpresa configuracion =
            obtenerConfiguracion(actual.getEmpresa().getId());

        configuracion.setUmbralAmarillo(datos.getUmbralAmarillo());
        configuracion.setUmbralRojo(datos.getUmbralRojo());
        configuracion.setDescuentoAmarillo(datos.getDescuentoAmarillo());
        configuracion.setDescuentoRojo(datos.getDescuentoRojo());
        configuracion.setDescuentoUltimoDia(datos.getDescuentoUltimoDia());
        configuracion.setPermitirVentaUltimoDia(Boolean.TRUE.equals(datos.getPermitirVentaUltimoDia()));
        configuracion.setPermitirVentaBajoCosto(Boolean.TRUE.equals(datos.getPermitirVentaBajoCosto()));
        configuracion.setVidaUtilMinimaIngreso(datos.getVidaUtilMinimaIngreso());
        configuracion.setDiasTraslado(datos.getDiasTraslado());
        configuracion.setAlfaPrediccion(datos.getAlfaPrediccion());
        configuracion.setDiasHistorial(datos.getDiasHistorial());
        configuracion.setDiasHistorialMinimo(datos.getDiasHistorialMinimo());

        return configuracionRepository.save(configuracion);
    }


    private ConfiguracionEmpresa crearConfiguracionPorDefecto(Empresa empresa) {

        return new ConfiguracionEmpresa(
            empresa,
            90,
            30,
            new BigDecimal("10.00"),
            new BigDecimal("25.00"),
            new BigDecimal("40.00"),
            true,
            false,
            180,
            1,
            new BigDecimal("0.30"),
            60,
            14
        );
    }


    private void validarEmpresa(Empresa empresa) {

        if (empresa.getRazonSocial() == null || empresa.getRazonSocial().isBlank()) {
            throw new IllegalArgumentException(
                "La razón social es obligatoria"
            );
        }

        if (empresa.getRuc() == null || !empresa.getRuc().matches("\\d{11}")) {
            throw new IllegalArgumentException(
                "El RUC debe tener 11 dígitos"
            );
        }

        if (repository.existsByRuc(empresa.getRuc())) {
            throw new IllegalArgumentException(
                "Ya existe una empresa con ese RUC"
            );
        }
    }


    private void validarSucursal(Sucursal sucursal) {

        if (sucursal.getNombre() == null || sucursal.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                "El nombre de la sucursal es obligatorio"
            );
        }
    }


    private void validarConfiguracion(ConfiguracionEmpresa datos) {

        if (
            datos.getUmbralAmarillo() == null ||
            datos.getUmbralRojo() == null ||
            datos.getUmbralRojo() <= 0 ||
            datos.getUmbralAmarillo() <= datos.getUmbralRojo()
        ) {
            throw new IllegalArgumentException(
                "Los umbrales deben cumplir: AMARILLO > ROJO > 0"
            );
        }

        validarPorcentaje(datos.getDescuentoAmarillo(), "El descuento AMARILLO");
        validarPorcentaje(datos.getDescuentoRojo(), "El descuento ROJO");
        validarPorcentaje(datos.getDescuentoUltimoDia(), "El descuento de ÚLTIMO DÍA");

        if (datos.getVidaUtilMinimaIngreso() == null || datos.getVidaUtilMinimaIngreso() < 1) {
            throw new IllegalArgumentException(
                "La vida útil mínima debe ser al menos 1 día"
            );
        }

        if (datos.getDiasTraslado() == null || datos.getDiasTraslado() < 0) {
            throw new IllegalArgumentException(
                "Los días de traslado no pueden ser negativos"
            );
        }

        if (
            datos.getAlfaPrediccion() == null ||
            datos.getAlfaPrediccion().compareTo(BigDecimal.ZERO) <= 0 ||
            datos.getAlfaPrediccion().compareTo(BigDecimal.ONE) > 0
        ) {
            throw new IllegalArgumentException(
                "El valor de α debe estar entre 0 y 1"
            );
        }

        if (
            datos.getDiasHistorialMinimo() == null ||
            datos.getDiasHistorial() == null ||
            datos.getDiasHistorialMinimo() < 1 ||
            datos.getDiasHistorial() < datos.getDiasHistorialMinimo()
        ) {
            throw new IllegalArgumentException(
                "El historial debe ser mayor o igual que el historial mínimo"
            );
        }
    }


    private void validarPorcentaje(BigDecimal valor, String nombre) {

        if (
            valor == null ||
            valor.compareTo(BigDecimal.ZERO) < 0 ||
            valor.compareTo(new BigDecimal("100")) >= 0
        ) {
            throw new IllegalArgumentException(
                nombre + " debe estar entre 0 y 99,99 %"
            );
        }
    }
}
