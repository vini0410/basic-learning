package com.learning.executors;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.List;
import java.util.ArrayList;

/**
 * Big Data File Processor - ForkJoin Framework
 * 
 * Objetivo: Usar Divisão e Conquista (Recursividade) para contar erros.
 * O ForkJoinPool é otimizado para tarefas que podem ser quebradas em partes menores.
 */
public class ForkJoinLogProcessor {

    private static final int TOTAL_LINES = 1_000_000;
    private static final List<String> LOG_FILE = new ArrayList<>(TOTAL_LINES);

    static {
        for (int i = 0; i < TOTAL_LINES; i++) {
            LOG_FILE.add(Math.random() > 0.95 ? "ERROR: Falha" : "INFO: OK");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Log Processor: ForkJoinPool (Dividir e Conquistar) ---");

        // O ForkJoinPool.commonPool() é o pool padrão do Java (usado em Streams)
        ForkJoinPool pool = new ForkJoinPool(); 

        long start = System.currentTimeMillis();

        // Criamos a tarefa principal (o arquivo inteiro)
        LogTask mainTask = new LogTask(LOG_FILE, 0, TOTAL_LINES);

        // Submetemos ao pool e aguardamos o resultado final
        long totalErrors = pool.invoke(mainTask);

        long end = System.currentTimeMillis();
        
        System.out.println(">>> Total de erros encontrados: " + totalErrors);
        System.out.println(">>> Processamento completo em " + (end - start) + "ms");
        
        pool.shutdown();
    }

    /**
     * RecursiveTask: Uma tarefa que retorna um resultado (Long).
     */
    static class LogTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 100_000; // Limite para parar de dividir
        private final List<String> lines;
        private final int start;
        private final int end;

        public LogTask(List<String> lines, int start, int end) {
            this.lines = lines;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;

            // Se o pedaço for pequeno o suficiente, processa sequencialmente
            if (length <= THRESHOLD) {
                return processSequentially();
            }

            // Caso contrário, divide em dois pedaços menores (Recursividade)
            int mid = start + (length / 2);
            LogTask leftTask = new LogTask(lines, start, mid);
            LogTask rightTask = new LogTask(lines, mid, end);

            // fork() manda a tarefa da esquerda para o pool de forma assíncrona
            leftTask.fork();

            // A tarefa da direita é calculada na thread atual
            long rightResult = rightTask.compute();

            // join() espera o resultado da tarefa da esquerda
            long leftResult = leftTask.join();

            return leftResult + rightResult;
        }

        private long processSequentially() {
            long count = 0;
            for (int i = start; i < end; i++) {
                if (lines.get(i).startsWith("ERROR")) {
                    count++;
                }
            }
            return count;
        }
    }
}
