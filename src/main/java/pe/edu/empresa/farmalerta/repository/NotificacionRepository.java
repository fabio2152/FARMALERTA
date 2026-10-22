package pe.edu.empresa.farmalerta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface NotificacionRepository
        extends JpaRepository<Notificacion, Long> {

    boolean existsByLoteIdAndNivel(Long loteId, NivelAlerta nivel);

    List<Notificacion> findBySucursalIdOrderByFechaDescIdDesc(Long sucursalId);

    List<Notificacion> findBySucursalEmpresaIdOrderByFechaDescIdDesc(Long empresaId);

    List<Notificacion> findBySucursalIdAndLeidaFalseOrderByFechaDescIdDesc(Long sucursalId);

    List<Notificacion> findBySucursalEmpresaIdAndLeidaFalseOrderByFechaDescIdDesc(Long empresaId);

}
