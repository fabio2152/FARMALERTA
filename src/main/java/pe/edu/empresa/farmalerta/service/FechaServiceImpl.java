package pe.edu.empresa.farmalerta.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class FechaServiceImpl
        implements FechaService {

    private final ZoneId zona;

    private final boolean demo;

    private int desplazamiento = 0;


    public FechaServiceImpl(
            @Value("${farmalerta.zona-horaria}") String zonaHoraria,
            @Value("${farmalerta.demo}") boolean demo
    ) {
        this.zona = ZoneId.of(zonaHoraria);
        this.demo = demo;
    }


    @Override
    public LocalDate hoy() {

        return LocalDate.now(zona).plusDays(desplazamiento);
    }


    @Override
    public LocalDateTime ahora() {

        return LocalDateTime.now(zona).plusDays(desplazamiento);
    }


    @Override
    public void avanzarDias(int dias) {

        validarDemo();

        if (dias < 1 || dias > 365) {
            throw new IllegalArgumentException(
                "Solo se puede avanzar entre 1 y 365 días"
            );
        }

        desplazamiento += dias;
    }


    @Override
    public void restablecer() {

        validarDemo();

        desplazamiento = 0;
    }


    @Override
    public int getDesplazamiento() {

        return desplazamiento;
    }


    @Override
    public boolean isDemo() {

        return demo;
    }


    private void validarDemo() {

        if (!demo) {
            throw new IllegalStateException(
                "La simulación de fecha solo funciona en modo demostración"
            );
        }
    }
}
