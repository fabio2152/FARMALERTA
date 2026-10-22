package pe.edu.empresa.farmalerta.controller;

import java.math.BigDecimal;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.Proveedor;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.ProveedorService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService service;

    private final UsuarioService usuarioService;


    public ProveedorController(
            ProveedorService service,
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
            model.addAttribute("proveedores", service.listar(usuario));
            return "proveedores/lista";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }
    }


    @GetMapping("/nuevo")
    public String nuevo(
            HttpSession session,
            Model model
    ) {

        if (usuarioService.obtenerUsuarioSesion(session) == null) {
            return "redirect:/login";
        }

        model.addAttribute(
            "proveedor",
            new Proveedor(null, null, null, true, 90, new BigDecimal("100.00"))
        );

        return "proveedores/formulario";
    }


    @PostMapping
    public String guardar(
            @ModelAttribute Proveedor proveedor,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.guardar(usuario, proveedor);
            redirect.addFlashAttribute("mensaje", "Proveedor registrado");
            return "redirect:/proveedores";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/proveedores/nuevo";
        }
    }


    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("proveedor", service.obtenerPorId(usuario, id));
            return "proveedores/formulario";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/proveedores";
        }
    }


    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @ModelAttribute Proveedor proveedor,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.actualizar(usuario, id, proveedor);
            redirect.addFlashAttribute("mensaje", "Proveedor actualizado");
            return "redirect:/proveedores";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/proveedores/" + id + "/editar";
        }
    }


    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.eliminar(usuario, id);
            redirect.addFlashAttribute("mensaje", "Proveedor eliminado");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/proveedores";
    }
}
