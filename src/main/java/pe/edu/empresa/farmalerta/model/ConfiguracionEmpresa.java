package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "configuraciones_empresa")
public class ConfiguracionEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "empresa_id", nullable = false, unique = true)
    private Empresa empresa;

    @Column(nullable = false)
    private Integer umbralAmarillo;

    @Column(nullable = false)
    private Integer umbralRojo;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoAmarillo;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoRojo;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoUltimoDia;

    @Column(nullable = false)
    private Boolean permitirVentaUltimoDia;

    @Column(nullable = false)
    private Boolean permitirVentaBajoCosto;

    @Column(nullable = false)
    private Integer vidaUtilMinimaIngreso;

    @Column(nullable = false)
    private Integer diasTraslado;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal alfaPrediccion;

    @Column(nullable = false)
    private Integer diasHistorial;

    @Column(nullable = false)
    private Integer diasHistorialMinimo;


    public ConfiguracionEmpresa() {
    }


    public ConfiguracionEmpresa(
            Empresa empresa,
            Integer umbralAmarillo,
            Integer umbralRojo,
            BigDecimal descuentoAmarillo,
            BigDecimal descuentoRojo,
            BigDecimal descuentoUltimoDia,
            Boolean permitirVentaUltimoDia,
            Boolean permitirVentaBajoCosto,
            Integer vidaUtilMinimaIngreso,
            Integer diasTraslado,
            BigDecimal alfaPrediccion,
            Integer diasHistorial,
            Integer diasHistorialMinimo
    ) {

        this.empresa = empresa;
        this.umbralAmarillo = umbralAmarillo;
        this.umbralRojo = umbralRojo;
        this.descuentoAmarillo = descuentoAmarillo;
        this.descuentoRojo = descuentoRojo;
        this.descuentoUltimoDia = descuentoUltimoDia;
        this.permitirVentaUltimoDia = permitirVentaUltimoDia;
        this.permitirVentaBajoCosto = permitirVentaBajoCosto;
        this.vidaUtilMinimaIngreso = vidaUtilMinimaIngreso;
        this.diasTraslado = diasTraslado;
        this.alfaPrediccion = alfaPrediccion;
        this.diasHistorial = diasHistorial;
        this.diasHistorialMinimo = diasHistorialMinimo;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Empresa getEmpresa() {
        return empresa;
    }


    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }


    public Integer getUmbralAmarillo() {
        return umbralAmarillo;
    }


    public void setUmbralAmarillo(Integer umbralAmarillo) {
        this.umbralAmarillo = umbralAmarillo;
    }


    public Integer getUmbralRojo() {
        return umbralRojo;
    }


    public void setUmbralRojo(Integer umbralRojo) {
        this.umbralRojo = umbralRojo;
    }


    public BigDecimal getDescuentoAmarillo() {
        return descuentoAmarillo;
    }


    public void setDescuentoAmarillo(BigDecimal descuentoAmarillo) {
        this.descuentoAmarillo = descuentoAmarillo;
    }


    public BigDecimal getDescuentoRojo() {
        return descuentoRojo;
    }


    public void setDescuentoRojo(BigDecimal descuentoRojo) {
        this.descuentoRojo = descuentoRojo;
    }


    public BigDecimal getDescuentoUltimoDia() {
        return descuentoUltimoDia;
    }


    public void setDescuentoUltimoDia(BigDecimal descuentoUltimoDia) {
        this.descuentoUltimoDia = descuentoUltimoDia;
    }


    public Boolean getPermitirVentaUltimoDia() {
        return permitirVentaUltimoDia;
    }


    public void setPermitirVentaUltimoDia(Boolean permitirVentaUltimoDia) {
        this.permitirVentaUltimoDia = permitirVentaUltimoDia;
    }


    public Boolean getPermitirVentaBajoCosto() {
        return permitirVentaBajoCosto;
    }


    public void setPermitirVentaBajoCosto(Boolean permitirVentaBajoCosto) {
        this.permitirVentaBajoCosto = permitirVentaBajoCosto;
    }


    public Integer getVidaUtilMinimaIngreso() {
        return vidaUtilMinimaIngreso;
    }


    public void setVidaUtilMinimaIngreso(Integer vidaUtilMinimaIngreso) {
        this.vidaUtilMinimaIngreso = vidaUtilMinimaIngreso;
    }


    public Integer getDiasTraslado() {
        return diasTraslado;
    }


    public void setDiasTraslado(Integer diasTraslado) {
        this.diasTraslado = diasTraslado;
    }


    public BigDecimal getAlfaPrediccion() {
        return alfaPrediccion;
    }


    public void setAlfaPrediccion(BigDecimal alfaPrediccion) {
        this.alfaPrediccion = alfaPrediccion;
    }


    public Integer getDiasHistorial() {
        return diasHistorial;
    }


    public void setDiasHistorial(Integer diasHistorial) {
        this.diasHistorial = diasHistorial;
    }


    public Integer getDiasHistorialMinimo() {
        return diasHistorialMinimo;
    }


    public void setDiasHistorialMinimo(Integer diasHistorialMinimo) {
        this.diasHistorialMinimo = diasHistorialMinimo;
    }

}
