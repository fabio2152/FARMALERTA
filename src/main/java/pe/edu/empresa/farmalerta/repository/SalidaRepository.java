package pe.edu.empresa.farmalerta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface SalidaRepository
        extends JpaRepository<Salida, Long> {

    List<Salida> findBySucursalIdOrderByFechaHoraDesc(Long sucursalId);

    List<Salida> findBySucursalEmpresaIdOrderByFechaHoraDesc(Long empresaId);

    List<Salida> findBySucursalIdAndEstadoOrderByFechaHoraAsc(Long sucursalId, EstadoSalida estado);

}
