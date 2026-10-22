package pe.edu.empresa.farmalerta.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import pe.edu.empresa.farmalerta.model.Usuario;


public interface ReporteService {

    Map<String, Object> reporteMensual(Usuario usuario, YearMonth mes);

    String actaBajasCsv(Usuario usuario, LocalDate desde, LocalDate hasta);
}
