package pe.edu.empresa.farmalerta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface LoteRepository
        extends JpaRepository<Lote, Long> {

    List<Lote> findBySucursalIdOrderByFechaVencimientoAsc(Long sucursalId);

    List<Lote> findBySucursalEmpresaIdOrderByFechaVencimientoAsc(Long empresaId);

    List<Lote> findBySucursalIdAndProductoIdAndEstadoOrderByFechaVencimientoAsc(Long sucursalId, Long productoId, EstadoLote estado);

    List<Lote> findBySucursalEmpresaIdAndEstadoOrderByFechaVencimientoAsc(Long empresaId, EstadoLote estado);

    Optional<Lote> findBySucursalIdAndProductoIdAndNumeroLote(Long sucursalId, Long productoId, String numeroLote);

    List<Lote> findByProductoIdAndNumeroLote(Long productoId, String numeroLote);

    List<Lote> findByEstadoIn(List<EstadoLote> estados);

    boolean existsByProductoId(Long productoId);

    boolean existsByProveedorId(Long proveedorId);

}
