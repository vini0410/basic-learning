package com.learning.sharding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * [08] Sharding Simulator - Consistent Hashing
 * Demonstra como distribuir chaves entre múltiplos servidores de forma escalável.
 * Quando um servidor entra ou sai, o Consistent Hashing minimiza a movimentação de dados.
 */
public class ShardingSimulator {

    private final TreeMap<Long, String> circle = new TreeMap<>();
    private final int numberOfReplicas; // Número de nós virtuais para melhor distribuição

    public ShardingSimulator(int numberOfReplicas, Collection<String> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        for (String node : nodes) {
            addNode(node);
        }
    }

    public void addNode(String node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hash(node + ":" + i), node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hash(node + ":" + i));
        }
    }

    public String getNode(String key) {
        if (circle.isEmpty()) return null;
        long h = hash(key);
        if (!circle.containsKey(h)) {
            SortedMap<Long, String> tailMap = circle.tailMap(h);
            h = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(h);
    }

    // Função de Hash simples (MD5 convertida para Long)
    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
            // Usar os primeiros 8 bytes como um long para o anel de hash
            long res = 0;
            for (int i = 0; i < 8; i++) {
                res = (res << 8) | (bytes[i] & 0xff);
            }
            return res;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== [08] Sharding Simulator (Consistent Hashing) ===");
        
        List<String> servers = new ArrayList<>(Arrays.asList("Server-A", "Server-B", "Server-C"));
        ShardingSimulator simulator = new ShardingSimulator(100, servers);

        System.out.println("Distribuindo 10.000 chaves entre 3 servidores...");
        analyzeDistribution(simulator, 10_000);

        System.out.println("\n--- Adicionando 'Server-D' ---");
        simulator.addNode("Server-D");
        analyzeDistribution(simulator, 10_000);

        System.out.println("\n--- Removendo 'Server-A' ---");
        simulator.removeNode("Server-A");
        analyzeDistribution(simulator, 10_000);
    }

    private static void analyzeDistribution(ShardingSimulator simulator, int totalKeys) {
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < totalKeys; i++) {
            String server = simulator.getNode("user_key_" + i);
            counts.put(server, counts.getOrDefault(server, 0) + 1);
        }

        counts.forEach((server, count) -> {
            double percent = (count / (double) totalKeys) * 100;
            System.out.printf("%s: %d chaves (%.1f%%)%n", server, count, percent);
        });
    }
}
