package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.AlertaService;
import pe.edu.empresa.farmalerta.service.LoteService;
import pe.edu.empresa.farmalerta.service.TransferenciaService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/transferencias")
public class TransferenciaController {

    private final TransferenciaService service;

    private final LoteService loteService;

    private final AlertaService alertaService;

    private final UsuarioService usuarioService;


    public TransferenciaController(
            TransferenciaService service,
            LoteService loteService,
            AlertaService alertaService,
            UsuarioService usuarioService
    ) {
        this.service = service;
        this.loteService = loteService;
        this.alertaService = alertaService;
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
            model.addAttribute("transferencias", service.listar(usuario));
            model.addAttribute("usuario", usuario);
            return "transferencias/lista";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }
    }


    @GetMapping("/nueva")
    public String nueva(
            @RequestParam(required = false) Long loteId,
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

            Lote lote = loteService.obtenerPorId(usuario, loteId);

            model.addAttribute("lote", lote);
            model.addAttribute("diasRestantes", alertaService.diasRestantes(lote));
            model.addAttribute("destinos", service.destinosPosibles(usuario));

            return "transferencias/formulario";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/transferencias";
        }
    }


    @PostMapping
    public String solicitar(
            @RequestParam(required = false) Long loteId,
            @RequestParam(required = false) Long sucursalDestinoId,
            @RequestParam(required = false) Integer cantidad,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.solicitar(usuario, loteId, sucursalDestinoId, cantidad);
            redirect.addFlashAttribute("mensaje", "Transferencia solicitada: las unidades quedan reservadas");
            return "redirect:/transferencias";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/transferencias/nueva?loteId=" + loteId;
        }
    }


    @PostMapping("/{id}/recibir")
    public String recibir(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.recibir(usuario, id);
            redirect.addFlashAttribute("mensaje", "Transferencia recibida: el lote ya está en esta sucursal");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/transferencias";
    }


    @PostMapping("/{id}/cancelar")
    public String cancelar(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.cancelar(usuario, id);
            redirect.addFlashAttribute("mensaje", "Transferencia cancelada: las unidades vuelven a estar disponibles");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/transferencias";
    }
}
