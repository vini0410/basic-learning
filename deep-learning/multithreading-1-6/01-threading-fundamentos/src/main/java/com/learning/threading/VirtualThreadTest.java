package com.learning.threading;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Teste de Estresse: Platform Threads vs Virtual Threads
 * 
 * Objetivo: Tentar rodar 100.000 tarefas simples simultâneas.
 */
public class VirtualThreadTest {

    public static void main(String[] args) {
        int numTasks = 100_000;
        
        System.out.println("--- Teste de Estresse: " + numTasks + " tarefas ---");

        // Escolha qual teste rodar comentando/descomentando:
        
        // testPlatformThreads(numTasks); // CUIDADO: Isso pode travar seu PC ou dar OutOfMemory
        testVirtualThreads(numTasks);
    }

    private static void testPlatformThreads(int numTasks) {
        System.out.println("Iniciando com Platform Threads...");
        long start = System.currentTimeMillis();
        
        try (var executor = Executors.newCachedThreadPool()) {
            runTasks(executor, numTasks);
        }

        long end = System.currentTimeMillis();
        System.out.println("Tempo Total (Platform): " + (end - start) + "ms");
    }

    private static void testVirtualThreads(int numTasks) {
        System.out.println("Iniciando com Virtual Threads (Java 21+)...");
        long start = System.currentTimeMillis();
        
        // Executors.newVirtualThreadPerTaskExecutor() é a forma oficial de usar Loom
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            runTasks(executor, numTasks);
        }

        long end = System.currentTimeMillis();
        System.out.println("Tempo Total (Virtual): " + (end - start) + "ms");
    }

    private static void runTasks(java.util.concurrent.ExecutorService executor, int numTasks) {
        AtomicInteger count = new AtomicInteger();
        
        IntStream.range(0, numTasks).forEach(i -> {
            executor.submit(() -> {
                try {
                    // Simula uma espera de I/O (ex: esperando banco de dados)
                    Thread.sleep(Duration.ofSeconds(1));
                    count.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        
        System.out.println("Todas as tarefas submetidas. Aguardando conclusão...");
    }
}
