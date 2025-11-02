package com.oliver.tictacpackage;


public class TicTacControl2 {
    private boolean turnoTic = true;

    // Clase que se usa para imprimir un mensaje u otro .
    // Se hace mediante boolean turnoTic .
    // Se imprime mensaje y se sincroniza el estado
    // para que se active el otro método.
    public TicTacControl2(){

    }
    public synchronized  void turnoTic (){
        while (!turnoTic){
            try{
                wait();
            } catch (InterruptedException e){
                System.err.println("Error al pausar: "+e.getMessage());
            }
        }
        System.out.println("Tic");
        turnoTic = false;
        notifyAll();
    }

    public synchronized void turnoTac (){
        while (turnoTic) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Error al pausar : "+e.getMessage());
            }
        }
        System.out.println("Tac");
        turnoTic = true;
        notifyAll();
    }
}
