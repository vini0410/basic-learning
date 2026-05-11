package com.learning.loadbalancer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * [09] Load Balancer Lab
 * Simula diferentes algoritmos de balanceamento de carga para distribuir requisições
 * entre um pool de servidores.
 */
public class LoadBalancerLab {

    // Representação de um servidor
    static class Server {
        String id;
        AtomicInteger activeConnections = new AtomicInteger(0);

        Server(String id) {
            this.id = id;
        }

        void handleRequest() {
            activeConnections.incrementAndGet();
            // Simula um tempo de processamento variável
            try { Thread.sleep(new Random().nextInt(50)); } catch (InterruptedException ignored) {}
            activeConnections.decrementAndGet();
        }
    }

    // Estratégia de Balanceamento
    interface LoadBalancer {
        Server getNextServer(List<Server> servers);
    }

    // Algoritmo 1: Round Robin (Sequencial)
    static class RoundRobinLB implements LoadBalancer {
        private final AtomicInteger index = new AtomicInteger(0);
        @Override
        public Server getNextServer(List<Server> servers) {
            int next = index.getAndIncrement() % servers.size();
            return servers.get(Math.abs(next));
        }
    }

    // Algoritmo 2: Random (Aleatório)
    static class RandomLB implements LoadBalancer {
        private final Random random = new Random();
        @Override
        public Server getNextServer(List<Server> servers) {
            return servers.get(random.nextInt(servers.size()));
        }
    }

    // Algoritmo 3: Least Connections (Menos Conexões Ativas)
    static class LeastConnectionsLB implements LoadBalancer {
        @Override
        public Server getNextServer(List<Server> servers) {
            return servers.stream()
                    .min(Comparator.comparingInt(s -> s.activeConnections.get()))
                    .orElse(servers.get(0));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== [09] Load Balancer Lab ===");
        
        List<Server> servers = Arrays.asList(
                new Server("Server-1"),
                new Server("Server-2"),
                new Server("Server-3"),
                new Server("Server-4")
        );

        System.out.println("Testando Round Robin:");
        runTest(new RoundRobinLB(), servers, 1000);

        System.out.println("\nTestando Random:");
        runTest(new RandomLB(), servers, 1000);

        System.out.println("\nTestando Least Connections:");
        runTest(new LeastConnectionsLB(), servers, 1000);
    }

    private static void runTest(LoadBalancer lb, List<Server> servers, int totalRequests) throws InterruptedException {
        Map<String, Integer> counts = new HashMap<>();
        for (Server s : servers) counts.put(s.id, 0);

        // Simulando requisições em paralelo para ver o efeito do Least Connections
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < totalRequests; i++) {
            threads.add(new Thread(() -> {
                Server s = lb.getNextServer(servers);
                synchronized (counts) {
                    counts.put(s.id, counts.get(s.id) + 1);
                }
                s.handleRequest();
            }));
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        counts.forEach((id, count) -> {
            System.out.printf("%s: %d requisições (%.1f%%)%n", id, count, (count * 100.0 / totalRequests));
        });
    }
}
