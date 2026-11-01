package co.danieldlr.cuentas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotNull Long origenId,
        @NotNull Long destinoId,
        @NotNull @Positive BigDecimal monto) {
}
