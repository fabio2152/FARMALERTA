package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.EstadoSalida;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Salida;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.ProductoService;
import pe.edu.empresa.farmalerta.service.UsuarioService;
import pe.edu.empresa.farmalerta.service.VentaService;


@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService service;

    private final ProductoService productoService;

    private final UsuarioService usuarioService;


    public VentaController(
            VentaService service,
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
            model.addAttribute("salidas", service.listar(usuario));
            return "ventas/lista";

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
            usuarioService.validarRol(usuario, Rol.TECNICO, Rol.QUIMICO_FARMACEUTICO);

            model.addAttribute("productos", productoService.listar(usuario));

            return "ventas/formulario";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas";
        }
    }


    @PostMapping
    public String registrar(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Integer cantidad,
            @RequestParam(required = false) Integer diasTratamiento,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Salida salida = service.registrar(usuario, productoId, cantidad, diasTratamiento);

            if (salida.getEstado() == EstadoSalida.PENDIENTE_CONFIRMACION) {
                redirect.addFlashAttribute("mensaje", "Venta pendiente: un químico farmacéutico debe confirmarla");
            } else {
                redirect.addFlashAttribute("mensaje", "Venta registrada");
            }

            return "redirect:/ventas/" + salida.getId();

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }


    @GetMapping("/pendientes")
    public String pendientes(
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("salidas", service.pendientes(usuario));
            return "ventas/pendientes";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas";
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
            model.addAttribute("salida", service.obtenerPorId(usuario, id));
            model.addAttribute("movimientos", service.movimientos(usuario, id));
            model.addAttribute("usuario", usuario);

            return "ventas/detalle";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas";
        }
    }


    @PostMapping("/{id}/confirmar")
    public String confirmar(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.confirmar(usuario, id);
            redirect.addFlashAttribute("mensaje", "Venta confirmada");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/ventas/" + id;
    }


    @PostMapping("/{id}/rechazar")
    public String rechazar(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.rechazar(usuario, id);
            redirect.addFlashAttribute("mensaje", "Venta rechazada; el stock no cambió");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/ventas/" + id;
    }
}
