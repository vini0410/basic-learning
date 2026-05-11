package com.learning.cache;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * [10] High Performance Cache
 * Implementação de um cache LRU (Least Recently Used) thread-safe com métricas.
 * Útil para entender como evitar acessos repetidos a fontes lentas (DB/API).
 */
public class HighPerformanceCache<K, V> {

    private final int capacity;
    private final Map<K, V> cache;
    
    // Métricas
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);

    public HighPerformanceCache(int capacity) {
        this.capacity = capacity;
        // LinkedHashMap com accessOrder=true move os itens acessados para o final
        this.cache = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > HighPerformanceCache.this.capacity;
            }
        };
    }

    public synchronized V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            hits.incrementAndGet();
        } else {
            misses.incrementAndGet();
        }
        return value;
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    public synchronized int size() {
        return cache.size();
    }

    public void printMetrics() {
        long total = hits.get() + misses.get();
        double hitRatio = total == 0 ? 0 : (hits.get() * 100.0 / total);
        System.out.printf("Cache Metrics: Capacity: %d | Size: %d | Hits: %d | Misses: %d | Hit Ratio: %.2f%%%n",
                capacity, size(), hits.get(), misses.get(), hitRatio);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== [10] High Performance Cache (LRU) ===");
        
        HighPerformanceCache<Integer, String> lruCache = new HighPerformanceCache<>(5);

        // Simulando carga de trabalho
        System.out.println("Populando cache...");
        for (int i = 1; i <= 5; i++) {
            lruCache.put(i, "Value-" + i);
        }

        System.out.println("Acessando itens (1 a 5)...");
        for (int i = 1; i <= 5; i++) {
            lruCache.get(i);
        }

        System.out.println("Adicionando novo item (6), isso deve remover o 1 (LRU)...");
        lruCache.put(6, "Value-6");

        System.out.println("Tentando acessar o item 1 (deve ser MISS): " + lruCache.get(1));
        System.out.println("Tentando acessar o item 2 (deve ser HIT): " + lruCache.get(2));

        lruCache.printMetrics();

        // Teste de Alta Performance (Múltiplas Threads)
        System.out.println("\n--- Teste de Stress com Múltiplas Threads ---");
        int numThreads = 10;
        int requestsPerThread = 1000;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                Random rand = new Random();
                for (int j = 0; j < requestsPerThread; j++) {
                    int key = rand.nextInt(10); // Acessando chaves de 0 a 9 em um cache de 5
                    if (lruCache.get(key) == null) {
                        lruCache.put(key, "Value-" + key);
                    }
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) t.join();
        lruCache.printMetrics();
    }
}
