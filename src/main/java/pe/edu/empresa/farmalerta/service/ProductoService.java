package pe.edu.empresa.farmalerta.service;

import java.util.List;

import pe.edu.empresa.farmalerta.model.Producto;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface ProductoService {

    List<Producto> listar(Usuario usuario);

    Producto obtenerPorId(Usuario usuario, Long id);

    Producto guardar(Usuario usuario, Producto producto);

    Producto actualizar(Usuario usuario, Long id, Producto producto);

    void eliminar(Usuario usuario, Long id);
}
