package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.AlertaSanitariaService;
import pe.edu.empresa.farmalerta.service.ProductoService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/alertas")
public class AlertaSanitariaController {

    private final AlertaSanitariaService service;

    private final ProductoService productoService;

    private final UsuarioService usuarioService;


    public AlertaSanitariaController(
            AlertaSanitariaService service,
            ProductoService productoService,
            UsuarioService usuarioService
    ) {
        this.service = service;
        this.productoService = productoService;
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
            model.addAttribute("alertas", service.listar(usuario));
            model.addAttribute("usuario", usuario);
            return "alertas/lista";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }
    }


    @GetMapping("/nueva")
    public String nueva(
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            usuarioService.validarRol(usuario, Rol.QUIMICO_FARMACEUTICO);

            model.addAttribute("productos", productoService.listar(usuario));

            return "alertas/formulario";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }
    }


    @PostMapping
    public String registrar(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) String numeroLote,
            @RequestParam(required = false) String documentoReferencia,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            int bloqueados = service.registrar(usuario, productoId, numeroLote, documentoReferencia);
            redirect.addFlashAttribute(
                "mensaje",
                "Alerta sanitaria registrada: " + bloqueados + " lotes bloqueados en las sucursales de la empresa"
            );
            return "redirect:/alertas";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/alertas/nueva";
        }
    }


    @PostMapping("/{id}/levantar")
    public String levantar(
            @PathVariable Long id,
            @RequestParam(required = false) String documentoLevantamiento,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            int liberados = service.levantar(usuario, id, documentoLevantamiento);
            redirect.addFlashAttribute("mensaje", "Alerta levantada: " + liberados + " lotes vuelven a estar disponibles");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/alertas";
    }
}
