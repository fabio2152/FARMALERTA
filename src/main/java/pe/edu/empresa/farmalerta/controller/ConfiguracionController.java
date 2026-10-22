package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.ConfiguracionEmpresa;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.EmpresaService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final EmpresaService empresaService;

    private final UsuarioService usuarioService;


    public ConfiguracionController(
            EmpresaService empresaService,
            UsuarioService usuarioService
    ) {
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String formulario(
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            usuarioService.validarRol(usuario, Rol.GERENTE);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }

        model.addAttribute(
            "configuracion",
            empresaService.obtenerConfiguracion(usuario.getEmpresa().getId())
        );

        return "configuracion/formulario";
    }


    @PostMapping
    public String guardar(
            @ModelAttribute ConfiguracionEmpresa configuracion,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            empresaService.actualizarConfiguracion(usuario, configuracion);
            redirect.addFlashAttribute("mensaje", "Configuración guardada");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/configuracion";
    }
}
