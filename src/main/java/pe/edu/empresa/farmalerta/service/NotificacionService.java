package pe.edu.empresa.farmalerta.service;

import java.util.List;

import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.NivelAlerta;
import pe.edu.empresa.farmalerta.model.Notificacion;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface NotificacionService {

    List<Notificacion> listar(Usuario usuario);

    List<Notificacion> sinLeer(Usuario usuario);

    void marcarLeida(Usuario usuario, Long id);

    boolean notificar(Lote lote, NivelAlerta nivel);
}
