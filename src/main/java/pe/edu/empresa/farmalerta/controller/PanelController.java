package pe.edu.empresa.farmalerta.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.edu.empresa.farmalerta.model.EstadoLote;
import pe.edu.empresa.farmalerta.model.Lote;
import pe.edu.empresa.farmalerta.model.NivelAlerta;
import pe.edu.empresa.farmalerta.model.Notificacion;
import pe.edu.empresa.farmalerta.model.Rol;
import pe.edu.empresa.farmalerta.model.Usuario;
import pe.edu.empresa.farmalerta.service.AlertaService;
import pe.edu.empresa.farmalerta.service.FechaService;
import pe.edu.empresa.farmalerta.service.LoteService;
import pe.edu.empresa.farmalerta.service.NotificacionService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Controller
public class PanelController {

    private final UsuarioService usuarioService;

    private final LoteService loteService;

    private final AlertaService alertaService;

    private final FechaService fechaService;

    private final NotificacionService notificacionService;


    public PanelController(
            UsuarioService usuarioService,
            LoteService loteService,
            AlertaService alertaService,
            FechaService fechaService,
            NotificacionService notificacionService
    ) {
        this.usuarioService = usuarioService;
        this.loteService = loteService;
        this.alertaService = alertaService;
        this.fechaService = fechaService;
        this.notificacionService = notificacionService;
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

        List<Lote> lotes = loteService.lotesActivos(usuario);
        Map<Long, NivelAlerta> niveles = alertaService.niveles(lotes);

        Map<NivelAlerta, Integer> contadores = new LinkedHashMap<>();
        contadores.put(NivelAlerta.VERDE, 0);
        contadores.put(NivelAlerta.AMARILLO, 0);
        contadores.put(NivelAlerta.ROJO, 0);
        contadores.put(NivelAlerta.ULTIMO_DIA, 0);

        int bloqueados = 0;

        for (Lote lote : lotes) {

            NivelAlerta nivel = niveles.get(lote.getId());

            if (lote.getEstado() == EstadoLote.BLOQUEADO) {
                bloqueados++;
            } else if (contadores.containsKey(nivel)) {
                contadores.put(nivel, contadores.get(nivel) + 1);
            }
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("hoy", fechaService.hoy());
        model.addAttribute("contadores", contadores);
        model.addAttribute("bloqueados", bloqueados);
        model.addAttribute("lotes", lotes.size() > 15 ? lotes.subList(0, 15) : lotes);
        model.addAttribute("niveles", niveles);
        model.addAttribute("dias", alertaService.diasRestantes(lotes));
        model.addAttribute("sucursales", usuarioService.sucursalesVisibles(usuario));

        List<Notificacion> notificaciones = notificacionService.sinLeer(usuario);
        model.addAttribute("notificaciones", notificaciones.size() > 10 ? notificaciones.subList(0, 10) : notificaciones);

        return "panel";
    }
}
