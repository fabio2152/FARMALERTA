package pe.edu.empresa.farmalerta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface EmpresaRepository
        extends JpaRepository<Empresa, Long> {

    boolean existsByRuc(String ruc);

    List<Empresa> findAllByOrderByRazonSocialAsc();

}
