package org.example.saga;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.saga.estoque.EstoqueService;
import org.example.saga.notificacao.NotificacaoService;
import org.example.saga.pagamento.PagamentoService;
import org.example.saga.pagamento.StatusPagamento;
import org.example.saga.pedido.Pedido;
import org.example.saga.pedido.PedidoService;
import org.example.saga.pedido.StatusPedido;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PedidoSaga {
    private final PedidoService pedidoService;
    private final EstoqueService estoqueService;
    private final PagamentoService pagamentoService;
    private final NotificacaoService notificacaoService;

    @Inject

    public PedidoSaga(
            PedidoService pedidoService,
            EstoqueService estoqueService,
            PagamentoService pagamentoService,
            NotificacaoService notificacaoService
    ) {
        this.pedidoService = pedidoService;
        this.estoqueService = estoqueService;
        this.pagamentoService = pagamentoService;
        this.notificacaoService = notificacaoService;
    }

    public Optional<Pedido> processar(UUID pedidoId) {
        Optional<Pedido> resultado = pedidoService.buscarPorId(pedidoId);

        if(resultado.isEmpty()) {
            return Optional.empty();
        }

        Pedido pedido = resultado.get();

        if(pedido.getStatus() == StatusPedido.CONFIRMADO
                || pedido.getStatus() == StatusPedido.CANCELADO) {
            return Optional.of(pedido);
        }

        if(pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalArgumentException("O pedido não está pendente.");
        }

        estoqueService.reservar(
                pedido.getId(),
                pedido.getProdutoId(),
                pedido.getQuantidade()
        );

        pedido.marcarEstoqueReservado();

        StatusPagamento pagamento =
                pagamentoService.processar(pedido.getId(), pedido.getValorTotal());

        if(pagamento == StatusPagamento.APROVADO) {
            pedido.marcarPagamentoAprovado();
            pedido.confirmar();
            notificacaoService.confirmarCompra(pedido.getId());
        } else {
            estoqueService.liberar(pedido.getId());
            pedido.cancelar();
            notificacaoService.notificarCancelamento(pedido.getId());
        }

        return Optional.of(pedido);
    }
}
