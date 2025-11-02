package com.oliver.tictacpackage;

public class Tac2 implements  Runnable{
    TicTacControl2 control2;

    public Tac2(TicTacControl2 control2) {
        this.control2 = control2;
    }

    public void run() {
        while (true) {
            control2.turnoTac();
            try {
                Thread.sleep(4500);
            } catch (InterruptedException e) {
                System.err.println("Error al poner el hilo en pausa : " + e.getMessage());

            }
        }
    }
}
