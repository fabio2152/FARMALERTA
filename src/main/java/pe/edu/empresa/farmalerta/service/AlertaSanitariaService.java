package pe.edu.empresa.farmalerta.service;

import java.util.List;

import pe.edu.empresa.farmalerta.model.AlertaSanitaria;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface AlertaSanitariaService {

    List<AlertaSanitaria> listar(Usuario usuario);

    int registrar(Usuario usuario, Long productoId, String numeroLote, String documentoReferencia);

    int levantar(Usuario usuario, Long id, String documentoLevantamiento);
}
