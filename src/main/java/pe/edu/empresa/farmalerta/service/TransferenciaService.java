package pe.edu.empresa.farmalerta.service;

import java.util.List;

import pe.edu.empresa.farmalerta.model.Sucursal;
import pe.edu.empresa.farmalerta.model.Transferencia;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface TransferenciaService {

    List<Transferencia> listar(Usuario usuario);

    List<Sucursal> destinosPosibles(Usuario usuario);

    Transferencia solicitar(Usuario usuario, Long loteId, Long sucursalDestinoId, Integer cantidad);

    Transferencia recibir(Usuario usuario, Long id);

    Transferencia cancelar(Usuario usuario, Long id);
}
