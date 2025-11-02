package com.oliver;

import java.util.Random;

public class Atleta implements  Runnable {
private final String nombreAtleta;
private Random random = new Random();

public Atleta (String nombreAtleta ){
    this.nombreAtleta = nombreAtleta;
}

    @Override
    public void run() {
    int distanciaRecorrida = 0;
    while (distanciaRecorrida < 100){
        int paso = random.nextInt(1,11);
        distanciaRecorrida += paso;
        System.out.println("Soy el atleta "+nombreAtleta+" y he recorrido "+distanciaRecorrida+" metros. ");
        // Generamos un tiempo entre paso y paso
        try {
            Thread.sleep(200);
        } catch (InterruptedException e){
            System.err.println("Error al pausar el hilo : "+e.getMessage());
        }
    }
        System.out.println("El atleta " + nombreAtleta+" ha llegado a la meta. ");
    }











//    private final String nombreAtleta;
//    private final Random random = new Random();
//
//
//    public Atleta(String nombreAtleta) {
//        this.nombreAtleta = nombreAtleta;
//    }
//
//    @Override
//    public void run() {
//        int distanciaRecorrida = 0;
//        while (distanciaRecorrida < 100) {
//            int paso = random.nextInt(1, 11);
//            distanciaRecorrida += paso;
//
//            System.out.println("Soy el atleta " + nombreAtleta + ", he recorrido " + distanciaRecorrida +
//                    " metros.");
//            try {
//                Thread.sleep(200); // simula tiempo entre pasos
//            } catch (InterruptedException e) {
//                System.err.println("Atleta interrupido: " + e.getMessage());
//            }
//        }
//        System.out.println("El atleta " + nombreAtleta + " ha llegado a la meta.");
//
//    }
}