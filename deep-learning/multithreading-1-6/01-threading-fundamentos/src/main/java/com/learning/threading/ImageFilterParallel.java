package com.learning.threading;

import java.util.Arrays;

/**
 * Image Filter - Versão Paralela (Atualizada)
 */
public class ImageFilterParallel {

    private static final int WIDTH = 10000;
    private static final int HEIGHT = 10000;
    private static final int[] IMAGE = new int[WIDTH * HEIGHT];

    static {
        for (int i = 0; i < IMAGE.length; i++) {
            IMAGE[i] = (int) (Math.random() * 256);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Image Filter PARALELO ---");
        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Processadores disponíveis: " + numThreads);

        Thread[] threads = new Thread[numThreads];
        int segmentSize = IMAGE.length / numThreads;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            final int startIdx = i * segmentSize;
            final int endIdx = (i == numThreads - 1) ? IMAGE.length : (i + 1) * segmentSize;

            threads[i] = new Thread(new PixelProcessor(IMAGE, startIdx, endIdx));
            threads[i].start();
        }

        // Barreira manual de sincronização
        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Tempo Paralelo (" + numThreads + " threads): " + (endTime - startTime) + "ms");
    }

    private static class PixelProcessor implements Runnable {
        private final int[] pixels;
        private final int startIdx;
        private final int endIdx;

        public PixelProcessor(int[] pixels, int startIdx, int endIdx) {
            this.pixels = pixels;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        public void run() {
            for (int i = startIdx; i < endIdx; i++) {
                pixels[i] = processPixel(pixels[i]);
            }
        }
    }

    private static int processPixel(int pixel) {
        for(int i = 0; i < 10; i++) {
             pixel = (pixel * 2) % 256;
        }
        return pixel;
    }
}
