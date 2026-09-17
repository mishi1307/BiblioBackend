package pe.edu.upeu.BiblioBackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LibroRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 150, message = "El título debe tener entre 3 y 150 caracteres")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(min = 3, max = 120, message = "El autor debe tener entre 3 y 120 caracteres")
    private String autor;

    @NotBlank(message = "El ISBN es obligatorio")
    @Pattern(
            regexp = "^(\\d{10}|\\d{13})$",
            message = "El ISBN debe contener 10 o 13 dígitos"
    )
    private String isbn;

    @NotNull(message = "El costo de reposición es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo de reposición debe ser mayor que cero")
    private BigDecimal costoReposicion;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    @NotNull(message = "El género es obligatorio")
    @Positive(message = "El identificador del género debe ser válido")
    private Long generoId;
}