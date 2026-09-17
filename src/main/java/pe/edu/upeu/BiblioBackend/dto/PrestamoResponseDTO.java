package pe.edu.upeu.BiblioBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrestamoResponseDTO {

    private Long id;
    private LocalDateTime fecha;
    private LocalDate fechaDevolucionPrevista;
    private LocalDate fechaDevolucionReal;
    private String estado;
    private Long socioId;
    private String socioNombre;
    private BigDecimal totalValorizado;
    private List<DetallePrestamoResponseDTO> detalles;
}