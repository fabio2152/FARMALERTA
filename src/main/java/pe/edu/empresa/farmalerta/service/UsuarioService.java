package pe.edu.empresa.farmalerta.service;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import pe.edu.empresa.farmalerta.model.Empresa;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Sucursal;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface UsuarioService {

    Usuario iniciarSesion(String nombreUsuario, String contrasena);

    Usuario obtenerUsuarioSesion(HttpSession session);

    Usuario crearCuenta(Usuario usuario);

    List<Usuario> listar(Usuario actual);

    Usuario registrar(Usuario actual, Usuario nuevo, Long sucursalId);

    void desactivar(Usuario actual, Long id);

    void validarRol(Usuario usuario, Rol... roles);

    void validarAccesoEmpresa(Usuario usuario, Empresa empresa);

    void validarAccesoSucursal(Usuario usuario, Sucursal sucursal);

    List<Sucursal> sucursalesVisibles(Usuario usuario);
}
