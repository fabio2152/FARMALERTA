package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "lotes")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column(nullable = false, length = 50)
    private String numeroLote;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private Integer cantidadInicial;

    @Column(nullable = false)
    private Integer cantidadDisponible;

    @Column(nullable = false)
    private Integer cantidadReservada;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costoUnitario;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoLote estado;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private MotivoBloqueo motivoBloqueo;

    @ManyToOne
    @JoinColumn(name = "autorizado_por_id")
    private Usuario autorizadoPor;


    public Lote() {
    }


    public Lote(
            Producto producto,
            Sucursal sucursal,
            Proveedor proveedor,
            String numeroLote,
            LocalDate fechaVencimiento,
            Integer cantidadInicial,
            Integer cantidadDisponible,
            Integer cantidadReservada,
            BigDecimal costoUnitario,
            LocalDate fechaIngreso,
            EstadoLote estado,
            MotivoBloqueo motivoBloqueo,
            Usuario autorizadoPor
    ) {

        this.producto = producto;
        this.sucursal = sucursal;
        this.proveedor = proveedor;
        this.numeroLote = numeroLote;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidadInicial = cantidadInicial;
        this.cantidadDisponible = cantidadDisponible;
        this.cantidadReservada = cantidadReservada;
        this.costoUnitario = costoUnitario;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.motivoBloqueo = motivoBloqueo;
        this.autorizadoPor = autorizadoPor;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Producto getProducto() {
        return producto;
    }


    public void setProducto(Producto producto) {
        this.producto = producto;
    }


    public Sucursal getSucursal() {
        return sucursal;
    }


    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }


    public Proveedor getProveedor() {
        return proveedor;
    }


    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }


    public String getNumeroLote() {
        return numeroLote;
    }


    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }


    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }


    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }


    public Integer getCantidadInicial() {
        return cantidadInicial;
    }


    public void setCantidadInicial(Integer cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }


    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }


    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }


    public Integer getCantidadReservada() {
        return cantidadReservada;
    }


    public void setCantidadReservada(Integer cantidadReservada) {
        this.cantidadReservada = cantidadReservada;
    }


    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }


    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }


    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }


    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }


    public EstadoLote getEstado() {
        return estado;
    }


    public void setEstado(EstadoLote estado) {
        this.estado = estado;
    }


    public MotivoBloqueo getMotivoBloqueo() {
        return motivoBloqueo;
    }


    public void setMotivoBloqueo(MotivoBloqueo motivoBloqueo) {
        this.motivoBloqueo = motivoBloqueo;
    }


    public Usuario getAutorizadoPor() {
        return autorizadoPor;
    }


    public void setAutorizadoPor(Usuario autorizadoPor) {
        this.autorizadoPor = autorizadoPor;
    }

}
