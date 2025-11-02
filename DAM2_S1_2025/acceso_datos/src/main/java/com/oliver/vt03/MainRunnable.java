
package com.oliver.vt03;

public class MainRunnable {
    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            SaludoRunnable saludo = new SaludoRunnable("Hilo");
            Thread thread = new Thread(saludo);
            System.out.println("Este es el " + saludo.nombre + " número " + (i + 1));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Error al unir los hilos . " + e.getMessage());
            }
        }
    }
}