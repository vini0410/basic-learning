package com.learning.cap;

import java.util.*;

/**
 * [11] CAP Theorem Visualizer
 * Demonstra os trade-offs entre Consistência (C) e Disponibilidade (A)
 * quando ocorre uma Partição (P) na rede.
 */
public class CAPTheoremVisualizer {

    static class Node {
        String id;
        String value = "Initial";
        boolean isConnected = true;

        Node(String id) { this.id = id; }
    }

    enum Strategy { CP, AP }

    static class Cluster {
        List<Node> nodes = new ArrayList<>();
        Strategy strategy;

        Cluster(Strategy strategy) {
            this.strategy = strategy;
            nodes.add(new Node("Node-1"));
            nodes.add(new Node("Node-2"));
            nodes.add(new Node("Node-3"));
        }

        // Simula uma falha de rede (Nó 3 isolado)
        void createPartition() {
            nodes.get(2).isConnected = false;
            System.out.println("\n[!] PARTIÇÃO DE REDE: Node-3 está ISOLADO do cluster.");
        }

        // Tenta escrever um novo valor no cluster
        void write(String newValue) {
            System.out.println("\nTentando escrever valor: '" + newValue + "'");
            
            boolean canReachQuorum = nodes.stream().filter(n -> n.isConnected).count() >= 2;

            if (strategy == Strategy.CP) {
                if (!canReachQuorum) {
                    System.err.println("[CP] ERRO: Não há quorum. Escrita REJEITADA para garantir consistência.");
                } else {
                    System.out.println("[CP] SUCESSO: Escrita realizada nos nós conectados.");
                    nodes.stream().filter(n -> n.isConnected).forEach(n -> n.value = newValue);
                }
            } else if (strategy == Strategy.AP) {
                System.out.println("[AP] SUCESSO: Respondendo com disponibilidade. Escrita realizada nos nós que conseguimos alcançar.");
                nodes.stream().filter(n -> n.isConnected).forEach(n -> n.value = newValue);
            }
        }

        void printStatus() {
            System.out.println("Status do Cluster (" + strategy + "):");
            for (Node n : nodes) {
                System.out.printf("  %s: [Valor: %s] [Status: %s]%n", 
                    n.id, n.value, n.isConnected ? "ONLINE" : "OFFLINE/ISOLADO");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== [11] CAP Theorem Visualizer ===");

        // Cenário 1: CP (Consistency & Partition Tolerance)
        System.out.println("\n--- CENÁRIO CP (Ex: MongoDB/HBase) ---");
        Cluster cpCluster = new Cluster(Strategy.CP);
        cpCluster.printStatus();
        cpCluster.createPartition();
        cpCluster.write("Update-1");
        cpCluster.printStatus();

        // Cenário 2: AP (Availability & Partition Tolerance)
        System.out.println("\n--- CENÁRIO AP (Ex: Cassandra/DynamoDB) ---");
        Cluster apCluster = new Cluster(Strategy.AP);
        apCluster.printStatus();
        apCluster.createPartition();
        apCluster.write("Update-2");
        apCluster.printStatus();
        
        System.out.println("\nNote como no cenário AP, o Node-3 ficou com um dado ANTIGO (divergente),");
        System.out.println("mas o sistema continuou respondendo com sucesso!");
    }
}
