package pe.edu.empresa.farmalerta.model;

import jakarta.persistence.*;


@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String razonSocial;

    @Column(nullable = false, length = 11, unique = true)
    private String ruc;


    public Empresa() {
    }


    public Empresa(
            String razonSocial,
            String ruc
    ) {

        this.razonSocial = razonSocial;
        this.ruc = ruc;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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

}
