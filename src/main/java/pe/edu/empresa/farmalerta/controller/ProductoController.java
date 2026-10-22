package pe.edu.empresa.farmalerta.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.service.ProductoService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    private final UsuarioService usuarioService;


    public ProductoController(
            ProductoService service,
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
            model.addAttribute("productos", service.listar(usuario));
            return "productos/lista";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/panel";
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
            model.addAttribute("producto", service.obtenerPorId(usuario, id));
            return "productos/detalle";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos";
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

        Producto producto = new Producto();
        producto.setUnidadesPorEmpaque(1);

        model.addAttribute("producto", producto);
        agregarOpciones(model);

        return "productos/formulario";
    }


    @PostMapping
    public String guardar(
            @ModelAttribute Producto producto,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.guardar(usuario, producto);
            redirect.addFlashAttribute("mensaje", "Producto registrado");
            return "redirect:/productos";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos/nuevo";
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
            model.addAttribute("producto", service.obtenerPorId(usuario, id));
            agregarOpciones(model);
            return "productos/formulario";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos";
        }
    }


    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable Long id,
            @ModelAttribute Producto producto,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            service.actualizar(usuario, id, producto);
            redirect.addFlashAttribute("mensaje", "Producto actualizado");
            return "redirect:/productos/" + id;

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos/" + id + "/editar";
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
            redirect.addFlashAttribute("mensaje", "Producto eliminado");

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/productos";
    }


    private void agregarOpciones(Model model) {

        model.addAttribute("formas", FormaFarmaceutica.values());
        model.addAttribute("categorias", CategoriaRegulatoria.values());
        model.addAttribute("condiciones", CondicionVenta.values());
        model.addAttribute("conservaciones", Conservacion.values());
    }
}
