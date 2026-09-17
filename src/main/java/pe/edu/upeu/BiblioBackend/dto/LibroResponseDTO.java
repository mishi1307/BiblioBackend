package pe.edu.upeu.BiblioBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LibroResponseDTO {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private BigDecimal costoReposicion;
    private Integer stock;
    private Boolean estado;
    private Long generoId;
    private String generoNombre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}