package com.oliver.vt04;

public class PrioridadesRunnable implements Runnable {
    private final String nombre;
    public PrioridadesRunnable(String nombre){
        this.nombre = nombre;
    }

    @Override
    public void run() {
    Thread hiloActual = Thread.currentThread();
        System.out.println("Estamos en el "+nombre+" , con prioridad "+hiloActual.getPriority()
        +" e ID : "+hiloActual.getId()+". Actualmente hay : "+Thread.activeCount()+" hilos activos.");

    }
}












//    private  final String nombre ;
//    public PrioridadesRunnable(String nombre){
//        this.nombre = nombre;
//    }
//    @Override
//    public void run(){
//        Thread hiloActual = Thread.currentThread();
//        System.out.println("Estoy dentro de " + nombre + ", prioridad :"+
//                hiloActual.getPriority() + ", ID: "+ hiloActual.getId() +
//                ", hilos activos: " + Thread.activeCount());
//    }

