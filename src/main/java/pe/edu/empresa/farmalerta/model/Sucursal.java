package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;


@Entity
@Table(name = "sucursales")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String distrito;


    public Sucursal() {
    }


    public Sucursal(
            Empresa empresa,
            String nombre,
            String direccion,
            String distrito
    ) {

        this.empresa = empresa;
        this.nombre = nombre;
        this.direccion = direccion;
        this.distrito = distrito;
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


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getDireccion() {
        return direccion;
    }


    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    public String getDistrito() {
        return distrito;
    }


    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

}
