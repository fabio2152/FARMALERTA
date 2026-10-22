package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombreComercial;

    @Column(nullable = false, length = 150)
    private String principioActivo;

    @Column(length = 50)
    private String concentracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private FormaFarmaceutica formaFarmaceutica;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CategoriaRegulatoria categoriaRegulatoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CondicionVenta condicionVenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Conservacion conservacion;

    @Column(length = 150)
    private String presentacion;

    @Column(nullable = false)
    private Integer unidadesPorEmpaque;

    @Column(length = 150)
    private String laboratorio;

    @Column(length = 50)
    private String registroSanitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column
    private Integer umbralAmarilloPropio;

    @Column
    private Integer umbralRojoPropio;


    public Producto() {
    }


    public Producto(
            Empresa empresa,
            String codigo,
            String nombreComercial,
            String principioActivo,
            String concentracion,
            FormaFarmaceutica formaFarmaceutica,
            CategoriaRegulatoria categoriaRegulatoria,
            CondicionVenta condicionVenta,
            Conservacion conservacion,
            String presentacion,
            Integer unidadesPorEmpaque,
            String laboratorio,
            String registroSanitario,
            BigDecimal precioVenta,
            Integer umbralAmarilloPropio,
            Integer umbralRojoPropio
    ) {

        this.empresa = empresa;
        this.codigo = codigo;
        this.nombreComercial = nombreComercial;
        this.principioActivo = principioActivo;
        this.concentracion = concentracion;
        this.formaFarmaceutica = formaFarmaceutica;
        this.categoriaRegulatoria = categoriaRegulatoria;
        this.condicionVenta = condicionVenta;
        this.conservacion = conservacion;
        this.presentacion = presentacion;
        this.unidadesPorEmpaque = unidadesPorEmpaque;
        this.laboratorio = laboratorio;
        this.registroSanitario = registroSanitario;
        this.precioVenta = precioVenta;
        this.umbralAmarilloPropio = umbralAmarilloPropio;
        this.umbralRojoPropio = umbralRojoPropio;
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


    public String getCodigo() {
        return codigo;
    }


    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    public String getNombreComercial() {
        return nombreComercial;
    }


    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }


    public String getPrincipioActivo() {
        return principioActivo;
    }


    public void setPrincipioActivo(String principioActivo) {
        this.principioActivo = principioActivo;
    }


    public String getConcentracion() {
        return concentracion;
    }


    public void setConcentracion(String concentracion) {
        this.concentracion = concentracion;
    }


    public FormaFarmaceutica getFormaFarmaceutica() {
        return formaFarmaceutica;
    }


    public void setFormaFarmaceutica(FormaFarmaceutica formaFarmaceutica) {
        this.formaFarmaceutica = formaFarmaceutica;
    }


    public CategoriaRegulatoria getCategoriaRegulatoria() {
        return categoriaRegulatoria;
    }


    public void setCategoriaRegulatoria(CategoriaRegulatoria categoriaRegulatoria) {
        this.categoriaRegulatoria = categoriaRegulatoria;
    }


    public CondicionVenta getCondicionVenta() {
        return condicionVenta;
    }


    public void setCondicionVenta(CondicionVenta condicionVenta) {
        this.condicionVenta = condicionVenta;
    }


    public Conservacion getConservacion() {
        return conservacion;
    }


    public void setConservacion(Conservacion conservacion) {
        this.conservacion = conservacion;
    }


    public String getPresentacion() {
        return presentacion;
    }


    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }


    public Integer getUnidadesPorEmpaque() {
        return unidadesPorEmpaque;
    }


    public void setUnidadesPorEmpaque(Integer unidadesPorEmpaque) {
        this.unidadesPorEmpaque = unidadesPorEmpaque;
    }


    public String getLaboratorio() {
        return laboratorio;
    }


    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }


    public String getRegistroSanitario() {
        return registroSanitario;
    }


    public void setRegistroSanitario(String registroSanitario) {
        this.registroSanitario = registroSanitario;
    }


    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }


    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }


    public Integer getUmbralAmarilloPropio() {
        return umbralAmarilloPropio;
    }


    public void setUmbralAmarilloPropio(Integer umbralAmarilloPropio) {
        this.umbralAmarilloPropio = umbralAmarilloPropio;
    }


    public Integer getUmbralRojoPropio() {
        return umbralRojoPropio;
    }


    public void setUmbralRojoPropio(Integer umbralRojoPropio) {
        this.umbralRojoPropio = umbralRojoPropio;
    }

}
