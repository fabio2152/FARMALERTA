package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelAlerta nivel;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private Boolean leida;


    public Notificacion() {
    }


    public Notificacion(
            Sucursal sucursal,
            Lote lote,
            NivelAlerta nivel,
            LocalDate fecha,
            String mensaje,
            Boolean leida
    ) {

        this.sucursal = sucursal;
        this.lote = lote;
        this.nivel = nivel;
        this.fecha = fecha;
        this.mensaje = mensaje;
        this.leida = leida;
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


    public Lote getLote() {
        return lote;
    }


    public void setLote(Lote lote) {
        this.lote = lote;
    }


    public NivelAlerta getNivel() {
        return nivel;
    }


    public void setNivel(NivelAlerta nivel) {
        this.nivel = nivel;
    }


    public LocalDate getFecha() {
        return fecha;
    }


    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


    public String getMensaje() {
        return mensaje;
    }


    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }


    public Boolean getLeida() {
        return leida;
    }


    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

}
