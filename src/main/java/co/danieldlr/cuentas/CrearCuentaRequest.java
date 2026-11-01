package co.danieldlr.cuentas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CrearCuentaRequest(
        @NotBlank String titular,
        @NotNull @PositiveOrZero BigDecimal saldoInicial) {
}
