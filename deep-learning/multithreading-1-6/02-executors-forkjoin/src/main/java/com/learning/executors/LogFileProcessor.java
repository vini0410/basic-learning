package com.learning.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

/**
 * Big Data File Processor - Simulação com ExecutorService
 * 
 * Objetivo: Processar 1 milhão de linhas de log e contar quantos erros existem.
 * Usaremos um FixedThreadPool para gerenciar as threads de forma eficiente.
 */
public class LogFileProcessor {

    private static final int TOTAL_LINES = 1_000_000;
    private static final int BATCH_SIZE = 100_000; // 10 batches de 100k
    private static final List<String> LOG_FILE = new ArrayList<>(TOTAL_LINES);

    static {
        // Gera logs aleatórios (INFO ou ERROR)
        for (int i = 0; i < TOTAL_LINES; i++) {
            LOG_FILE.add(Math.random() > 0.95 ? "ERROR: Falha no sistema" : "INFO: Operação normal");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Log Processor: Fixed Thread Pool ---");

        // Criamos um pool com o número de processadores disponíveis
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long start = System.currentTimeMillis();

        // Dividindo o arquivo em lotes (batches) e submetendo as tarefas
        for (int i = 0; i < TOTAL_LINES; i += BATCH_SIZE) {
            final int startIdx = i;
            final int endIdx = Math.min(i + BATCH_SIZE, TOTAL_LINES);

            executor.submit(() -> {
                long errorCount = processBatch(startIdx, endIdx);
                System.out.println("Batch [" + startIdx + "-" + endIdx + "] finalizado. Erros encontrados: " + errorCount);
            });
        }

        // Importante: Mandamos o executor parar de aceitar novas tarefas e fechar
        executor.shutdown();
        
        // Espera até que todas as tarefas terminem ou o timeout ocorra
        if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
            long end = System.currentTimeMillis();
            System.out.println(">>> Processamento completo em " + (end - start) + "ms");
        } else {
            System.err.println("Tempo limite atingido!");
        }
    }

    private static long processBatch(int start, int end) {
        return LOG_FILE.subList(start, end).stream()
                .filter(line -> line.startsWith("ERROR"))
                .count();
    }
}
