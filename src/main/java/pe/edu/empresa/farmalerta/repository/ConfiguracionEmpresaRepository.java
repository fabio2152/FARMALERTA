package pe.edu.empresa.farmalerta.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface ConfiguracionEmpresaRepository
        extends JpaRepository<ConfiguracionEmpresa, Long> {

    Optional<ConfiguracionEmpresa> findByEmpresaId(Long empresaId);

}
