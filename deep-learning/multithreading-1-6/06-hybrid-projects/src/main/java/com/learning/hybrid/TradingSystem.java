package com.learning.hybrid;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.DoubleStream;

/**
 * Projeto Híbrido 2: Sistema de Trading em Tempo Real
 * Integra Executors (Ingestão), ForkJoin (Cálculo de Indicadores) e CyclicBarrier (Sincronização).
 */
public class TradingSystem {

    // Simulação de base de dados histórica (1 milhão de preços)
    private static final double[] PRICE_HISTORY = DoubleStream.generate(() -> 100 + Math.random() * 50)
            .limit(1_000_000)
            .toArray();

    // Barreia para sincronizar 3 tipos de análises antes de tomar a decisão final
    private final CyclicBarrier strategyBarrier;
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private final Map<String, Double> results = new ConcurrentHashMap<>();

    public TradingSystem() {
        // A barreira aguarda 3 threads (Analistas) e executa a ação de Trading ao final
        this.strategyBarrier = new CyclicBarrier(3, this::evaluateTradeDecision);
    }

    /**
     * ForkJoin Task para calcular a média móvel de forma paralela (Divisão e Conquista).
     */
    static class MovingAverageTask extends RecursiveTask<Double> {
        private final double[] prices;
        private final int start, end;
        private static final int THRESHOLD = 10_000;

        MovingAverageTask(double[] prices, int start, int end) {
            this.prices = prices;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            if (end - start <= THRESHOLD) {
                double sum = 0;
                for (int i = start; i < end; i++) sum += prices[i];
                return sum / (end - start);
            }

            int mid = (start + end) / 2;
            MovingAverageTask left = new MovingAverageTask(prices, start, mid);
            MovingAverageTask right = new MovingAverageTask(prices, mid, end);
            left.fork();
            return (right.compute() + left.join()) / 2;
        }
    }

    /**
     * Analista que calcula um indicador específico e aguarda na barreira.
     */
    private void runAnalyst(String name, int windowSize) {
        System.out.printf("[%s] Iniciando análise do indicador: %s...%n", Thread.currentThread().getName(), name);
        
        // Simula o cálculo pesado usando ForkJoin
        Double average = forkJoinPool.invoke(new MovingAverageTask(PRICE_HISTORY, 0, windowSize));
        results.put(name, average);

        try {
            System.out.printf("[%s] Indicador %s calculado (%.2f). Aguardando outros analistas...%n", 
                    Thread.currentThread().getName(), name, average);
            strategyBarrier.await(); // Ponto de sincronização
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Ação executada pela CyclicBarrier quando todos os analistas terminam.
     */
    private void evaluateTradeDecision() {
        System.out.println("\n--- Decisão de Trading ---");
        double sma50 = results.get("SMA_50");
        double sma200 = results.get("SMA_200");
        double rsi = results.get("RSI"); // Simulado como um indicador extra

        System.out.printf("Indicadores: SMA50=%.2f | SMA200=%.2f | RSI=%.2f%n", sma50, sma200, rsi);
        
        if (sma50 > sma200 && rsi < 70) {
            System.out.println("SINAL: COMPRAR (Tendência de Alta)");
        } else if (sma50 < sma200) {
            System.out.println("SINAL: VENDER (Tendência de Baixa)");
        } else {
            System.out.println("SINAL: AGUARDAR (Mercado Lateral)");
        }
        System.out.println("--------------------------\n");
    }

    public void start() {
        // Executor para gerenciar a entrada das cotações
        try (ExecutorService ingestor = Executors.newFixedThreadPool(3)) {
            System.out.println("Recebendo stream de cotações e iniciando analistas...");
            
            ingestor.submit(() -> runAnalyst("SMA_50", 50_000));
            ingestor.submit(() -> runAnalyst("SMA_200", 200_000));
            ingestor.submit(() -> runAnalyst("RSI", 100_000));
            
            ingestor.shutdown();
            try {
                ingestor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        new TradingSystem().start();
    }
}
