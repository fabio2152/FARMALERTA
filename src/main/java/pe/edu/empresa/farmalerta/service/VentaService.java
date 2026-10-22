package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.util.List;

import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.Movimiento;
import pe.edu.empresa.farmalerta.model.Salida;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface VentaService {

    Salida registrar(Usuario usuario, Long productoId, Integer cantidad, Integer diasTratamiento);

    List<Salida> listar(Usuario usuario);

    Salida obtenerPorId(Usuario usuario, Long id);

    List<Movimiento> movimientos(Usuario usuario, Long salidaId);

    List<Salida> pendientes(Usuario usuario);

    Salida confirmar(Usuario usuario, Long id);

    Salida rechazar(Usuario usuario, Long id);

    BigDecimal descuentoDeNivel(Lote lote);

    BigDecimal precioFinal(Lote lote);

    List<Lote> ofertas(Long empresaId);
}
