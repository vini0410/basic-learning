package com.learning.virtual;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Duration;

/**
 * High-Throughput Echo Server (Simulação)
 * 
 * Objetivo: Lidar com 10.000 conexões simultâneas usando Virtual Threads.
 */
public class HighThroughputEchoServer {

    private static final int NUM_CONNECTIONS = 10_000;
    private static final AtomicInteger ACTIVE_CONNECTIONS = new AtomicInteger(0);
    private static final AtomicInteger COMPLETED_REQUESTS = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Servidor de Alta Vazão Iniciado ---");
        long start = System.currentTimeMillis();

        // No Java 21+, para alta vazão de I/O, NÃO use pool de threads.
        // Use o executor que cria uma nova Virtual Thread por tarefa.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            for (int i = 0; i < NUM_CONNECTIONS; i++) {
                final int connectionId = i;
                executor.submit(() -> handleConnection(connectionId));
            }
            
            // O bloco try-with-resources chama o executor.close() automaticamente,
            // que por sua vez espera que todas as Virtual Threads terminem.
        }

        long end = System.currentTimeMillis();
        System.out.println("\n>>> TODAS AS " + COMPLETED_REQUESTS.get() + " CONEXÕES FINALIZADAS!");
        System.out.println(">>> Tempo Total para processar 10k conexões: " + (end - start) + "ms");
    }

    private static void handleConnection(int id) {
        int active = ACTIVE_CONNECTIONS.incrementAndGet();
        if (id % 1000 == 0) {
            System.out.println("[Server] Conexão #" + id + " recebida. Ativas agora: " + active);
        }

        try {
            // Simula o tempo de I/O de rede (espera por dados)
            // Em uma Platform Thread, isso travaria 1MB de RAM por 2 segundos.
            // Em uma Virtual Thread, ela é desmontada e a CPU fica livre.
            Thread.sleep(Duration.ofSeconds(2));
            
            COMPLETED_REQUESTS.incrementAndGet();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            ACTIVE_CONNECTIONS.decrementAndGet();
        }
    }
}
