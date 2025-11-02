package com.oliver.tictacpackage;

public class MainTicTac2 {
    public static void main(String[] args) {
        TicTacControl2 ticTacControl2 = new TicTacControl2();
        Thread threadTic = new Thread(new Tic2(ticTacControl2));
        Thread threadTac = new Thread(new Tac2(ticTacControl2));

        threadTic.start();
        threadTac.start();
    }
}
