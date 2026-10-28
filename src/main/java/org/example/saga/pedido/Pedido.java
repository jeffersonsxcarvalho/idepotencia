package org.example.saga.pedido;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.UUID;

public class Pedido {
    private final UUID id;
    private final String clienteId;
    private final String produtoId;
    private final int quantidade;
    private final BigDecimal valorTotal;
    private StatusPedido status;

    public Pedido(
            UUID id,
            String clienteId,
            String produtoId,
            int quantidade,
            BigDecimal valorTotal
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.status = StatusPedido.PENDENTE;
    }

    public void marcarEstoqueReservado() {
        exigirStatus(StatusPedido.PENDENTE);
        status = StatusPedido.ESTOQUE_RESERVADO;
    }

    public void marcarPagamentoAprovado() {
        exigirStatus(StatusPedido.ESTOQUE_RESERVADO);
        status = StatusPedido.PAGAMENTO_APROVADO;
    }

    public void confirmar() {
        exigirStatus(StatusPedido.PAGAMENTO_APROVADO);
        status = StatusPedido.CONFIRMADO;
    }

    public void cancelar() {
        if(status != StatusPedido.PENDENTE
                && status != StatusPedido.ESTOQUE_RESERVADO) {
            throw new IllegalArgumentException(
                    "Este pedido não pode ser cancelado sem tratar o pagamento"
            );
        }
        status = StatusPedido.CANCELADO;
    }

    private void exigirStatus(StatusPedido esperado) {
        if(status != esperado) {
            throw new IllegalArgumentException(
                    "Status esperado: " + esperado + "; status atual: " + status
            );
        }
    }

    public UUID getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getProdutoId() {
        return produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }

}
