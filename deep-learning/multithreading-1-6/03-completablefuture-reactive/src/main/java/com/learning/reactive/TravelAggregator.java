package com.learning.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * Travel Aggregator - Simulador de Consulta de Passagens
 * 
 * Objetivo: Consultar múltiplas "APIs" (com delays variados) e combinar os resultados.
 */
public class TravelAggregator {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        System.out.println("--- Travel Aggregator: Iniciando Consultas ---");
        long start = System.currentTimeMillis();

        // 1. Consultar duas companhias aéreas EM PARALELO (assíncrono)
        CompletableFuture<Integer> latamPrice = fetchPriceAsync("LATAM", 100, 500);
        CompletableFuture<Integer> azulPrice = fetchPriceAsync("AZUL", 200, 600);

        // 2. COMBINAR os dois resultados para achar o menor preço
        // thenCombine espera os dois terminarem e executa a lógica final
        CompletableFuture<Integer> bestPrice = latamPrice.thenCombine(azulPrice, (p1, p2) -> {
            System.out.println(">>> Combinando preços: LATAM=" + p1 + ", AZUL=" + p2);
            return Math.min(p1, p2);
        });

        // 3. ENCADEAR uma ação após encontrar o melhor preço
        CompletableFuture<String> finalReceipt = bestPrice.thenApply(price -> "Melhor Preço Encontrado: R$" + price);

        // 4. Tratamento de Erros e Finalização
        finalReceipt.whenComplete((res, ex) -> {
            if (ex != null) {
                System.err.println("Erro na consulta: " + ex.getMessage());
            } else {
                long end = System.currentTimeMillis();
                System.out.println("RESULTADO FINAL: " + res);
                System.out.println("Tempo Total da Operação: " + (end - start) + "ms");
            }
        });

        // Como CompletableFuture é não-bloqueante, precisamos impedir que o main() morra antes da resposta
        // Em um servidor real (Spring/Quarkus), isso não seria necessário.
        System.out.println("O Main continua livre para fazer outras coisas enquanto as APIs respondem...");
        finalReceipt.join(); // Agora bloqueamos só no final para ver o resultado
    }

    /**
     * Simula uma chamada de API assíncrona.
     */
    private static CompletableFuture<Integer> fetchPriceAsync(String company, int minDelay, int maxDelay) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int delay = minDelay + RANDOM.nextInt(maxDelay - minDelay);
                System.out.println("[" + company + "] Consultando preço... (delay: " + delay + "ms)");
                Thread.sleep(delay);
                
                // Simula um erro ocasional para vermos o tratamento
                if (RANDOM.nextDouble() > 0.95) {
                    throw new RuntimeException("Falha na API da " + company);
                }

                return 500 + RANDOM.nextInt(1000); // Preço aleatório
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
