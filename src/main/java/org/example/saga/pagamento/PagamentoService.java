package org.example.saga.pagamento;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class PagamentoService {
    private static final BigDecimal LIMITE_SIMULADO =
            BigDecimal.valueOf(1000);

    private final Map<UUID, Pagamento> pagamentos =
            new HashMap<>();

    public synchronized StatusPagamento processar(
            UUID pedidoId, BigDecimal valor
    ) {
        if(valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }

        Pagamento existente = pagamentos.get(pedidoId);

        if(existente != null) {
            if(existente.valor().compareTo(valor) != 0) {
                throw new IllegalArgumentException("O pedido já possui um pagamento com outro valor.");
            }
            return existente.status();
        }

        StatusPagamento status = valor.compareTo(LIMITE_SIMULADO) <= 0
                ? StatusPagamento.APROVADO
                : StatusPagamento.RECUSADO;

        pagamentos.put(pedidoId, new Pagamento(valor, status));
        return status;
    }

    private record Pagamento(BigDecimal valor, StatusPagamento status){}
}
