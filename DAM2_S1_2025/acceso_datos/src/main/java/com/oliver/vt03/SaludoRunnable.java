package com.oliver.vt03;

public class SaludoRunnable  implements Runnable {
    public String nombre;

    public SaludoRunnable(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Este es el saludo número : " + (i + 1));
            if (i == 9) {
                System.out.println("Fin del hilo.");
            }
        }
    }
}