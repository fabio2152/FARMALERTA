package pe.edu.empresa.farmalerta.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.AccionRecomendada;
import pe.edu.empresa.farmalerta.model.EstadoRecomendacion;
import pe.edu.empresa.farmalerta.model.Recomendacion;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.RecomendacionService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/recomendaciones")
public class RecomendacionController {

    private final RecomendacionService service;

    private final UsuarioService usuarioService;


    public RecomendacionController(
            RecomendacionService service,
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
            List<Recomendacion> todas = service.listar(usuario);

            model.addAttribute("pendientes", todas.stream()
                .filter(r -> r.getEstado() == EstadoRecomendacion.PENDIENTE && r.getAccion() != AccionRecomendada.MANTENER)
                .toList());
            model.addAttribute("mantener", todas.stream()
                .filter(r -> r.getEstado() == EstadoRecomendacion.PENDIENTE && r.getAccion() == AccionRecomendada.MANTENER)
                .toList());
            model.addAttribute("decididas", todas.stream()
                .filter(r -> r.getEstado() != EstadoRecomendacion.PENDIENTE)
                .toList());
            model.addAttribute("usuario", usuario);

            return "recomendaciones/lista";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }
    }


    @PostMapping("/generar")
    public String generar(
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            int generadas = service.generar(usuario);
            redirect.addFlashAttribute("mensaje", generadas + " recomendaciones actualizadas");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recomendaciones";
    }


    @PostMapping("/{id}/aceptar")
    public String aceptar(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Recomendacion recomendacion = service.aceptar(usuario, id);

            switch (recomendacion.getAccion()) {
                case TRANSFERIR -> {
                    redirect.addFlashAttribute("mensaje", "Recomendación aceptada: se creó la solicitud de transferencia");
                    return "redirect:/transferencias";
                }
                case DEVOLVER -> redirect.addFlashAttribute("mensaje", "Recomendación aceptada: se registró la devolución al proveedor");
                case BAJA -> {
                    redirect.addFlashAttribute("mensaje", "Recomendación aceptada: registre la baja del lote");
                    return "redirect:/lotes/" + recomendacion.getLote().getId();
                }
                default -> redirect.addFlashAttribute(
                    "mensaje",
                    "Recomendación aceptada: el descuento se aplica automáticamente al vender"
                );
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recomendaciones";
    }


    @PostMapping("/{id}/rechazar")
    public String rechazar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.rechazar(usuario, id, motivo);
            redirect.addFlashAttribute("mensaje", "Recomendación rechazada");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recomendaciones";
    }
}
