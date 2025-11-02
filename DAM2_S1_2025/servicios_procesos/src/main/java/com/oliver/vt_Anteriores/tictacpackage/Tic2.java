package com.oliver.tictacpackage;

public class Tic2 implements  Runnable{
    TicTacControl2 control2 ;

    public Tic2(TicTacControl2 control2){
       this.control2 = control2;
    }
    public void run(){
        while(true) {
            control2.turnoTic();
            try {
                Thread.sleep(4500);
            } catch (InterruptedException e) {
                System.err.println("Error al poner el hilo en espera: " + e.getMessage());
            }
        }

    }
}
