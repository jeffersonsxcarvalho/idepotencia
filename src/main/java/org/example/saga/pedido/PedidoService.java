package org.example.saga.pedido;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PedidoService {
    private final Map<UUID, Pedido> pedidos = new ConcurrentHashMap<>();

    public Pedido criar(CriarPedidoRequest request) {
        Pedido pedido = new Pedido(
                UUID.randomUUID(),
                request.clienteId(),
                request.produtoId(),
                request.quantidade(),
                request.valorTotal()
        );

        pedidos.put(pedido.getId(), pedido);
        return pedido;
    }

    public Optional<Pedido> buscarPorId(UUID id) {
        return Optional.ofNullable(pedidos.get(id));
    }
}
