package pe.edu.empresa.farmalerta.service;

import java.util.List;

import pe.edu.empresa.farmalerta.model.Recomendacion;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface RecomendacionService {

    List<Recomendacion> listar(Usuario usuario);

    int generar(Usuario usuario);

    int generarTodas();

    Recomendacion aceptar(Usuario usuario, Long id);

    Recomendacion rechazar(Usuario usuario, Long id, String motivo);
}
