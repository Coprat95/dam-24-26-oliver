package com.oliver;

public class TicTacControl {
    private boolean turnoTic = true;

    public synchronized void tic() {
        while (!turnoTic) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Tic interrumpido: " + e.getMessage());
            }
        }
        System.out.println("Tic");
        turnoTic = false;
        notifyAll();
    }

    public synchronized void tac() {
        while (turnoTic) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Tac interrumpido: " + e.getMessage());
            }
        }
        System.out.println("Tac");
        turnoTic = true;
        notifyAll();
    }
}