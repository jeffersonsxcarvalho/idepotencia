package org.example.saga.estoque;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class EstoqueService {

    private final Map<String, Integer> quantidades =
            new HashMap<>(Map.of("produto-1", 10));

    private final Map<UUID, Reserva> reservas = new HashMap<>();

    public synchronized void reservar(UUID pedidoId, String produtoId, int quantidade) {
        if(quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        Reserva existente = reservas.get(pedidoId);

        if(existente != null) {
            if(
                    !existente.produtoId().equals(produtoId)
                    || existente.quantidade() != quantidade
            ) {
                throw new IllegalArgumentException(
                        "O pedido já possui uma reserva diferente."
                );
            }
            return;
        }

        int disponivel = quantidades.getOrDefault(produtoId, 0);
        if(disponivel < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente.");
        }

        quantidades.put(produtoId, disponivel - quantidade);
        reservas.put(pedidoId, new Reserva(produtoId, quantidade, true));
    }

    public synchronized void liberar(UUID pedidoId) {
        Reserva reserva = reservas.get(pedidoId);

        if(reserva == null || !reserva.ativa()){
            return;
        }

        quantidades.merge(reserva.produtoId(), reserva.quantidade(), Integer::sum);
        reservas.put(
                pedidoId,
                new Reserva(reserva.produtoId(), reserva.quantidade(), false)
        );
    }

    public synchronized int consultarDisponivel(String produtoId) {
        return quantidades.getOrDefault(produtoId, 0);
    }

    private record Reserva(
            String produtoId,
            int quantidade,
            boolean ativa
    ){}


}
