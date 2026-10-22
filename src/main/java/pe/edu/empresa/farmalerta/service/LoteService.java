package pe.edu.empresa.farmalerta.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import pe.edu.empresa.farmalerta.model.EstadoLote;
import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.Movimiento;
import pe.edu.empresa.farmalerta.model.NivelAlerta;
import pe.edu.empresa.farmalerta.model.Usuario;


public interface LoteService {

    List<Lote> listar(Usuario usuario, NivelAlerta nivel, EstadoLote estado);

    List<Lote> lotesActivos(Usuario usuario);

    Lote obtenerPorId(Usuario usuario, Long id);

    List<Movimiento> movimientos(Usuario usuario, Long loteId);

    Lote ingresar(
        Usuario usuario,
        Long productoId,
        Long proveedorId,
        String numeroLote,
        LocalDate fechaVencimiento,
        String mesAnio,
        Integer cantidad,
        boolean enEmpaques,
        BigDecimal costoUnitario
    );
}
