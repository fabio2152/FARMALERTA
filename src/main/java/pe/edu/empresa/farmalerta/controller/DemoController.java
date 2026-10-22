package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.service.FechaService;
import pe.edu.empresa.farmalerta.service.RevisionDiariaService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/demo")
public class DemoController {

    private final FechaService fechaService;

    private final RevisionDiariaService revisionDiariaService;

    private final UsuarioService usuarioService;


    public DemoController(
            FechaService fechaService,
            RevisionDiariaService revisionDiariaService,
            UsuarioService usuarioService
    ) {
        this.fechaService = fechaService;
        this.revisionDiariaService = revisionDiariaService;
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String panel(
            HttpSession session,
            Model model
    ) {

        if (usuarioService.obtenerUsuarioSesion(session) == null) {
            return "redirect:/login";
        }

        model.addAttribute("hoy", fechaService.hoy());
        model.addAttribute("desplazamiento", fechaService.getDesplazamiento());
        model.addAttribute("demo", fechaService.isDemo());

        return "demo/panel";
    }


    @PostMapping("/avanzar-dias")
    public String avanzarDias(
            @RequestParam(defaultValue = "1") int dias,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        if (usuarioService.obtenerUsuarioSesion(session) == null) {
            return "redirect:/login";
        }

        try {
            fechaService.avanzarDias(dias);
            redirect.addFlashAttribute("mensaje", "La fecha simulada avanzó " + dias + " día(s). Ejecute la revisión diaria.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/demo";
    }


    @PostMapping("/restablecer")
    public String restablecer(
            HttpSession session,
            RedirectAttributes redirect
    ) {

        if (usuarioService.obtenerUsuarioSesion(session) == null) {
            return "redirect:/login";
        }

        try {
            fechaService.restablecer();
            redirect.addFlashAttribute("mensaje", "La fecha volvió a la fecha real");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/demo";
    }


    @PostMapping("/revision-diaria")
    public String revisionDiaria(
            HttpSession session,
            RedirectAttributes redirect
    ) {

        if (usuarioService.obtenerUsuarioSesion(session) == null) {
            return "redirect:/login";
        }

        if (!fechaService.isDemo()) {
            redirect.addFlashAttribute("error", "La pantalla de demostración está desactivada");
            return "redirect:/demo";
        }

        redirect.addFlashAttribute("mensaje", revisionDiariaService.ejecutar());

        return "redirect:/demo";
    }
}
