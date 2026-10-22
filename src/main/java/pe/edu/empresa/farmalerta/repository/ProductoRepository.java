package pe.edu.empresa.farmalerta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface ProductoRepository
        extends JpaRepository<Producto, Long> {

    List<Producto> findByEmpresaIdOrderByNombreComercialAsc(Long empresaId);

    boolean existsByEmpresaIdAndCodigo(Long empresaId, String codigo);

}
