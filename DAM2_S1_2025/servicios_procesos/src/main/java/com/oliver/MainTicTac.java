package com.oliver;

public class MainTicTac {
    public static void main(String[] args) {
        TicTacControl control = new TicTacControl();

        Thread ticThread = new Thread(new Tic(control));
        Thread tacThread = new Thread(new Tac(control));

        ticThread.start();
        tacThread.start();
    }
}