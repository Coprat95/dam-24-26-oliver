package com.oliver;

public class MainCarrera {
    public static void main(String[] args) {

        Thread[] arrayHilos = new Thread[10];
        for (int i = 0; i < 10; i++) {
            Atleta atleta = new Atleta("corredor"+(i+1));
            arrayHilos[i] = new Thread(atleta);
            arrayHilos[i].start();
        }
        for (Thread hilos : arrayHilos){
            try{
                hilos.join();
            } catch (InterruptedException e) {
                System.err.println("Error al unir los hilos: "+e.getMessage());
            }
        }
    }




















//    public static void main(String[] args) {
//    Thread[] corredores = new Thread[10];
//
//    for (int i = 0; i < 10 ; i ++){
//        Atleta atleta = new Atleta("Corredor"+ (i+1));
//        corredores[i] = new Thread(atleta);
//        corredores[i].start();
//    }
//
//    // esperamos a que todos terminen
//        // bucle for each para arrays
//    for (Thread corredor : corredores) {
//        try {
//            corredor.join();
//        } catch (InterruptedException e){
//            System.err.println("Error al unir el hilo : "+ e.getMessage());
//        }
//    }
//        System.out.println("\n La carrera ha finalizado.");
//    }
}
