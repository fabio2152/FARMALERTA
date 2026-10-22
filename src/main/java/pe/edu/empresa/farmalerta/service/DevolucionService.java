package pe.edu.empresa.farmalerta.service;

import pe.edu.empresa.farmalerta.model.Movimiento;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface DevolucionService {

    Movimiento devolver(Usuario usuario, Long loteId, Integer cantidad, Long proveedorId);
}
