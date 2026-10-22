package pe.edu.empresa.farmalerta.service;

import pe.edu.empresa.farmalerta.model.MotivoBaja;
import pe.edu.empresa.farmalerta.model.Movimiento;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface BajaService {

    Movimiento darDeBaja(Usuario usuario, Long loteId, Integer cantidad, MotivoBaja motivo, String observacion);
}
