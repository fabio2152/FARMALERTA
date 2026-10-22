package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 150)
    private String razonSocial;

    @Column(nullable = false, length = 11)
    private String ruc;

    @Column(nullable = false)
    private Boolean aceptaCanje;

    @Column(nullable = false)
    private Integer diasMinimosCanje;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeReconocido;


    public Proveedor() {
    }


    public Proveedor(
            Empresa empresa,
            String razonSocial,
            String ruc,
            Boolean aceptaCanje,
            Integer diasMinimosCanje,
            BigDecimal porcentajeReconocido
    ) {

        this.empresa = empresa;
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.aceptaCanje = aceptaCanje;
        this.diasMinimosCanje = diasMinimosCanje;
        this.porcentajeReconocido = porcentajeReconocido;
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


    public String getRazonSocial() {
        return razonSocial;
    }


    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }


    public String getRuc() {
        return ruc;
    }


    public void setRuc(String ruc) {
        this.ruc = ruc;
    }


    public Boolean getAceptaCanje() {
        return aceptaCanje;
    }


    public void setAceptaCanje(Boolean aceptaCanje) {
        this.aceptaCanje = aceptaCanje;
    }


    public Integer getDiasMinimosCanje() {
        return diasMinimosCanje;
    }


    public void setDiasMinimosCanje(Integer diasMinimosCanje) {
        this.diasMinimosCanje = diasMinimosCanje;
    }


    public BigDecimal getPorcentajeReconocido() {
        return porcentajeReconocido;
    }


    public void setPorcentajeReconocido(BigDecimal porcentajeReconocido) {
        this.porcentajeReconocido = porcentajeReconocido;
    }

}
