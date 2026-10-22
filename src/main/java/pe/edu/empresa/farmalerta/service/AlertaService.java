package pe.edu.empresa.farmalerta.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.NivelAlerta;


public interface AlertaService {

    NivelAlerta calcularNivel(Lote lote);

    long diasRestantes(Lote lote);

    LocalDate fechaFinDeMes(YearMonth mes);

    Map<Long, NivelAlerta> niveles(List<Lote> lotes);

    Map<Long, Long> diasRestantes(List<Lote> lotes);
}
