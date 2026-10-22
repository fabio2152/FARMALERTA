package pe.edu.empresa.farmalerta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface RecomendacionRepository
        extends JpaRepository<Recomendacion, Long> {

    Optional<Recomendacion> findFirstByLoteIdAndEstado(Long loteId, EstadoRecomendacion estado);

    boolean existsByLoteIdAndAccionAndEstadoIn(Long loteId, AccionRecomendada accion, List<EstadoRecomendacion> estados);

    List<Recomendacion> findByEstado(EstadoRecomendacion estado);

    List<Recomendacion> findByLoteSucursalIdOrderByFechaDescIdDesc(Long sucursalId);

    List<Recomendacion> findByLoteSucursalEmpresaIdOrderByFechaDescIdDesc(Long empresaId);

}
