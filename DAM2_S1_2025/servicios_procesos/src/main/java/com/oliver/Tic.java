package com.oliver;

public class Tic implements Runnable {
    private final TicTacControl control;

    public Tic(TicTacControl control) {
        this.control = control;
    }

    @Override
    public void run() {
        while (true) {
            control.tic();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.err.println("Error en Tic: " + e.getMessage());
            }
        }
    }
}