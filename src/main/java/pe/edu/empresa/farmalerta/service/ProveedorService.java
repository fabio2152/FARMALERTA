package pe.edu.empresa.farmalerta.service;

import java.util.List;

import pe.edu.empresa.farmalerta.model.Proveedor;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface ProveedorService {

    List<Proveedor> listar(Usuario usuario);

    Proveedor obtenerPorId(Usuario usuario, Long id);

    Proveedor guardar(Usuario usuario, Proveedor proveedor);

    Proveedor actualizar(Usuario usuario, Long id, Proveedor proveedor);

    void eliminar(Usuario usuario, Long id);
}
