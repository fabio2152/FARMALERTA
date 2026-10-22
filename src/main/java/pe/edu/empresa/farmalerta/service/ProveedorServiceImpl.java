package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import pe.edu.empresa.farmalerta.model.Proveedor;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.ProveedorRepository;


@Service
public class ProveedorServiceImpl
        implements ProveedorService {

    private final ProveedorRepository repository;

    private final LoteRepository loteRepository;

    private final UsuarioService usuarioService;


    public ProveedorServiceImpl(
            ProveedorRepository repository,
            LoteRepository loteRepository,
            UsuarioService usuarioService
    ) {
        this.repository = repository;
        this.loteRepository = loteRepository;
        this.usuarioService = usuarioService;
    }


    @Override
    public List<Proveedor> listar(Usuario usuario) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO);

        return repository.findByEmpresaIdOrderByRazonSocialAsc(
            usuario.getEmpresa().getId()
        );
    }


    @Override
    public Proveedor obtenerPorId(Usuario usuario, Long id) {

        Proveedor proveedor = repository
                .findById(id == null ? -1L : id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Proveedor no encontrado"
                ));

        usuarioService.validarAccesoEmpresa(usuario, proveedor.getEmpresa());

        return proveedor;
    }


    @Override
    public Proveedor guardar(Usuario usuario, Proveedor proveedor) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        validarProveedor(proveedor);

        proveedor.setId(null);
        proveedor.setEmpresa(usuario.getEmpresa());
        proveedor.setAceptaCanje(Boolean.TRUE.equals(proveedor.getAceptaCanje()));

        return repository.save(proveedor);
    }


    @Override
    public Proveedor actualizar(Usuario usuario, Long id, Proveedor datos) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        Proveedor proveedor = obtenerPorId(usuario, id);

        validarProveedor(datos);

        proveedor.setRazonSocial(datos.getRazonSocial());
        proveedor.setRuc(datos.getRuc());
        proveedor.setAceptaCanje(Boolean.TRUE.equals(datos.getAceptaCanje()));
        proveedor.setDiasMinimosCanje(datos.getDiasMinimosCanje());
        proveedor.setPorcentajeReconocido(datos.getPorcentajeReconocido());

        return repository.save(proveedor);
    }


    @Override
    public void eliminar(Usuario usuario, Long id) {

        usuarioService.validarRol(usuario, Rol.GERENTE, Rol.QUIMICO_FARMACEUTICO);

        Proveedor proveedor = obtenerPorId(usuario, id);

        if (loteRepository.existsByProveedorId(proveedor.getId())) {
            throw new IllegalStateException(
                "No se puede eliminar un proveedor que tiene lotes"
            );
        }

        repository.delete(proveedor);
    }


    private void validarProveedor(Proveedor proveedor) {

        if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().isBlank()) {
            throw new IllegalArgumentException(
                "La razón social es obligatoria"
            );
        }

        if (proveedor.getRuc() == null || !proveedor.getRuc().matches("\\d{11}")) {
            throw new IllegalArgumentException(
                "El RUC debe tener 11 dígitos"
            );
        }

        if (proveedor.getDiasMinimosCanje() == null || proveedor.getDiasMinimosCanje() < 0) {
            throw new IllegalArgumentException(
                "Los días mínimos de canje no pueden ser negativos"
            );
        }

        if (
            proveedor.getPorcentajeReconocido() == null ||
            proveedor.getPorcentajeReconocido().compareTo(BigDecimal.ZERO) < 0 ||
            proveedor.getPorcentajeReconocido().compareTo(new BigDecimal("100")) > 0
        ) {
            throw new IllegalArgumentException(
                "El porcentaje reconocido debe estar entre 0 y 100"
            );
        }
    }
}
