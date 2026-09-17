package pe.edu.upeu.BiblioBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetallePrestamoResponseDTO {

    private Long libroId;
    private String titulo;
    private Integer cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
}