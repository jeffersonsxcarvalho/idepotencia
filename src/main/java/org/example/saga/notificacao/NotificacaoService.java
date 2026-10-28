package org.example.saga.notificacao;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class NotificacaoService {
    private final Set<Notificacao> enviadas = ConcurrentHashMap.newKeySet();

    public void confirmarCompra(UUID pedidoId) {
        enviar(pedidoId, Tipo.CONFIRMACAO);
    }

    public void notificarCancelamento(UUID pedidoId) {
        enviar(pedidoId, Tipo.CANCELAMENTO);
    }

    private void enviar(UUID pedidoId, Tipo tipo) {
        Notificacao notificacao = new Notificacao(pedidoId, tipo);

        if(enviadas.add(notificacao)) {
            System.out.println("Notificação enviada: " + notificacao);
        }
    }

    private enum Tipo {
        CONFIRMACAO,
        CANCELAMENTO
    }

    private record Notificacao(UUID pedidoId, Tipo tipo) {}
}
