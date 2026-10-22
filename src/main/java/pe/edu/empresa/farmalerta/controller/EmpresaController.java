package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.Empresa;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Sucursal;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.EmpresaService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService service;

    private final UsuarioService usuarioService;


    public EmpresaController(
            EmpresaService service,
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
            usuarioService.validarRol(usuario, Rol.ADMIN_PLATAFORMA);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
        }

        model.addAttribute("empresas", service.listarTodas());

        return "empresas/lista";
    }


    @GetMapping("/nueva")
    public String nueva(
            HttpSession session,
            Model model
    ) {

        if (usuarioService.obtenerUsuarioSesion(session) == null) {
            return "redirect:/login";
        }

        model.addAttribute("empresa", new Empresa());

        return "empresas/formulario";
    }


    @PostMapping
    public String guardar(
            @ModelAttribute Empresa empresa,
            @RequestParam String sucursalNombre,
            @RequestParam(required = false) String sucursalDireccion,
            @RequestParam(required = false) String sucursalDistrito,
            @RequestParam String gerenteUsuario,
            @RequestParam String gerenteNombre,
            @RequestParam(required = false) String gerenteCorreo,
            @RequestParam String gerenteContrasena,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Empresa guardada = service.registrar(
                usuario,
                empresa,
                new Sucursal(null, sucursalNombre, sucursalDireccion, sucursalDistrito),
                new Usuario(gerenteUsuario, gerenteNombre, gerenteCorreo, gerenteContrasena, Rol.GERENTE, null, null, true)
            );

            redirect.addFlashAttribute("mensaje", "Empresa registrada con su sucursal y su gerente");
            return "redirect:/empresas/" + guardada.getId();

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/empresas/nueva";
        }
    }


    @GetMapping("/{id}")
    public String detalle(
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
            usuarioService.validarRol(usuario, Rol.ADMIN_PLATAFORMA);

            model.addAttribute("empresa", service.obtenerPorId(id));
            model.addAttribute("sucursales", service.listarSucursales(id));

            return "empresas/detalle";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/empresas";
        }
    }


    @PostMapping("/{id}/sucursales")
    public String agregarSucursal(
            @PathVariable Long id,
            @ModelAttribute Sucursal sucursal,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.agregarSucursal(usuario, id, sucursal);
            redirect.addFlashAttribute("mensaje", "Sucursal agregada");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/empresas/" + id;
    }
}
