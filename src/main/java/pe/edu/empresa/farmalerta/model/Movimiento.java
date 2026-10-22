package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @ManyToOne
    @JoinColumn(name = "salida_id")
    private Salida salida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoAplicado;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 100)
    private String motivo;

    @Column(length = 500)
    private String observacion;


    public Movimiento() {
    }


    public Movimiento(
            Lote lote,
            Salida salida,
            TipoMovimiento tipo,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal descuentoAplicado,
            BigDecimal montoTotal,
            Usuario usuario,
            LocalDateTime fechaHora,
            String motivo,
            String observacion
    ) {

        this.lote = lote;
        this.salida = salida;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuentoAplicado = descuentoAplicado;
        this.montoTotal = montoTotal;
        this.usuario = usuario;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.observacion = observacion;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Lote getLote() {
        return lote;
    }


    public void setLote(Lote lote) {
        this.lote = lote;
    }


    public Salida getSalida() {
        return salida;
    }


    public void setSalida(Salida salida) {
        this.salida = salida;
    }


    public TipoMovimiento getTipo() {
        return tipo;
    }


    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }


    public Integer getCantidad() {
        return cantidad;
    }


    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }


    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }


    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }


    public BigDecimal getDescuentoAplicado() {
        return descuentoAplicado;
    }


    public void setDescuentoAplicado(BigDecimal descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }


    public BigDecimal getMontoTotal() {
        return montoTotal;
    }


    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }


    public Usuario getUsuario() {
        return usuario;
    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public LocalDateTime getFechaHora() {
        return fechaHora;
    }


    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }


    public String getMotivo() {
        return motivo;
    }


    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }


    public String getObservacion() {
        return observacion;
    }


    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

}
