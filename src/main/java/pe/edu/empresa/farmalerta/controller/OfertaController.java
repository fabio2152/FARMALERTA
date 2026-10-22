package pe.edu.empresa.farmalerta.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.service.AlertaService;
import pe.edu.empresa.farmalerta.service.EmpresaService;
import pe.edu.empresa.farmalerta.service.VentaService;


@Controller
public class OfertaController {

    private final VentaService ventaService;

    private final EmpresaService empresaService;

    private final AlertaService alertaService;


    public OfertaController(
            VentaService ventaService,
            EmpresaService empresaService,
            AlertaService alertaService
    ) {
        this.ventaService = ventaService;
        this.empresaService = empresaService;
        this.alertaService = alertaService;
    }


    @GetMapping("/ofertas")
    public String ofertas(
            @RequestParam(required = false) Long empresa,
            Model model
    ) {

        model.addAttribute("empresas", empresaService.listarTodas());
        model.addAttribute("empresaSeleccionada", empresa);

        if (empresa == null) {
            return "ofertas/lista";
        }

        List<Lote> lotes = ventaService.ofertas(empresa);
        Map<Long, BigDecimal> precios = new HashMap<>();
        Map<Long, BigDecimal> descuentos = new HashMap<>();

        for (Lote lote : lotes) {
            precios.put(lote.getId(), ventaService.precioFinal(lote));
            descuentos.put(lote.getId(), ventaService.descuentoDeNivel(lote));
        }

        model.addAttribute("lotes", lotes);
        model.addAttribute("precios", precios);
        model.addAttribute("descuentos", descuentos);
        model.addAttribute("niveles", alertaService.niveles(lotes));

        return "ofertas/lista";
    }
}
