package pe.edu.empresa.farmalerta.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.empresa.farmalerta.model.*;


public interface MovimientoRepository
        extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByLoteIdOrderByFechaHoraAsc(Long loteId);

    List<Movimiento> findBySalidaIdOrderByIdAsc(Long salidaId);

    List<Movimiento> findByTipoAndLoteProductoIdAndLoteSucursalIdAndFechaHoraGreaterThanEqualAndFechaHoraLessThan(TipoMovimiento tipo, Long productoId, Long sucursalId, LocalDateTime desde, LocalDateTime hasta);

    Optional<Movimiento> findFirstByTipoAndLoteProductoIdAndLoteSucursalIdOrderByFechaHoraAsc(TipoMovimiento tipo, Long productoId, Long sucursalId);

    List<Movimiento> findByLoteSucursalIdAndFechaHoraGreaterThanEqualAndFechaHoraLessThanOrderByFechaHoraAsc(Long sucursalId, LocalDateTime desde, LocalDateTime hasta);

    List<Movimiento> findByLoteSucursalEmpresaIdAndFechaHoraGreaterThanEqualAndFechaHoraLessThanOrderByFechaHoraAsc(Long empresaId, LocalDateTime desde, LocalDateTime hasta);

}
