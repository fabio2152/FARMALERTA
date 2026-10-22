package pe.edu.empresa.farmalerta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    List<Usuario> findByEmpresaIdOrderByNombreCompletoAsc(Long empresaId);

}
