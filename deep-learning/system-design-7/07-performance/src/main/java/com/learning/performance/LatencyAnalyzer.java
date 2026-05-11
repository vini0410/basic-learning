package com.learning.performance;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * [07] Latency Analyzer Tool
 * Esta ferramenta simula um pipeline de processamento e mede latência e vazão.
 * Útil para observar como o aumento da carga afeta os percentis de cauda (P95/P99).
 */
public class LatencyAnalyzer {

    private static final int TOTAL_REQUESTS = 10_000;
    private static final int SIMULATED_WORK_MS = 100; // Tempo médio de processamento
    private static final int JITTER_MS = 20; // Variação aleatória (Jitter)

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== [07] Latency Analyzer Tool ===");
        System.out.println("Total de Requisições: " + TOTAL_REQUESTS);
        System.out.println("Tempo Médio Estimado: " + SIMULATED_WORK_MS + "ms (+/- " + JITTER_MS + "ms)");
        System.out.println("--------------------------------------------------");

        // Testar com diferentes níveis de concorrência
        int[] threadCounts = {1, 10, 50, 100, 500};

        for (int threads : threadCounts) {
            runBenchmark(threads);
        }
    }

    private static void runBenchmark(int numThreads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>(TOTAL_REQUESTS));
        LongAdder completedRequests = new LongAdder();
        
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            executor.submit(() -> {
                long taskStart = System.nanoTime();
                
                simulateWork();
                
                long taskEnd = System.nanoTime();
                latencies.add((taskEnd - taskStart) / 1_000_000); // ms
                completedRequests.increment();
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        
        long endTime = System.currentTimeMillis();
        long totalTimeMs = endTime - startTime;
        double throughput = (double) TOTAL_REQUESTS / (totalTimeMs / 1000.0);

        printMetrics(numThreads, latencies, totalTimeMs, throughput);
    }

    private static void simulateWork() {
        try {
            // Simula um processamento com variabilidade (normal em sistemas reais)
            long jitter = ThreadLocalRandom.current().nextLong(-JITTER_MS, JITTER_MS);
            
            // Simula uma "cauda longa" ocasional (problemas de rede, GC, etc)
            if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                jitter += 200; // 5% das requisições são lentas
            }
            
            Thread.sleep(Math.max(0, SIMULATED_WORK_MS + jitter));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void printMetrics(int threads, List<Long> latencies, long totalTime, double throughput) {
        List<Long> sorted = latencies.stream().sorted().collect(Collectors.toList());
        
        if (sorted.isEmpty()) return;

        long p50 = sorted.get((int) (sorted.size() * 0.50));
        long p95 = sorted.get((int) (sorted.size() * 0.95));
        long p99 = sorted.get((int) (sorted.size() * 0.99));
        long avg = (long) latencies.stream().mapToLong(Long::longValue).average().orElse(0);

        System.out.printf("Threads: %3d | Vazão: %7.2f req/s | Média: %4dms | P50: %4dms | P95: %4dms | P99: %4dms%n",
                threads, throughput, avg, p50, p95, p99);
    }
}
