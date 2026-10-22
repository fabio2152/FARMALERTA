package pe.edu.empresa.farmalerta.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.FechaService;
import pe.edu.empresa.farmalerta.service.ReporteService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService service;

    private final UsuarioService usuarioService;

    private final FechaService fechaService;


    public ReporteController(
            ReporteService service,
            UsuarioService usuarioService,
            FechaService fechaService
    ) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.fechaService = fechaService;
    }


    @GetMapping
    public String mensual(
            @RequestParam(required = false) String mes,
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            YearMonth periodo = mes == null || mes.isBlank()
                ? YearMonth.from(fechaService.hoy())
                : YearMonth.parse(mes);

            model.addAttribute("reporte", service.reporteMensual(usuario, periodo));
            model.addAttribute("mes", periodo.toString());
            model.addAttribute("desde", periodo.atDay(1));
            model.addAttribute("hasta", periodo.atEndOfMonth());
            model.addAttribute("usuario", usuario);

            return "reportes/mensual";

        } catch (DateTimeParseException e) {
            redirect.addFlashAttribute("error", "El mes no es válido");
            return "redirect:/reportes";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }
    }


    @GetMapping(value = "/acta-bajas.csv", produces = "text/csv; charset=UTF-8")
    @ResponseBody
    public String actaBajas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            HttpSession session,
            HttpServletResponse response
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            throw new IllegalStateException("Debe iniciar sesión");
        }

        String csv = service.actaBajasCsv(usuario, desde, hasta);

        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"acta-bajas-" + desde + "-a-" + hasta + ".csv\""
        );

        return csv;
    }
}
