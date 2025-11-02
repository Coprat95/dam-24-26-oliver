package com.oliver.vt04;

public class MainCarrera {
    public static void main(String[] args) {
    Thread[] hilos = new Thread[10];
    for(int i = 0; i<10; i++){
        CarreraRunnable corredor = new CarreraRunnable("corredor"+(i+1));
        Thread hilo = new Thread(corredor);
        hilos[i] = hilo;
        hilos[i].start();
    }
    for (Thread hilo : hilos ) {
       try{
           hilo.join();
       } catch (InterruptedException e) {
           System.err.println("Error al unir los hilos. "+e.getMessage());
       }

    }
        System.out.println("La carrera ha terminado.");

















//    Thread[] hilos = new Thread[10];
//
//    for (int i = 0; i <10 ; i++) {
//        CarreraRunnable corredor = new CarreraRunnable("Corredor "+ (i+1));
//        hilos[i] = new Thread(corredor);
//        hilos[i].start();
//    }
//    // Esperar a que todos los hilos terminen
//        for (Thread hilo : hilos){
//            try {
//                hilo.join();
//            } catch (InterruptedException e) {
//                System.err.println("Error al esperar hilo : "+e.getMessage());
//            }
//        }
//        System.out.println("La carrera ha terminado.");
    }

}
