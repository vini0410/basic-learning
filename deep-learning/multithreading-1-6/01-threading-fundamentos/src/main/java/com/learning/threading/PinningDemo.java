package com.learning.threading;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.time.Duration;

/**
 * Demonstração de Thread Pinning
 * 
 * Forçamos apenas 1 Carrier Thread para tornar o efeito visível.
 */
public class PinningDemo {

    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        // FORÇAR APENAS 1 CARRIER THREAD (Apenas 1 núcleo real para o Java)
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        
        System.out.println("--- Iniciando Demo de Pinning (1 Carrier Thread disponível) ---");

        // TESTE 1: COM PINNING (synchronized)
        runTest("Teste COM Pinning (synchronized)", () -> testWithSynchronized());

        Thread.sleep(2000); // Pausa entre os testes

        // TESTE 2: SEM PINNING (ReentrantLock)
        runTest("Teste SEM Pinning (ReentrantLock)", () -> testWithLock());
    }

    private static void runTest(String name, Runnable test) {
        System.out.println("\n>>> " + name);
        long start = System.currentTimeMillis();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                int id = i;
                executor.submit(() -> {
                    System.out.println("Thread " + id + " iniciou.");
                    test.run();
                    System.out.println("Thread " + id + " terminou.");
                });
            }
        }
        
        long end = System.currentTimeMillis();
        System.out.println("Tempo Total: " + (end - start) + "ms");
    }

    // BLOQUEIA A CARRIER THREAD!
    private static synchronized void testWithSynchronized() {
        try {
            // Ao dormir dentro de um synchronized, a Virtual Thread 
            // "pina" (gruda) na Carrier Thread.
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // NÃO BLOQUEIA A CARRIER THREAD!
    private static void testWithLock() {
        lock.lock();
        try {
            // ReentrantLock é "Loom-friendly". A Virtual Thread 
            // é desmontada e a Carrier Thread fica livre.
            Thread.sleep(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
