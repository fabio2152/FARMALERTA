package pe.edu.empresa.farmalerta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface AlertaSanitariaRepository
        extends JpaRepository<AlertaSanitaria, Long> {

    List<AlertaSanitaria> findByEmpresaIdOrderByFechaRegistroDescIdDesc(Long empresaId);

}
