package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.DetallePrestamoRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.DetallePrestamoResponseDTO;
import pe.edu.upeu.BiblioBackend.dto.PrestamoRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.PrestamoResponseDTO;
import pe.edu.upeu.BiblioBackend.entity.DetallePrestamo;
import pe.edu.upeu.BiblioBackend.entity.Libro;
import pe.edu.upeu.BiblioBackend.entity.Prestamo;
import pe.edu.upeu.BiblioBackend.entity.Socio;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;
import pe.edu.upeu.BiblioBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException;
import pe.edu.upeu.BiblioBackend.repository.LibroRepository;
import pe.edu.upeu.BiblioBackend.repository.PrestamoRepository;
import pe.edu.upeu.BiblioBackend.repository.SocioRepository;
import pe.edu.upeu.BiblioBackend.service.service.PrestamoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private static final Logger log =
            LoggerFactory.getLogger(PrestamoServiceImpl.class);

    private static final Set<String> CAMPOS_ORDENABLES =
            Set.of("id", "fecha", "totalValorizado", "estado");

    private final PrestamoRepository prestamoRepository;
    private final SocioRepository socioRepository;
    private final LibroRepository libroRepository;

    public PrestamoServiceImpl(
            PrestamoRepository prestamoRepository,
            SocioRepository socioRepository,
            LibroRepository libroRepository) {

        this.prestamoRepository = prestamoRepository;
        this.socioRepository = socioRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    @Transactional
    public PrestamoResponseDTO registrar(
            PrestamoRequestDTO request) {

        long inicio = System.currentTimeMillis();

        log.info(
                "Iniciando registro de préstamo socioId={}, cantidadDetalles={}",
                request.getSocioId(),
                request.getDetalles().size()
        );

        Socio socio = socioRepository.findById(request.getSocioId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Socio no encontrado con id: " +
                                        request.getSocioId()
                        )
                );

        if (!Boolean.TRUE.equals(socio.getEstado())) {
            throw new ReglaNegocioException(
                    "No se puede registrar un préstamo para un socio inactivo"
            );
        }

        Prestamo prestamo = new Prestamo();

        LocalDateTime fecha = LocalDateTime.now();

        prestamo.setFecha(fecha);
        prestamo.setFechaDevolucionPrevista(
                fecha.toLocalDate().plusDays(7)
        );
        prestamo.setEstado(EstadoPrestamo.REGISTRADO);
        prestamo.setSocio(socio);

        BigDecimal total = BigDecimal.ZERO;

        for (DetallePrestamoRequestDTO item : request.getDetalles()) {

            Libro libro = libroRepository.findById(item.getLibroId())
                    .orElseThrow(() ->
                            new RecursoNoEncontradoException(
                                    "Libro no encontrado con id: " +
                                            item.getLibroId()
                            )
                    );

            if (!Boolean.TRUE.equals(libro.getEstado())) {
                throw new ReglaNegocioException(
                        "El libro " + libro.getTitulo() +
                                " se encuentra inactivo"
                );
            }

            if (libro.getStock() < item.getCantidad()) {
                throw new ReglaNegocioException(
                        "Stock insuficiente para " +
                                libro.getTitulo() +
                                ". Disponible: " +
                                libro.getStock() +
                                ", solicitado: " +
                                item.getCantidad()
                );
            }

            BigDecimal costoUnitario =
                    libro.getCostoReposicion();

            BigDecimal subtotal =
                    costoUnitario.multiply(
                            BigDecimal.valueOf(item.getCantidad())
                    );

            DetallePrestamo detalle =
                    new DetallePrestamo();

            detalle.setLibro(libro);
            detalle.setCantidad(item.getCantidad());
            detalle.setCostoUnitario(costoUnitario);
            detalle.setSubtotal(subtotal);

            prestamo.agregarDetalle(detalle);

            total = total.add(subtotal);

            libro.setStock(
                    libro.getStock() - item.getCantidad()
            );
        }

        prestamo.setTotalValorizado(total);

        Prestamo guardado =
                prestamoRepository.save(prestamo);

        log.info(
                "Finalizando registro de préstamo id={}, total={}, duracionMs={}",
                guardado.getId(),
                total,
                System.currentTimeMillis() - inicio
        );

        return convertirResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoResponseDTO buscar(Long id) {

        Prestamo prestamo =
                prestamoRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Préstamo no encontrado con id: " +
                                                id
                                )
                        );

        return convertirResponse(prestamo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> listar() {

        return prestamoRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponseDTO> buscarPrestamos(
            Long socioId,
            EstadoPrestamo estado,
            LocalDate desde,
            LocalDate hasta,
            String ordenarPor,
            String direccion) {

        long inicio = System.currentTimeMillis();

        if (desde != null && hasta != null &&
                desde.isAfter(hasta)) {

            throw new ReglaNegocioException(
                    "La fecha desde no puede ser posterior a la fecha hasta"
            );
        }

        String campoOrden = ordenarPor == null ||
                ordenarPor.isBlank()
                ? "fecha"
                : ordenarPor;

        if (!CAMPOS_ORDENABLES.contains(campoOrden)) {
            throw new ReglaNegocioException(
                    "Campo de ordenamiento no permitido: " +
                            campoOrden
            );
        }

        String direccionNormalizada =
                direccion == null ||
                        direccion.isBlank()
                        ? "desc"
                        : direccion.toLowerCase();

        Sort.Direction sortDirection;

        if ("asc".equals(direccionNormalizada)) {
            sortDirection = Sort.Direction.ASC;
        } else if ("desc".equals(direccionNormalizada)) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new ReglaNegocioException(
                    "La dirección debe ser asc o desc"
            );
        }

        Sort sort =
                Sort.by(sortDirection, campoOrden);

        LocalDateTime fechaDesde =
                desde == null
                        ? null
                        : desde.atStartOfDay();

        LocalDateTime fechaHasta =
                hasta == null
                        ? null
                        : hasta.atTime(LocalTime.MAX);

        List<Prestamo> prestamos =
                prestamoRepository.buscar(
                        socioId,
                        estado,
                        fechaDesde,
                        fechaHasta,
                        sort
                );

        List<PrestamoResponseDTO> respuesta =
                prestamos.stream()
                        .map(this::convertirResponse)
                        .toList();

        log.info(
                "Búsqueda de préstamos finalizada filas={}, duracionMs={}",
                respuesta.size(),
                System.currentTimeMillis() - inicio
        );

        return respuesta;
    }

    @Override
    @Transactional
    public PrestamoResponseDTO devolver(Long id) {

        long inicio = System.currentTimeMillis();

        Prestamo prestamo =
                prestamoRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Préstamo no encontrado con id: " +
                                                id
                                )
                        );

        if (prestamo.getEstado() !=
                EstadoPrestamo.REGISTRADO) {

            throw new ReglaNegocioException(
                    "Solo se puede devolver un préstamo en estado REGISTRADO"
            );
        }

        for (DetallePrestamo detalle :
                prestamo.getDetalles()) {

            Libro libro = detalle.getLibro();

            libro.setStock(
                    libro.getStock() +
                            detalle.getCantidad()
            );
        }

        prestamo.setEstado(
                EstadoPrestamo.DEVUELTO
        );

        prestamo.setFechaDevolucionReal(
                LocalDate.now()
        );

        Prestamo actualizado =
                prestamoRepository.save(prestamo);

        log.info(
                "Préstamo id={} devuelto, duracionMs={}",
                id,
                System.currentTimeMillis() - inicio
        );

        return convertirResponse(actualizado);
    }

    @Override
    @Transactional
    public PrestamoResponseDTO anular(Long id) {

        long inicio = System.currentTimeMillis();

        Prestamo prestamo =
                prestamoRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Préstamo no encontrado con id: " +
                                                id
                                )
                        );

        if (prestamo.getEstado() !=
                EstadoPrestamo.REGISTRADO) {

            throw new ReglaNegocioException(
                    "Solo se puede anular un préstamo en estado REGISTRADO"
            );
        }

        for (DetallePrestamo detalle :
                prestamo.getDetalles()) {

            Libro libro = detalle.getLibro();

            libro.setStock(
                    libro.getStock() +
                            detalle.getCantidad()
            );
        }

        prestamo.setEstado(
                EstadoPrestamo.ANULADO
        );

        Prestamo actualizado =
                prestamoRepository.save(prestamo);

        log.info(
                "Préstamo id={} anulado, duracionMs={}",
                id,
                System.currentTimeMillis() - inicio
        );

        return convertirResponse(actualizado);
    }

    private PrestamoResponseDTO convertirResponse(
            Prestamo prestamo) {

        List<DetallePrestamoResponseDTO> detalles =
                prestamo.getDetalles()
                        .stream()
                        .map(detalle ->
                                new DetallePrestamoResponseDTO(
                                        detalle.getLibro().getId(),
                                        detalle.getLibro().getTitulo(),
                                        detalle.getCantidad(),
                                        detalle.getCostoUnitario(),
                                        detalle.getSubtotal()
                                )
                        )
                        .toList();

        String socioNombre =
                prestamo.getSocio().getNombres() +
                        " " +
                        prestamo.getSocio().getApellidos();

        return new PrestamoResponseDTO(
                prestamo.getId(),
                prestamo.getFecha(),
                prestamo.getFechaDevolucionPrevista(),
                prestamo.getFechaDevolucionReal(),
                prestamo.getEstado().name(),
                prestamo.getSocio().getId(),
                socioNombre,
                prestamo.getTotalValorizado(),
                detalles
        );
    }
}