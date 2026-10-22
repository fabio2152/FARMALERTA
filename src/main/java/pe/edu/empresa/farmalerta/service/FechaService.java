package pe.edu.empresa.farmalerta.service;

import java.time.LocalDate;
import java.time.LocalDateTime;


public interface FechaService {

    LocalDate hoy();

    LocalDateTime ahora();

    void avanzarDias(int dias);

    void restablecer();

    int getDesplazamiento();

    boolean isDemo();
}
