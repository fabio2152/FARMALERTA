package pe.edu.empresa.farmalerta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface ProveedorRepository
        extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findByEmpresaIdOrderByRazonSocialAsc(Long empresaId);

}
