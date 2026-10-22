package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "transferencias")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @ManyToOne
    @JoinColumn(name = "sucursal_origen_id", nullable = false)
    private Sucursal sucursalOrigen;

    @ManyToOne
    @JoinColumn(name = "sucursal_destino_id", nullable = false)
    private Sucursal sucursalDestino;

    @Column(nullable = false)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoTransferencia estado;

    @ManyToOne
    @JoinColumn(name = "solicitada_por_id", nullable = false)
    private Usuario solicitadaPor;

    @ManyToOne
    @JoinColumn(name = "recibida_por_id")
    private Usuario recibidaPor;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column
    private LocalDateTime fechaRecepcion;


    public Transferencia() {
    }


    public Transferencia(
            Lote lote,
            Sucursal sucursalOrigen,
            Sucursal sucursalDestino,
            Integer cantidad,
            EstadoTransferencia estado,
            Usuario solicitadaPor,
            Usuario recibidaPor,
            LocalDateTime fechaSolicitud,
            LocalDateTime fechaRecepcion
    ) {

        this.lote = lote;
        this.sucursalOrigen = sucursalOrigen;
        this.sucursalDestino = sucursalDestino;
        this.cantidad = cantidad;
        this.estado = estado;
        this.solicitadaPor = solicitadaPor;
        this.recibidaPor = recibidaPor;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaRecepcion = fechaRecepcion;
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


    public Sucursal getSucursalOrigen() {
        return sucursalOrigen;
    }


    public void setSucursalOrigen(Sucursal sucursalOrigen) {
        this.sucursalOrigen = sucursalOrigen;
    }


    public Sucursal getSucursalDestino() {
        return sucursalDestino;
    }


    public void setSucursalDestino(Sucursal sucursalDestino) {
        this.sucursalDestino = sucursalDestino;
    }


    public Integer getCantidad() {
        return cantidad;
    }


    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }


    public EstadoTransferencia getEstado() {
        return estado;
    }


    public void setEstado(EstadoTransferencia estado) {
        this.estado = estado;
    }


    public Usuario getSolicitadaPor() {
        return solicitadaPor;
    }


    public void setSolicitadaPor(Usuario solicitadaPor) {
        this.solicitadaPor = solicitadaPor;
    }


    public Usuario getRecibidaPor() {
        return recibidaPor;
    }


    public void setRecibidaPor(Usuario recibidaPor) {
        this.recibidaPor = recibidaPor;
    }


    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }


    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }


    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }


    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

}
