package com.oliver;

public class Tac implements Runnable {
    private final TicTacControl control;

    public Tac(TicTacControl control) {
        this.control = control;
    }

    @Override
    public void run() {
        while (true) {
            control.tac();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.err.println("Error en Tac: " + e.getMessage());
            }
        }
    }
}