package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import pe.edu.empresa.farmalerta.model.Producto;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.ProductoRepository;


@Service
public class ProductoServiceImpl
        implements ProductoService {

    private final ProductoRepository repository;

    private final LoteRepository loteRepository;

    private final UsuarioService usuarioService;


    public ProductoServiceImpl(
            ProductoRepository repository,
            LoteRepository loteRepository,
            UsuarioService usuarioService
    ) {
        this.repository = repository;
        this.loteRepository = loteRepository;
        this.usuarioService = usuarioService;
    }


    @Override
    public List<Producto> listar(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO);

        return repository.findByEmpresaIdOrderByNombreComercialAsc(
            usuario.getEmpresa().getId()
        );
    }


    @Override
    public Producto obtenerPorId(Usuario usuario, Long id) {

        Producto producto = repository
                .findById(id == null ? -1L : id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Producto no encontrado"
                ));

        usuarioService.validarAccesoEmpresa(usuario, producto.getEmpresa());

        return producto;
    }


    @Override
    public Producto guardar(Usuario usuario, Producto producto) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        validarProducto(producto);

        if (repository.existsByEmpresaIdAndCodigo(usuario.getEmpresa().getId(), producto.getCodigo().trim())) {
            throw new IllegalArgumentException(
                "Ya existe un producto con ese código"
            );
        }

        producto.setId(null);
        producto.setEmpresa(usuario.getEmpresa());
        producto.setCodigo(producto.getCodigo().trim());

        return repository.save(producto);
    }


    @Override
    public Producto actualizar(Usuario usuario, Long id, Producto datos) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        Producto producto = obtenerPorId(usuario, id);

        validarProducto(datos);

        producto.setNombreComercial(datos.getNombreComercial());
        producto.setPrincipioActivo(datos.getPrincipioActivo());
        producto.setConcentracion(datos.getConcentracion());
        producto.setFormaFarmaceutica(datos.getFormaFarmaceutica());
        producto.setCategoriaRegulatoria(datos.getCategoriaRegulatoria());
        producto.setCondicionVenta(datos.getCondicionVenta());
        producto.setConservacion(datos.getConservacion());
        producto.setPresentacion(datos.getPresentacion());
        producto.setUnidadesPorEmpaque(datos.getUnidadesPorEmpaque());
        producto.setLaboratorio(datos.getLaboratorio());
        producto.setRegistroSanitario(datos.getRegistroSanitario());
        producto.setPrecioVenta(datos.getPrecioVenta());
        producto.setUmbralAmarilloPropio(datos.getUmbralAmarilloPropio());
        producto.setUmbralRojoPropio(datos.getUmbralRojoPropio());

        return repository.save(producto);
    }


    @Override
    public void eliminar(Usuario usuario, Long id) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        Producto producto = obtenerPorId(usuario, id);

        if (loteRepository.existsByProductoId(producto.getId())) {
            throw new IllegalStateException(
                "No se puede eliminar un producto que tiene lotes"
            );
        }

        repository.delete(producto);
    }


    private void validarProducto(Producto producto) {

        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            throw new IllegalArgumentException(
                "El código es obligatorio"
            );
        }

        if (producto.getNombreComercial() == null || producto.getNombreComercial().isBlank()) {
            throw new IllegalArgumentException(
                "El nombre comercial es obligatorio"
            );
        }

        if (producto.getPrincipioActivo() == null || producto.getPrincipioActivo().isBlank()) {
            throw new IllegalArgumentException(
                "El principio activo es obligatorio"
            );
        }

        if (
            producto.getFormaFarmaceutica() == null ||
            producto.getCategoriaRegulatoria() == null ||
            producto.getCondicionVenta() == null ||
            producto.getConservacion() == null
        ) {
            throw new IllegalArgumentException(
                "Complete la clasificación farmacéutica del producto"
            );
        }

        if (producto.getUnidadesPorEmpaque() == null || producto.getUnidadesPorEmpaque() < 1) {
            throw new IllegalArgumentException(
                "Las unidades por empaque deben ser al menos 1"
            );
        }

        if (producto.getPrecioVenta() == null || producto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "El precio de venta debe ser mayor a cero"
            );
        }

        validarUmbralesPropios(producto);
    }


    private void validarUmbralesPropios(Producto producto) {

        Integer amarillo = producto.getUmbralAmarilloPropio();
        Integer rojo = producto.getUmbralRojoPropio();

        if (amarillo == null && rojo == null) {
            return;
        }

        if (amarillo == null || rojo == null || rojo <= 0 || amarillo <= rojo) {
            throw new IllegalArgumentException(
                "Los umbrales propios deben indicarse juntos y cumplir: AMARILLO > ROJO > 0"
            );
        }
    }
}
