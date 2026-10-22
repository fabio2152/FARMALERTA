package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
public class LoginController {

    private final UsuarioService usuarioService;


    public LoginController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }


    @GetMapping("/login")
    public String login(
            HttpSession session
    ) {

        if (usuarioService.obtenerUsuarioSesion(session) != null) {
            return "redirect:/panel";
        }

        return "login";
    }


    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String nombreUsuario,
            @RequestParam String contrasena,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        try {
            Usuario usuario = usuarioService.iniciarSesion(nombreUsuario, contrasena);

            session.setAttribute("usuario", usuario);

            if (usuario.getRol() == Rol.ADMIN_PLATAFORMA) {
                return "redirect:/empresas";
            }

            return "redirect:/panel";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }


    @PostMapping("/logout")
    public String cerrarSesion(
            HttpSession session
    ) {

        session.invalidate();

        return "redirect:/";
    }
}
