package org.example.saga.pedido;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CriarPedidoRequest(
        @NotBlank String clienteId,
        @NotBlank String produtoId,
        @Positive int quantidade,
        @NotNull @DecimalMin("0.01") BigDecimal valorTotal
        ) {
}
