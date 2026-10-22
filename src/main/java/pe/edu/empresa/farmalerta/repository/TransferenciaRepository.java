package pe.edu.empresa.farmalerta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface TransferenciaRepository
        extends JpaRepository<Transferencia, Long> {

    List<Transferencia> findBySucursalOrigenEmpresaIdOrderByFechaSolicitudDesc(Long empresaId);

    List<Transferencia> findBySucursalOrigenIdOrSucursalDestinoIdOrderByFechaSolicitudDesc(Long origenId, Long destinoId);

}
