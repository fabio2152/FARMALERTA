package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "alertas_sanitarias")
public class AlertaSanitaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, length = 50)
    private String numeroLote;

    @Column(nullable = false, length = 200)
    private String documentoReferencia;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "registrada_por_id", nullable = false)
    private Usuario registradaPor;

    @Column(nullable = false)
    private Boolean levantada;

    @Column(length = 200)
    private String documentoLevantamiento;


    public AlertaSanitaria() {
    }


    public AlertaSanitaria(
            Empresa empresa,
            Producto producto,
            String numeroLote,
            String documentoReferencia,
            LocalDate fechaRegistro,
            Usuario registradaPor,
            Boolean levantada,
            String documentoLevantamiento
    ) {

        this.empresa = empresa;
        this.producto = producto;
        this.numeroLote = numeroLote;
        this.documentoReferencia = documentoReferencia;
        this.fechaRegistro = fechaRegistro;
        this.registradaPor = registradaPor;
        this.levantada = levantada;
        this.documentoLevantamiento = documentoLevantamiento;
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


    public Producto getProducto() {
        return producto;
    }


    public void setProducto(Producto producto) {
        this.producto = producto;
    }


    public String getNumeroLote() {
        return numeroLote;
    }


    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }


    public String getDocumentoReferencia() {
        return documentoReferencia;
    }


    public void setDocumentoReferencia(String documentoReferencia) {
        this.documentoReferencia = documentoReferencia;
    }


    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }


    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }


    public Usuario getRegistradaPor() {
        return registradaPor;
    }


    public void setRegistradaPor(Usuario registradaPor) {
        this.registradaPor = registradaPor;
    }


    public Boolean getLevantada() {
        return levantada;
    }


    public void setLevantada(Boolean levantada) {
        this.levantada = levantada;
    }


    public String getDocumentoLevantamiento() {
        return documentoLevantamiento;
    }


    public void setDocumentoLevantamiento(String documentoLevantamiento) {
        this.documentoLevantamiento = documentoLevantamiento;
    }

}
