package pe.edu.upeu.BiblioBackend.service.service;

import pe.edu.upeu.BiblioBackend.dto.PrestamoRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.PrestamoResponseDTO;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;

import java.time.LocalDate;
import java.util.List;

public interface PrestamoService {

    PrestamoResponseDTO registrar(PrestamoRequestDTO request);

    PrestamoResponseDTO buscar(Long id);

    List<PrestamoResponseDTO> listar();

    List<PrestamoResponseDTO> buscarPrestamos(
            Long socioId,
            EstadoPrestamo estado,
            LocalDate desde,
            LocalDate hasta,
            String ordenarPor,
            String direccion
    );

    PrestamoResponseDTO devolver(Long id);

    PrestamoResponseDTO anular(Long id);
}