package com.oliver.vt04;

import java.util.Random;

public class CarreraRunnable implements  Runnable {
    private String nombre ;
    private int metrosRecorridos;
    private static int posicionGlobal = 0;
    public CarreraRunnable(String nombre){
        this.nombre = nombre;
    }

    @Override
    public void run(){
        Random random = new Random();
        while (metrosRecorridos <100) {
            metrosRecorridos += random.nextInt(1, 11);
            System.out.printf("""
                    Soy el atleta %s  y he recorrido %d metros. 
                    """, nombre, metrosRecorridos);
            try{
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.err.println("Pausamos ejecucion: "+e.getMessage());
            }
        }
        synchronized (CarreraRunnable.class){
            posicionGlobal= posicionGlobal+1;
            System.out.printf("""
                    El corredor %s ha llegado a meta en posición %d
                    """,nombre,posicionGlobal);

        }

    }
















//    private final String nombre;
//    private int metrosRecorridos;
//    private static int posicionGlobal = 0;
//
//    public CarreraRunnable(String nombre) {
//        this.nombre = nombre;
//
//    }
//
//    @Override
//    public void run() {
//
//        Random random = new Random();
//        while (metrosRecorridos < 100) {
//
//            int avance = random.nextInt(1, 11);
//            metrosRecorridos += avance;
//            System.out.println(nombre + " ha recorrido " + metrosRecorridos + " metros.");
//            try {
//                Thread.sleep(200); // simula tiempo entre avances
//            } catch (InterruptedException e) {
//                System.err.println(nombre + "fue interrumpido.");
//                return;
//            }
//        }
//
//        // Sincronizar acceso a la posicion final
//        synchronized (CarreraRunnable.class){
//            posicionGlobal++;
//            System.out.println(nombre+" ha llegado a la meta en la posicion "+posicionGlobal);
//        }
//
//    }
//
//    public int getMetrosRecorridos() {
//        return metrosRecorridos;
//    }
//
//    public void setMetrosRecorridos(int metrosRecorridos) {
//        this.metrosRecorridos = metrosRecorridos;
//    }
//
//    public String getNombre() {
//        return nombre;
//    }
}


