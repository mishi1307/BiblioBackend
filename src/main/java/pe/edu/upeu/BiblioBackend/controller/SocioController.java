package pe.edu.upeu.BiblioBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.BiblioBackend.dto.SocioRequestDTO;
import pe.edu.upeu.BiblioBackend.dto.SocioResponseDTO;
import pe.edu.upeu.BiblioBackend.service.service.SocioService;

@RestController
@RequestMapping("/api/v1/socios")
public class SocioController {

    private final SocioService socioService;

    public SocioController(
            SocioService socioService) {

        this.socioService = socioService;
    }

    @PostMapping
    public ResponseEntity<SocioResponseDTO> create(
            @Valid @RequestBody SocioRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(socioService.create(request));
    }

    @GetMapping
    public ResponseEntity<Iterable<SocioResponseDTO>> readAll() {

        return ResponseEntity.ok(
                socioService.readAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocioResponseDTO> read(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                socioService.read(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SocioResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody SocioRequestDTO request) {

        return ResponseEntity.ok(
                socioService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        socioService.delete(id);

        return ResponseEntity.noContent().build();
    }
}