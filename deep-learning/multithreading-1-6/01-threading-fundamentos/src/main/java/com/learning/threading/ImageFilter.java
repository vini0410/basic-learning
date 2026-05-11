package com.learning.threading;

import java.util.Arrays;

/**
 * Image Filter - Versão Sequencial (Original)
 */
public class ImageFilter {

    private static final int WIDTH = 10000;
    private static final int HEIGHT = 10000;
    private static final int[] IMAGE = new int[WIDTH * HEIGHT];

    static {
        for (int i = 0; i < IMAGE.length; i++) {
            IMAGE[i] = (int) (Math.random() * 256);
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Image Filter SEQUENCIAL ---");

        long startTime = System.currentTimeMillis();
        applyFilterSequential(IMAGE);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Tempo Sequencial: " + (endTime - startTime) + "ms");
    }

    private static void applyFilterSequential(int[] pixels) {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = processPixel(pixels[i]);
        }
    }

    private static int processPixel(int pixel) {
        for(int i = 0; i < 10; i++) {
             pixel = (pixel * 2) % 256;
        }
        return pixel;
    }
}
