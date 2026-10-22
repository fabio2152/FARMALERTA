package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.NotificacionService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    private final UsuarioService usuarioService;


    public NotificacionController(
            NotificacionService service,
            UsuarioService usuarioService
    ) {
        this.service = service;
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String listar(
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("notificaciones", service.listar(usuario));
            return "notificaciones/lista";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }
    }


    @PostMapping("/{id}/leida")
    public String marcarLeida(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.marcarLeida(usuario, id);

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/notificaciones";
    }
}
