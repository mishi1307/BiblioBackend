package pe.edu.upeu.BiblioBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.BiblioBackend.dto.reporte.LibroMasPrestadoDTO;
import pe.edu.upeu.BiblioBackend.dto.reporte.PrestamoPorGeneroDTO;
import pe.edu.upeu.BiblioBackend.repository.PrestamoRepository;
import pe.edu.upeu.BiblioBackend.service.service.ReporteService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private static final Logger log =
            LoggerFactory.getLogger(ReporteServiceImpl.class);

    private final PrestamoRepository prestamoRepository;

    public ReporteServiceImpl(
            PrestamoRepository prestamoRepository) {

        this.prestamoRepository = prestamoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoPorGeneroDTO> prestamosPorGenero(
            LocalDate desde,
            LocalDate hasta) {

        long inicio = System.currentTimeMillis();

        validarRango(desde, hasta);

        LocalDateTime fechaDesde =
                desde == null
                        ? null
                        : desde.atStartOfDay();

        LocalDateTime fechaHasta =
                hasta == null
                        ? null
                        : hasta.atTime(LocalTime.MAX);

        List<PrestamoPorGeneroDTO> resultado =
                prestamoRepository.reportePrestamosPorGenero(
                        fechaDesde,
                        fechaHasta
                );

        log.info(
                "Reporte préstamos por género finalizado filas={}, duracionMs={}",
                resultado.size(),
                System.currentTimeMillis() - inicio
        );

        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroMasPrestadoDTO> librosMasPrestados(
            LocalDate desde,
            LocalDate hasta) {

        long inicio = System.currentTimeMillis();

        validarRango(desde, hasta);

        LocalDateTime fechaDesde =
                desde == null
                        ? null
                        : desde.atStartOfDay();

        LocalDateTime fechaHasta =
                hasta == null
                        ? null
                        : hasta.atTime(LocalTime.MAX);

        List<LibroMasPrestadoDTO> resultado =
                prestamoRepository.reporteLibrosMasPrestados(
                        fechaDesde,
                        fechaHasta
                );

        log.info(
                "Reporte libros más prestados finalizado filas={}, duracionMs={}",
                resultado.size(),
                System.currentTimeMillis() - inicio
        );

        return resultado;
    }

    private void validarRango(
            LocalDate desde,
            LocalDate hasta) {

        if (desde != null &&
                hasta != null &&
                desde.isAfter(hasta)) {

            throw new pe.edu.upeu.BiblioBackend.exception.ReglaNegocioException(
                    "La fecha desde no puede ser posterior a la fecha hasta"
            );
        }
    }
}