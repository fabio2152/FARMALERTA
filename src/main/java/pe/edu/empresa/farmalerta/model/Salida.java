package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "salidas")
public class Salida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoSalida tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSalida estado;

    @Column
    private Integer diasTratamiento;

    @Column(length = 2000)
    private String advertencias;

    @ManyToOne
    @JoinColumn(name = "confirmada_por_id")
    private Usuario confirmadaPor;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column
    private Integer cantidadSolicitada;


    public Salida() {
    }


    public Salida(
            Sucursal sucursal,
            Usuario usuario,
            LocalDateTime fechaHora,
            TipoSalida tipo,
            EstadoSalida estado,
            Integer diasTratamiento,
            String advertencias,
            Usuario confirmadaPor,
            Producto producto,
            Integer cantidadSolicitada
    ) {

        this.sucursal = sucursal;
        this.usuario = usuario;
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.estado = estado;
        this.diasTratamiento = diasTratamiento;
        this.advertencias = advertencias;
        this.confirmadaPor = confirmadaPor;
        this.producto = producto;
        this.cantidadSolicitada = cantidadSolicitada;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Sucursal getSucursal() {
        return sucursal;
    }


    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
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


    public TipoSalida getTipo() {
        return tipo;
    }


    public void setTipo(TipoSalida tipo) {
        this.tipo = tipo;
    }


    public EstadoSalida getEstado() {
        return estado;
    }


    public void setEstado(EstadoSalida estado) {
        this.estado = estado;
    }


    public Integer getDiasTratamiento() {
        return diasTratamiento;
    }


    public void setDiasTratamiento(Integer diasTratamiento) {
        this.diasTratamiento = diasTratamiento;
    }


    public String getAdvertencias() {
        return advertencias;
    }


    public void setAdvertencias(String advertencias) {
        this.advertencias = advertencias;
    }


    public Usuario getConfirmadaPor() {
        return confirmadaPor;
    }


    public void setConfirmadaPor(Usuario confirmadaPor) {
        this.confirmadaPor = confirmadaPor;
    }


    public Producto getProducto() {
        return producto;
    }


    public void setProducto(Producto producto) {
        this.producto = producto;
    }


    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }


    public void setCantidadSolicitada(Integer cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

}
