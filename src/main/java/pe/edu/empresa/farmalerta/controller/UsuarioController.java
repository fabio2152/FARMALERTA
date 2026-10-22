package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;


    public UsuarioController(
            UsuarioService service
    ) {
        this.service = service;
    }


    @GetMapping
    public String listar(
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = service.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("usuarios", service.listar(usuario));
            return "usuarios/lista";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }
    }


    @GetMapping("/nuevo")
    public String nuevo(
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = service.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.validarRol(usuario, Rol.GERENTE);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }

        model.addAttribute("nuevo", new Usuario());
        model.addAttribute("roles", new Rol[] { Rol.QUIMICO_FARMACEUTICO, Rol.TECNICO, Rol.GERENTE });
        model.addAttribute("sucursales", service.sucursalesVisibles(usuario));

        return "usuarios/formulario";
    }


    @PostMapping
    public String guardar(
            @ModelAttribute("nuevo") Usuario nuevo,
            @RequestParam(required = false) Long sucursalId,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = service.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.registrar(usuario, nuevo, sucursalId);
            redirect.addFlashAttribute("mensaje", "Usuario registrado");
            return "redirect:/usuarios";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/nuevo";
        }
    }


    @PostMapping("/{id}/desactivar")
    public String desactivar(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = service.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.desactivar(usuario, id);
            redirect.addFlashAttribute("mensaje", "Usuario desactivado");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/usuarios";
    }
}
