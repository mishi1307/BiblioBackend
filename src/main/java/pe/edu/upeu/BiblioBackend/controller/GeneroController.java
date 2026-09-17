package pe.edu.upeu.BiblioBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.BiblioBackend.dto.GeneroRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.GeneroResponseDTO;
import pe.edu.upeu.BiblioBackend.service.service.GeneroService;

@RestController
@RequestMapping("/api/v1/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(
            GeneroService generoService) {

        this.generoService = generoService;
    }

    @GetMapping
    public ResponseEntity<Iterable<GeneroResponseDTO>> findAll() {

        return ResponseEntity.ok(
                generoService.readAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneroResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                generoService.read(id)
        );
    }

    @PostMapping
    public ResponseEntity<GeneroResponseDTO> create(
            @Valid @RequestBody GeneroRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(generoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneroResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody GeneroRequestDTO request) {

        return ResponseEntity.ok(
                generoService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        generoService.delete(id);

        return ResponseEntity.noContent().build();
    }
}