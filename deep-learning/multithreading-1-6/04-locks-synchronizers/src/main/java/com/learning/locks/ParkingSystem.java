package com.learning.locks;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * Multi-Level Parking System
 * 
 * Objetivo: Usar Semaphore para vagas e ReentrantLock para entrada/saída.
 */
public class ParkingSystem {

    private static final int TOTAL_SPOTS = 5; // Simula 5 vagas totais
    private static final Semaphore spots = new Semaphore(TOTAL_SPOTS);
    private static final ReentrantLock gateLock = new ReentrantLock();
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        System.out.println("--- Estacionamento Iniciado (Total de Vagas: " + TOTAL_SPOTS + ") ---");

        // Simula 20 carros tentando entrar
        for (int i = 1; i <= 20; i++) {
            final int carId = i;
            Thread carThread = new Thread(() -> simulateCar(carId));
            carThread.start();
        }
    }

    private static void simulateCar(int carId) {
        try {
            System.out.println("Carro #" + carId + " está procurando vaga...");
            
            // 1. Tenta pegar uma vaga (Semaphore)
            if (spots.tryAcquire(3, TimeUnit.SECONDS)) { // Espera até 3 segundos por uma vaga
                try {
                    // 2. Passa pelo portão (Lock) - apenas um carro entra por vez
                    gateLock.lock();
                    try {
                        System.out.println(">> Carro #" + carId + " PASSANDO pelo portão...");
                        Thread.sleep(200); // Tempo de passagem do portão
                    } finally {
                        gateLock.unlock();
                    }

                    System.out.println("   [+] Carro #" + carId + " ESTACIONADO.");
                    Thread.sleep(1000 + RANDOM.nextInt(3000)); // Tempo que o carro fica na vaga
                    System.out.println("   [-] Carro #" + carId + " SAINDO da vaga.");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 3. Libera a vaga para o próximo
                    spots.release();
                }
            } else {
                System.err.println("!!! Carro #" + carId + " DESISTIU: Estacionamento lotado.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
