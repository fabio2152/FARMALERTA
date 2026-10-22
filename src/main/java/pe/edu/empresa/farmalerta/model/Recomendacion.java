package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "recomendaciones")
public class Recomendacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccionRecomendada accion;

    @Column
    private Integer unidadesEnRiesgo;

    @Column(precision = 10, scale = 2)
    private BigDecimal demandaDiaria;

    @ManyToOne
    @JoinColumn(name = "sucursal_sugerida_id")
    private Sucursal sucursalSugerida;

    @Column(precision = 5, scale = 2)
    private BigDecimal descuentoSugerido;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorRecuperable;

    @Column(length = 1000)
    private String justificacion;

    @Column(nullable = false)
    private Boolean sinPrediccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoRecomendacion estado;

    @ManyToOne
    @JoinColumn(name = "decidida_por_id")
    private Usuario decididaPor;

    @Column(length = 500)
    private String motivoRechazo;


    public Recomendacion() {
    }


    public Recomendacion(
            Lote lote,
            LocalDate fecha,
            AccionRecomendada accion,
            Integer unidadesEnRiesgo,
            BigDecimal demandaDiaria,
            Sucursal sucursalSugerida,
            BigDecimal descuentoSugerido,
            BigDecimal valorRecuperable,
            String justificacion,
            Boolean sinPrediccion,
            EstadoRecomendacion estado,
            Usuario decididaPor,
            String motivoRechazo
    ) {

        this.lote = lote;
        this.fecha = fecha;
        this.accion = accion;
        this.unidadesEnRiesgo = unidadesEnRiesgo;
        this.demandaDiaria = demandaDiaria;
        this.sucursalSugerida = sucursalSugerida;
        this.descuentoSugerido = descuentoSugerido;
        this.valorRecuperable = valorRecuperable;
        this.justificacion = justificacion;
        this.sinPrediccion = sinPrediccion;
        this.estado = estado;
        this.decididaPor = decididaPor;
        this.motivoRechazo = motivoRechazo;
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


    public LocalDate getFecha() {
        return fecha;
    }


    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


    public AccionRecomendada getAccion() {
        return accion;
    }


    public void setAccion(AccionRecomendada accion) {
        this.accion = accion;
    }


    public Integer getUnidadesEnRiesgo() {
        return unidadesEnRiesgo;
    }


    public void setUnidadesEnRiesgo(Integer unidadesEnRiesgo) {
        this.unidadesEnRiesgo = unidadesEnRiesgo;
    }


    public BigDecimal getDemandaDiaria() {
        return demandaDiaria;
    }


    public void setDemandaDiaria(BigDecimal demandaDiaria) {
        this.demandaDiaria = demandaDiaria;
    }


    public Sucursal getSucursalSugerida() {
        return sucursalSugerida;
    }


    public void setSucursalSugerida(Sucursal sucursalSugerida) {
        this.sucursalSugerida = sucursalSugerida;
    }


    public BigDecimal getDescuentoSugerido() {
        return descuentoSugerido;
    }


    public void setDescuentoSugerido(BigDecimal descuentoSugerido) {
        this.descuentoSugerido = descuentoSugerido;
    }


    public BigDecimal getValorRecuperable() {
        return valorRecuperable;
    }


    public void setValorRecuperable(BigDecimal valorRecuperable) {
        this.valorRecuperable = valorRecuperable;
    }


    public String getJustificacion() {
        return justificacion;
    }


    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }


    public Boolean getSinPrediccion() {
        return sinPrediccion;
    }


    public void setSinPrediccion(Boolean sinPrediccion) {
        this.sinPrediccion = sinPrediccion;
    }


    public EstadoRecomendacion getEstado() {
        return estado;
    }


    public void setEstado(EstadoRecomendacion estado) {
        this.estado = estado;
    }


    public Usuario getDecididaPor() {
        return decididaPor;
    }


    public void setDecididaPor(Usuario decididaPor) {
        this.decididaPor = decididaPor;
    }


    public String getMotivoRechazo() {
        return motivoRechazo;
    }


    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

}
