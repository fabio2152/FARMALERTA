package pe.edu.empresa.farmalerta.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pe.edu.empresa.farmalerta.model.ConfiguracionEmpresa;
import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.NivelAlerta;
import pe.edu.empresa.farmalerta.model.Producto;


@Service
public class AlertaServiceImpl
        implements AlertaService {

    private final EmpresaService empresaService;

    private final FechaService fechaService;


    public AlertaServiceImpl(
            EmpresaService empresaService,
            FechaService fechaService
    ) {
        this.empresaService = empresaService;
        this.fechaService = fechaService;
    }


    @Override
    public NivelAlerta calcularNivel(Lote lote) {

        ConfiguracionEmpresa configuracion = empresaService.obtenerConfiguracion(
            lote.getProducto().getEmpresa().getId()
        );

        return calcularNivel(lote, configuracion);
    }


    @Override
    public long diasRestantes(Lote lote) {

        return ChronoUnit.DAYS.between(fechaService.hoy(), lote.getFechaVencimiento());
    }


    @Override
    public LocalDate fechaFinDeMes(YearMonth mes) {

        return mes.atEndOfMonth();
    }


    @Override
    public Map<Long, NivelAlerta> niveles(List<Lote> lotes) {

        Map<Long, ConfiguracionEmpresa> configuraciones = new HashMap<>();
        Map<Long, NivelAlerta> niveles = new HashMap<>();

        for (Lote lote : lotes) {

            Long empresaId = lote.getProducto().getEmpresa().getId();

            ConfiguracionEmpresa configuracion = configuraciones.computeIfAbsent(
                empresaId,
                empresaService::obtenerConfiguracion
            );

            niveles.put(lote.getId(), calcularNivel(lote, configuracion));
        }

        return niveles;
    }


    @Override
    public Map<Long, Long> diasRestantes(List<Lote> lotes) {

        Map<Long, Long> dias = new HashMap<>();

        for (Lote lote : lotes) {
            dias.put(lote.getId(), diasRestantes(lote));
        }

        return dias;
    }


    private NivelAlerta calcularNivel(Lote lote, ConfiguracionEmpresa configuracion) {

        long dias = diasRestantes(lote);

        if (dias < 0) {
            return NivelAlerta.VENCIDO;
        }

        if (dias == 0) {
            return NivelAlerta.ULTIMO_DIA;
        }

        if (dias <= umbralRojo(lote.getProducto(), configuracion)) {
            return NivelAlerta.ROJO;
        }

        if (dias <= umbralAmarillo(lote.getProducto(), configuracion)) {
            return NivelAlerta.AMARILLO;
        }

        return NivelAlerta.VERDE;
    }


    private int umbralAmarillo(Producto producto, ConfiguracionEmpresa configuracion) {

        if (producto.getUmbralAmarilloPropio() != null && producto.getUmbralRojoPropio() != null) {
            return producto.getUmbralAmarilloPropio();
        }

        return configuracion.getUmbralAmarillo();
    }


    private int umbralRojo(Producto producto, ConfiguracionEmpresa configuracion) {

        if (producto.getUmbralAmarilloPropio() != null && producto.getUmbralRojoPropio() != null) {
            return producto.getUmbralRojoPropio();
        }

        return configuracion.getUmbralRojo();
    }
}
