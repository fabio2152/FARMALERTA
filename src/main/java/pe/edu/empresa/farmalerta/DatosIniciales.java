package pe.edu.empresa.farmalerta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import pe.edu.empresa.farmalerta.model.*;
import pe.edu.empresa.farmalerta.repository.LoteRepository;
import pe.edu.empresa.farmalerta.repository.MovimientoRepository;
import pe.edu.empresa.farmalerta.repository.ProductoRepository;
import pe.edu.empresa.farmalerta.repository.ProveedorRepository;
import pe.edu.empresa.farmalerta.repository.UsuarioRepository;
import pe.edu.empresa.farmalerta.service.EmpresaService;
import pe.edu.empresa.farmalerta.service.FechaService;
import pe.edu.empresa.farmalerta.service.RevisionDiariaService;
import pe.edu.empresa.farmalerta.service.UsuarioService;


@Component
public class DatosIniciales
        implements CommandLineRunner {

    private static final String CLAVE = "clave123";

    private final UsuarioRepository usuarioRepository;

    private final ProductoRepository productoRepository;

    private final ProveedorRepository proveedorRepository;

    private final LoteRepository loteRepository;

    private final MovimientoRepository movimientoRepository;

    private final UsuarioService usuarioService;

    private final EmpresaService empresaService;

    private final FechaService fechaService;

    private final RevisionDiariaService revisionDiariaService;

    private final Random aleatorio = new Random(42);

    private LocalDate hoy;


    public DatosIniciales(
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            ProveedorRepository proveedorRepository,
            LoteRepository loteRepository,
            MovimientoRepository movimientoRepository,
            UsuarioService usuarioService,
            EmpresaService empresaService,
            FechaService fechaService,
            RevisionDiariaService revisionDiariaService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.fechaService = fechaService;
        this.revisionDiariaService = revisionDiariaService;
    }


    @Override
    public void run(String... args) {

        if (usuarioRepository.count() > 0) {
            return;
        }

        hoy = fechaService.hoy();

        Usuario admin = usuarioService.crearCuenta(
            new Usuario("admin", "Administrador de la plataforma", "admin@farmalerta.pe", CLAVE, Rol.ADMIN_PLATAFORMA, null, null, true)
        );

        cargarCadena(admin);
        cargarIndependiente(admin);

        revisionDiariaService.ejecutar();
    }


    private void cargarCadena(Usuario admin) {

        Empresa cadena = empresaService.registrar(
            admin,
            new Empresa("Boticas Salud Total S.A.C.", "20601234567"),
            new Sucursal(null, "Miraflores", "Av. José Larco 345", "Miraflores"),
            new Usuario("gerente1", "Carla Mendoza (Gerente)", "gerente@saludtotal.pe", CLAVE, null, null, null, true)
        );

        Sucursal miraflores = empresaService.listarSucursales(cadena.getId()).get(0);

        Sucursal sjl = empresaService.agregarSucursal(
            admin,
            cadena.getId(),
            new Sucursal(null, "San Juan de Lurigancho", "Av. Próceres de la Independencia 1520", "San Juan de Lurigancho")
        );

        crearUsuario("qf_miraflores", "Q.F. Luis Paredes", Rol.QUIMICO_FARMACEUTICO, cadena, miraflores);
        Usuario tecMir = crearUsuario("tecnico_miraflores", "Téc. Ana Quispe", Rol.TECNICO, cadena, miraflores);
        crearUsuario("qf_sjl", "Q.F. Rosa Huamán", Rol.QUIMICO_FARMACEUTICO, cadena, sjl);
        Usuario tecSjl = crearUsuario("tecnico_sjl", "Téc. Jorge Ramos", Rol.TECNICO, cadena, sjl);

        Proveedor andina = proveedorRepository.save(
            new Proveedor(cadena, "Droguería Andina S.A.", "20512345678", true, 90, new BigDecimal("100.00"))
        );
        Proveedor lima = proveedorRepository.save(
            new Proveedor(cadena, "Distribuidora Lima S.A.C.", "20598765432", false, 90, new BigDecimal("100.00"))
        );

        Producto paracetamol = crearParacetamol(cadena);
        Producto amoxicilina = crearAmoxicilina(cadena);
        Producto ibuprofeno = crearProducto(cadena, "IBU-100", "Ibuprofeno Kids", "Ibuprofeno", "100 mg/5 mL",
            FormaFarmaceutica.SUSPENSION_ORAL, CategoriaRegulatoria.MEDICAMENTO, CondicionVenta.SIN_RECETA,
            Conservacion.AMBIENTE, "Frasco x 60 mL", 1, "Laboratorios Unidos", "EN-04512", "12.50");
        Producto amoxPolvo = crearProducto(cadena, "AMX-250", "Amoxicilina Suspensión", "Amoxicilina", "250 mg/5 mL",
            FormaFarmaceutica.POLVO_PARA_RECONSTITUIR, CategoriaRegulatoria.MEDICAMENTO, CondicionVenta.CON_RECETA,
            Conservacion.AMBIENTE, "Frasco x 60 mL", 1, "Medifarma", "EN-03321", "14.50");
        Producto insulina = crearProducto(cadena, "INS-NPH", "Insulina Humana NPH", "Insulina isófana", "100 UI/mL",
            FormaFarmaceutica.INYECTABLE, CategoriaRegulatoria.PRODUCTO_BIOLOGICO, CondicionVenta.CON_RECETA,
            Conservacion.REFRIGERADO, "Vial x 10 mL", 1, "Novo Nordisk", "BE-00871", "62.00");
        Producto sales = crearProducto(cadena, "SRO-01", "Sales de Rehidratación Oral", "Cloruro de sodio, glucosa", "20,5 g",
            FormaFarmaceutica.SOLUCION_ORAL, CategoriaRegulatoria.MEDICAMENTO, CondicionVenta.SIN_RECETA,
            Conservacion.AMBIENTE, "Caja x 20 sobres", 20, "Farmindustria", "EN-01177", "2.50");
        Producto formula = crearProducto(cadena, "FOR-1", "Fórmula Infantil Etapa 1", "Leche de inicio", "",
            FormaFarmaceutica.OTRA, CategoriaRegulatoria.PRODUCTO_DIETETICO, CondicionVenta.SIN_RECETA,
            Conservacion.AMBIENTE, "Lata x 400 g", 1, "Nutricia", "PD-00456", "78.00");
        Producto tiras = crearProducto(cadena, "TIR-GLU", "Tiras Reactivas de Glucosa", "Glucosa oxidasa", "",
            FormaFarmaceutica.OTRA, CategoriaRegulatoria.DISPOSITIVO_MEDICO, CondicionVenta.SIN_RECETA,
            Conservacion.AMBIENTE, "Caja x 50 tiras", 50, "Accu-Check", "DM-07788", "3.00");
        Producto bloqueador = crearProducto(cadena, "BLQ-50", "Bloqueador Solar", "Óxido de zinc", "FPS 50",
            FormaFarmaceutica.SEMISOLIDO_TOPICO, CategoriaRegulatoria.PRODUCTO_SANITARIO, CondicionVenta.SIN_RECETA,
            Conservacion.AMBIENTE, "Frasco x 120 mL", 1, "Isdin", "PS-11223", "55.00");

        crearLote(paracetamol, miraflores, andina, "P-101", 20, 30, "0.20", tecMir);
        crearLote(paracetamol, miraflores, andina, "P-102", 150, 200, "0.20", tecMir);
        crearLote(amoxicilina, miraflores, andina, "A-201", 3, 12, "0.45", tecMir);
        crearLote(amoxicilina, miraflores, lima, "A-202", 60, 100, "0.45", tecMir);
        crearLote(ibuprofeno, miraflores, andina, "I-501", 40, 120, "6.00", tecMir);
        crearLote(amoxPolvo, miraflores, andina, "X-901", 130, 30, "6.50", tecMir);
        crearLote(insulina, miraflores, andina, "N-601", 75, 20, "35.00", tecMir);
        crearLote(sales, miraflores, lima, "S-301", 0, 10, "1.20", tecMir);
        crearLote(sales, miraflores, lima, "S-302", 45, 150, "1.20", tecMir);
        crearLote(formula, miraflores, lima, "F-401", -2, 12, "45.00", tecMir);
        crearLote(tiras, miraflores, lima, "T-801", 25, 40, "1.60", tecMir);
        crearLote(bloqueador, miraflores, andina, "B-701", 100, 200, "28.00", tecMir);

        crearLote(paracetamol, sjl, andina, "P-111", 200, 300, "0.20", tecSjl);
        crearLote(amoxicilina, sjl, andina, "A-211", 85, 50, "0.45", tecSjl);
        crearLote(ibuprofeno, sjl, andina, "I-511", 200, 20, "6.00", tecSjl);
        crearLote(insulina, sjl, andina, "N-601", 75, 15, "35.00", tecSjl);
        crearLote(sales, sjl, lima, "S-311", 180, 150, "1.20", tecSjl);
        crearLote(formula, sjl, lima, "F-411", 10, 8, "45.00", tecSjl);

        crearHistorial(paracetamol, miraflores, andina, tecMir, 8);
        crearHistorial(amoxicilina, miraflores, andina, tecMir, 3);
        crearHistorial(ibuprofeno, miraflores, andina, tecMir, 2);
        crearHistorial(insulina, miraflores, andina, tecMir, 1);
        crearHistorial(sales, miraflores, lima, tecMir, 2);
        crearHistorial(bloqueador, miraflores, andina, tecMir, 1);

        crearHistorial(paracetamol, sjl, andina, tecSjl, 10);
        crearHistorial(amoxicilina, sjl, andina, tecSjl, 4);
        crearHistorial(ibuprofeno, sjl, andina, tecSjl, 6);
        crearHistorial(sales, sjl, lima, tecSjl, 3);
    }


    private void cargarIndependiente(Usuario admin) {

        Empresa esperanza = empresaService.registrar(
            admin,
            new Empresa("Botica La Esperanza E.I.R.L.", "20487654321"),
            new Sucursal(null, "La Esperanza", "Jr. Los Olivos 120", "Comas"),
            new Usuario("gerente_esperanza", "Pedro Salazar (Gerente)", "gerente@laesperanza.pe", CLAVE, null, null, null, true)
        );

        Sucursal sucursal = empresaService.listarSucursales(esperanza.getId()).get(0);

        crearUsuario("qf_esperanza", "Q.F. Elena Torres", Rol.QUIMICO_FARMACEUTICO, esperanza, sucursal);
        Usuario tecnico = crearUsuario("tecnico_esperanza", "Téc. Miguel Flores", Rol.TECNICO, esperanza, sucursal);

        Proveedor andina = proveedorRepository.save(
            new Proveedor(esperanza, "Droguería Andina S.A.", "20512345678", true, 90, new BigDecimal("100.00"))
        );
        proveedorRepository.save(
            new Proveedor(esperanza, "Distribuidora Lima S.A.C.", "20598765432", false, 90, new BigDecimal("100.00"))
        );

        Producto paracetamol = crearParacetamol(esperanza);
        Producto amoxicilina = crearAmoxicilina(esperanza);
        Producto bloqueador = crearProducto(esperanza, "BLQ-50", "Bloqueador Solar", "Óxido de zinc", "FPS 50",
            FormaFarmaceutica.SEMISOLIDO_TOPICO, CategoriaRegulatoria.PRODUCTO_SANITARIO, CondicionVenta.SIN_RECETA,
            Conservacion.AMBIENTE, "Frasco x 120 mL", 1, "Isdin", "PS-11223", "55.00");

        crearLote(paracetamol, sucursal, andina, "P-121", 15, 40, "0.20", tecnico);
        crearLote(amoxicilina, sucursal, andina, "A-221", 25, 30, "0.45", tecnico);
        crearLote(bloqueador, sucursal, andina, "B-721", 50, 10, "28.00", tecnico);

        crearHistorial(paracetamol, sucursal, andina, tecnico, 5);
    }


    private Usuario crearUsuario(String nombreUsuario, String nombreCompleto, Rol rol, Empresa empresa, Sucursal sucursal) {

        return usuarioService.crearCuenta(
            new Usuario(nombreUsuario, nombreCompleto, nombreUsuario + "@farmalerta.pe", CLAVE, rol, empresa, sucursal, true)
        );
    }


    private Producto crearParacetamol(Empresa empresa) {

        return crearProducto(empresa, "PAR-500", "Paracetamol", "Paracetamol", "500 mg",
            FormaFarmaceutica.TABLETA, CategoriaRegulatoria.MEDICAMENTO, CondicionVenta.SIN_RECETA,
            Conservacion.AMBIENTE, "Caja x 100 tabletas", 100, "Portugal", "EN-02345", "0.50");
    }


    private Producto crearAmoxicilina(Empresa empresa) {

        return crearProducto(empresa, "AMX-500", "Amoxicilina", "Amoxicilina", "500 mg",
            FormaFarmaceutica.CAPSULA, CategoriaRegulatoria.MEDICAMENTO, CondicionVenta.CON_RECETA,
            Conservacion.AMBIENTE, "Caja x 100 cápsulas", 100, "Genfar", "EN-05678", "1.20");
    }


    private Producto crearProducto(
            Empresa empresa,
            String codigo,
            String nombreComercial,
            String principioActivo,
            String concentracion,
            FormaFarmaceutica forma,
            CategoriaRegulatoria categoria,
            CondicionVenta condicion,
            Conservacion conservacion,
            String presentacion,
            int unidadesPorEmpaque,
            String laboratorio,
            String registroSanitario,
            String precioVenta
    ) {

        return productoRepository.save(
            new Producto(
                empresa, codigo, nombreComercial, principioActivo, concentracion,
                forma, categoria, condicion, conservacion, presentacion,
                unidadesPorEmpaque, laboratorio, registroSanitario,
                new BigDecimal(precioVenta), null, null
            )
        );
    }


    private void crearLote(
            Producto producto,
            Sucursal sucursal,
            Proveedor proveedor,
            String numeroLote,
            int diasParaVencer,
            int cantidad,
            String costo,
            Usuario usuario
    ) {

        LocalDate ingreso = hoy.minusDays(15);

        Lote lote = loteRepository.save(
            new Lote(
                producto, sucursal, proveedor, numeroLote,
                hoy.plusDays(diasParaVencer), cantidad, cantidad, 0,
                new BigDecimal(costo), ingreso, EstadoLote.DISPONIBLE, null, null
            )
        );

        registrarIngreso(lote, usuario, ingreso);
    }


    private void crearHistorial(
            Producto producto,
            Sucursal sucursal,
            Proveedor proveedor,
            Usuario usuario,
            double demandaMedia
    ) {

        LocalDate ingreso = hoy.minusDays(61);

        Lote lote = loteRepository.save(
            new Lote(
                producto, sucursal, proveedor, "H-" + producto.getCodigo() + "-" + sucursal.getId(),
                hoy.plusDays(400), 0, 0, 0,
                producto.getPrecioVenta().multiply(new BigDecimal("0.50")),
                ingreso, EstadoLote.AGOTADO, null, null
            )
        );

        int total = 0;

        for (int dia = 60; dia >= 1; dia--) {

            int cantidad = (int) Math.max(0, Math.round(demandaMedia + aleatorio.nextGaussian() * demandaMedia * 0.3));

            if (cantidad == 0) {
                continue;
            }

            total += cantidad;

            movimientoRepository.save(
                new Movimiento(
                    lote, null, TipoMovimiento.VENTA, cantidad,
                    producto.getPrecioVenta(), BigDecimal.ZERO.setScale(2),
                    producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)),
                    usuario, hoy.minusDays(dia).atTime(11, 0), "Venta histórica (datos de demostración)", null
                )
            );
        }

        lote.setCantidadInicial(total);
        loteRepository.save(lote);

        registrarIngreso(lote, usuario, ingreso);
    }


    private void registrarIngreso(Lote lote, Usuario usuario, LocalDate fecha) {

        movimientoRepository.save(
            new Movimiento(
                lote, null, TipoMovimiento.INGRESO, lote.getCantidadInicial(),
                lote.getCostoUnitario(), BigDecimal.ZERO.setScale(2),
                lote.getCostoUnitario().multiply(BigDecimal.valueOf(lote.getCantidadInicial())),
                usuario, fecha.atTime(9, 0), "Ingreso de lote", null
            )
        );
    }
}
