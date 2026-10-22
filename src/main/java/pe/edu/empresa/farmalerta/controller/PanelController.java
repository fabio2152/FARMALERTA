package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
public class PanelController {

    private final UsuarioService usuarioService;


    public PanelController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }


    @GetMapping("/panel")
    public String panel(
            HttpSession session,
            Model model
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        if (usuario.getRol() == Rol.ADMIN_PLATAFORMA) {
            return "redirect:/empresas";
        }

        model.addAttribute("usuario", usuario);

        return "panel";
    }
}
