package pe.edu.empresa.farmalerta.service;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import pe.edu.empresa.farmalerta.model.Empresa;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Sucursal;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.repository.SucursalRepository;
import pe.edu.empresa.farmalerta.repository.UsuarioRepository;


@Service
public class UsuarioServiceImpl
        implements UsuarioService {

    private final UsuarioRepository repository;

    private final SucursalRepository sucursalRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public UsuarioServiceImpl(
            UsuarioRepository repository,
            SucursalRepository sucursalRepository
    ) {
        this.repository = repository;
        this.sucursalRepository = sucursalRepository;
    }


    @Override
    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {

        Usuario usuario = repository
                .findByNombreUsuario(nombreUsuario == null ? "" : nombreUsuario.trim())
                .orElse(null);

        if (usuario == null || contrasena == null || !encoder.matches(contrasena, usuario.getContrasena())) {
            throw new IllegalArgumentException(
                "Usuario o contraseña incorrectos"
            );
        }

        if (!usuario.getActivo()) {
            throw new IllegalStateException(
                "El usuario está inactivo"
            );
        }

        return usuario;
    }


    @Override
    public Usuario obtenerUsuarioSesion(HttpSession session) {

        Object valor = session.getAttribute("usuario");

        if (!(valor instanceof Usuario usuarioSesion)) {
            return null;
        }

        return repository
                .findById(usuarioSesion.getId())
                .filter(Usuario::getActivo)
                .orElse(null);
    }


    @Override
    public Usuario crearCuenta(Usuario usuario) {

        validarUsuario(usuario);

        usuario.setNombreUsuario(usuario.getNombreUsuario().trim());
        usuario.setContrasena(encoder.encode(usuario.getContrasena()));
        usuario.setActivo(true);

        return repository.save(usuario);
    }


    @Override
    public List<Usuario> listar(Usuario actual) {

        validarRol(actual, Rol.GERENTE);

        return repository.findByEmpresaIdOrderByNombreCompletoAsc(
            actual.getEmpresa().getId()
        );
    }


    @Override
    public Usuario registrar(Usuario actual, Usuario nuevo, Long sucursalId) {

        validarRol(actual, Rol.GERENTE);

        if (nuevo.getRol() == null || nuevo.getRol() == Rol.ADMIN_PLATAFORMA) {
            throw new IllegalArgumentException(
                "Seleccione un rol válido"
            );
        }

        nuevo.setEmpresa(actual.getEmpresa());

        if (nuevo.getRol() == Rol.GERENTE) {
            nuevo.setSucursal(null);
        } else {
            Sucursal sucursal = sucursalRepository
                    .findById(sucursalId == null ? -1L : sucursalId)
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Seleccione la sucursal del usuario"
                    ));

            validarAccesoEmpresa(actual, sucursal.getEmpresa());
            nuevo.setSucursal(sucursal);
        }

        return crearCuenta(nuevo);
    }


    @Override
    public void desactivar(Usuario actual, Long id) {

        validarRol(actual, Rol.GERENTE);

        Usuario usuario = repository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Usuario no encontrado"
                ));

        validarAccesoEmpresa(actual, usuario.getEmpresa());

        if (usuario.getId().equals(actual.getId())) {
            throw new IllegalStateException(
                "No puede desactivar su propia cuenta"
            );
        }

        usuario.setActivo(false);
        repository.save(usuario);
    }


    @Override
    public void validarRol(Usuario usuario, Rol... roles) {

        if (usuario == null || !Arrays.asList(roles).contains(usuario.getRol())) {
            throw new IllegalStateException(
                "Su rol no tiene permiso para realizar esta acción"
            );
        }
    }


    @Override
    public void validarAccesoEmpresa(Usuario usuario, Empresa empresa) {

        if (
            usuario.getEmpresa() == null ||
            empresa == null ||
            !usuario.getEmpresa().getId().equals(empresa.getId())
        ) {
            throw new IllegalStateException(
                "No tiene acceso a datos de otra empresa"
            );
        }
    }


    @Override
    public void validarAccesoSucursal(Usuario usuario, Sucursal sucursal) {

        validarAccesoEmpresa(usuario, sucursal.getEmpresa());

        if (
            usuario.getRol() != Rol.GERENTE &&
            !usuario.getSucursal().getId().equals(sucursal.getId())
        ) {
            throw new IllegalStateException(
                "No tiene acceso a datos de otra sucursal"
            );
        }
    }


    @Override
    public List<Sucursal> sucursalesVisibles(Usuario usuario) {

        if (usuario.getEmpresa() == null) {
            return List.of();
        }

        if (usuario.getRol() == Rol.GERENTE) {
            return sucursalRepository.findByEmpresaIdOrderByNombreAsc(
                usuario.getEmpresa().getId()
            );
        }

        return List.of(usuario.getSucursal());
    }


    private void validarUsuario(Usuario usuario) {

        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isBlank()) {
            throw new IllegalArgumentException(
                "El nombre de usuario es obligatorio"
            );
        }

        if (repository.existsByNombreUsuario(usuario.getNombreUsuario().trim())) {
            throw new IllegalArgumentException(
                "El nombre de usuario ya existe"
            );
        }

        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException(
                "El nombre completo es obligatorio"
            );
        }

        if (usuario.getContrasena() == null || usuario.getContrasena().length() < 8) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 8 caracteres"
            );
        }
    }
}
