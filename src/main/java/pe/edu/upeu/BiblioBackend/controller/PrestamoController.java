package pe.edu.upeu.BiblioBackend.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.BiblioBackend.dto.PrestamoRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.PrestamoResponseDTO;
import pe.edu.upeu.BiblioBackend.enums.EstadoPrestamo;
import pe.edu.upeu.BiblioBackend.service.service.PrestamoService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(
            PrestamoService prestamoService) {

        this.prestamoService = prestamoService;
    }

    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> registrar(
            @Valid @RequestBody PrestamoRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(prestamoService.registrar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                prestamoService.buscar(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<PrestamoResponseDTO>> listar() {

        return ResponseEntity.ok(
                prestamoService.listar()
        );
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PrestamoResponseDTO>> buscarPrestamos(

            @RequestParam(required = false)
            Long socioId,

            @RequestParam(required = false)
            EstadoPrestamo estado,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,

            @RequestParam(required = false, defaultValue = "fecha")
            String ordenarPor,

            @RequestParam(required = false, defaultValue = "desc")
            String direccion) {

        return ResponseEntity.ok(
                prestamoService.buscarPrestamos(
                        socioId,
                        estado,
                        desde,
                        hasta,
                        ordenarPor,
                        direccion
                )
        );
    }

    @PutMapping("/{id}/devolucion")
    public ResponseEntity<PrestamoResponseDTO> devolver(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                prestamoService.devolver(id)
        );
    }

    @PutMapping("/{id}/anulacion")
    public ResponseEntity<PrestamoResponseDTO> anular(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                prestamoService.anular(id)
        );
    }
}