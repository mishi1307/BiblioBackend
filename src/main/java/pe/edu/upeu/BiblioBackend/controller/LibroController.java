package pe.edu.upeu.BiblioBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.BiblioBackend.dto.LibroRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.LibroResponseDTO;
import pe.edu.upeu.BiblioBackend.service.service.LibroService;

@RestController
@RequestMapping("/api/v1/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(
            LibroService libroService) {

        this.libroService = libroService;
    }

    @GetMapping
    public ResponseEntity<Iterable<LibroResponseDTO>> findAll() {

        return ResponseEntity.ok(
                libroService.readAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                libroService.read(id)
        );
    }

    @PostMapping
    public ResponseEntity<LibroResponseDTO> create(
            @Valid @RequestBody LibroRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(libroService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody LibroRequestDTO request) {

        return ResponseEntity.ok(
                libroService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        libroService.delete(id);

        return ResponseEntity.noContent().build();
    }
}