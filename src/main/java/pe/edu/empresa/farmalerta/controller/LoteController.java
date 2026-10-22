package pe.edu.empresa.farmalerta.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.service.AlertaService;
import pe.edu.empresa.farmalerta.service.LoteService;
import pe.edu.empresa.farmalerta.service.ProductoService;
import pe.edu.empresa.farmalerta.service.ProveedorService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
@RequestMapping("/lotes")
public class LoteController {

    private final LoteService service;

    private final AlertaService alertaService;

    private final ProductoService productoService;

    private final ProveedorService proveedorService;

    private final UsuarioService usuarioService;


    public LoteController(
            LoteService service,
            AlertaService alertaService,
            ProductoService productoService,
            ProveedorService proveedorService,
            UsuarioService usuarioService
    ) {
        this.service = service;
        this.alertaService = alertaService;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
        this.usuarioService = usuarioService;
    }


    @GetMapping
    public String listar(
            @RequestParam(required = false) NivelAlerta nivel,
            @RequestParam(required = false) EstadoLote estado,
            HttpSession session,
            Model model,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            List<Lote> lotes = service.listar(usuario, nivel, estado);

            model.addAttribute("lotes", lotes);
            model.addAttribute("niveles", alertaService.niveles(lotes));
            model.addAttribute("dias", alertaService.diasRestantes(lotes));
            model.addAttribute("nivelSeleccionado", nivel);
            model.addAttribute("estadoSeleccionado", estado);
            model.addAttribute("opcionesNivel", NivelAlerta.values());
            model.addAttribute("opcionesEstado", EstadoLote.values());

            return "lotes/lista";

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

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            usuarioService.validarRol(usuario, Rol.TECNICO, Rol.QUIMICO_FARMACEUTICO);

            model.addAttribute("productos", productoService.listar(usuario));
            model.addAttribute("proveedores", proveedorService.listar(usuario));

            return "lotes/formulario";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/lotes";
        }
    }


    @PostMapping
    public String ingresar(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) String numeroLote,
            @RequestParam(defaultValue = "completa") String tipoFecha,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento,
            @RequestParam(required = false) String mesAnio,
            @RequestParam(required = false) Integer cantidad,
            @RequestParam(defaultValue = "unidades") String unidadCantidad,
            @RequestParam(required = false) BigDecimal costoUnitario,
            HttpSession session,
            RedirectAttributes redirect
    ) {

        Usuario usuario = usuarioService.obtenerUsuarioSesion(session);

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Lote lote = service.ingresar(
                usuario,
                productoId,
                proveedorId,
                numeroLote,
                "mes".equals(tipoFecha) ? null : fechaVencimiento,
                "mes".equals(tipoFecha) ? mesAnio : null,
                cantidad,
                "empaques".equals(unidadCantidad),
                costoUnitario
            );

            redirect.addFlashAttribute("mensaje", "Lote ingresado correctamente");
            return "redirect:/lotes/" + lote.getId();

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/lotes/nuevo";
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
            Lote lote = service.obtenerPorId(usuario, id);

            model.addAttribute("lote", lote);
            model.addAttribute("nivel", alertaService.calcularNivel(lote));
            model.addAttribute("diasRestantes", alertaService.diasRestantes(lote));
            model.addAttribute("movimientos", service.movimientos(usuario, id));
            model.addAttribute("usuario", usuario);

            return "lotes/detalle";

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/lotes";
        }
    }
}
