package com.learning.hybrid;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

/**
 * Projeto Híbrido 1: Web Crawler Inteligente
 * Integra Virtual Threads, CompletableFuture e ReadWriteLock.
 */
public class WebCrawler {

    // Índice de palavras global (Shared Resource)
    private final Map<String, Integer> wordIndex = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Simula o download do conteúdo de uma URL usando I/O bloqueante.
     * Virtual Threads brilham aqui, pois o bloqueio não trava a Carrier Thread.
     */
    private String fetchContent(String url) {
        System.out.printf("[%s] Baixando: %s%n", Thread.currentThread(), url);
        try {
            // Simula latência de rede variável
            Thread.sleep(Duration.ofMillis(ThreadLocalRandom.current().nextInt(500, 1500)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "conteúdo da página " + url + " sobre java concorrência threads performance e java";
    }

    /**
     * Processa o conteúdo de forma assíncrona usando CompletableFuture.
     */
    private CompletableFuture<Void> processContentAsync(String content) {
        return CompletableFuture.runAsync(() -> {
            String[] words = content.toLowerCase().split("\\s+");
            for (String word : words) {
                updateIndex(word);
            }
        });
    }

    /**
     * Atualiza o índice global usando Write Lock para garantir consistência.
     */
    private void updateIndex(String word) {
        lock.writeLock().lock();
        try {
            wordIndex.merge(word, 1, Integer::sum);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Lê o índice global usando Read Lock (permite múltiplas leituras simultâneas).
     */
    public void printTopWords(int limit) {
        lock.readLock().lock();
        try {
            System.out.println("\n--- Top Palavras Encontradas ---");
            wordIndex.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(limit)
                    .forEach(e -> System.out.printf("%s: %d%n", e.getKey(), e.getValue()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void execute() {
        // Simulando 50 URLs para crawl
        List<String> urls = IntStream.range(1, 51)
                .mapToObj(i -> "https://api.exemplo.com/site-" + i)
                .toList();

        // Usando Virtual Threads para o I/O massivo
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = urls.stream()
                    .map(url -> CompletableFuture.supplyAsync(() -> fetchContent(url), executor)
                            .thenCompose(this::processContentAsync))
                    .toList();

            // Aguarda a conclusão de todos os crawlers
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando Web Crawler Inteligente...");
        long start = System.currentTimeMillis();

        WebCrawler crawler = new WebCrawler();
        crawler.execute();
        crawler.printTopWords(5);

        long end = System.currentTimeMillis();
        System.out.printf("%nTempo total de execução: %dms%n", (end - start));
    }
}
