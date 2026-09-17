package pe.edu.upeu.BiblioBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrestamoRequestDTO {

    @NotNull(message = "El socio es obligatorio")
    @Positive(message = "El identificador del socio debe ser válido")
    private Long socioId;

    @NotEmpty(message = "El préstamo debe contener al menos un detalle")
    @Valid
    private List<DetallePrestamoRequestDTO> detalles;
}