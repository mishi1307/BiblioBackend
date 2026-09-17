package pe.edu.upeu.BiblioBackend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.BiblioBackend.dto.reporte.LibroMasPrestadoDTO;
import pe.edu.upeu.BiblioBackend.dto.reporte.PrestamoPorGeneroDTO;
import pe.edu.upeu.BiblioBackend.entity.Prestamo;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;

import java.time.LocalDateTime;
import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("""
            select distinct p
            from Prestamo p
            left join fetch p.socio s
            left join fetch p.detalles d
            left join fetch d.libro l
            where (:socioId is null or s.id = :socioId)
              and (:estado is null or p.estado = :estado)
              and (:desde is null or p.fecha >= :desde)
              and (:hasta is null or p.fecha <= :hasta)
            """)
    List<Prestamo> buscar(
            @Param("socioId") Long socioId,
            @Param("estado") EstadoPrestamo estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Sort sort
    );

    @Query("""
            select new pe.edu.upeu.BiblioBackend.dto.reporte.PrestamoPorGeneroDTO(
                    g.id,
                    g.nombre,
                    sum(d.cantidad),
                    sum(d.subtotal)
            )
            from DetallePrestamo d
            join d.prestamo p
            join d.libro l
            join l.genero g
            where p.estado in (
                pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo.REGISTRADO,
                pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo.DEVUELTO
            )
              and (:desde is null or p.fecha >= :desde)
              and (:hasta is null or p.fecha <= :hasta)
            group by g.id, g.nombre
            order by sum(d.cantidad) desc
            """)
    List<PrestamoPorGeneroDTO> reportePrestamosPorGenero(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    @Query("""
            select new pe.edu.upeu.BiblioBackend.dto.reporte.LibroMasPrestadoDTO(
                    l.id,
                    l.titulo,
                    g.nombre,
                    sum(d.cantidad),
                    sum(d.subtotal)
            )
            from DetallePrestamo d
            join d.prestamo p
            join d.libro l
            join l.genero g
            where p.estado in (
                pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo.REGISTRADO,
                pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo.DEVUELTO
            )
              and (:desde is null or p.fecha >= :desde)
              and (:hasta is null or p.fecha <= :hasta)
            group by l.id, l.titulo, g.nombre
            order by sum(d.cantidad) desc
            """)
    List<LibroMasPrestadoDTO> reporteLibrosMasPrestados(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}