package com.oliver.vt04;

public class MainPrioridades {
    public static void main(String[] args) {
        PrioridadesRunnable prio1 = new PrioridadesRunnable("Hilo1");
        Thread thread1 = new Thread(prio1);
        thread1.setPriority(1);

        PrioridadesRunnable prio2 = new PrioridadesRunnable("Hilo2");
        Thread thread2 = new Thread(prio2);
        thread2.setPriority(2);

        PrioridadesRunnable prio3 = new PrioridadesRunnable("Hilo3");
        Thread thread3 = new Thread(prio3);
        thread3.setPriority(3);

        System.out.println("Se han creado los 3 hilos.");
        System.out.println("Hilos activos en este momento: "+Thread.activeCount());

        thread1.start();
        thread2.start();
        thread3.start();
        }

















//    public static void main(String[] args) {
//        PrioridadesRunnable runnable1 = new PrioridadesRunnable("Hilo1");
//        Thread thread1 = new Thread(runnable1);
//        thread1.setPriority(1);
//
//
//        PrioridadesRunnable runnable2 = new PrioridadesRunnable("Hilo2");
//        Thread thread2 = new Thread(runnable2);
//        thread2.setPriority(2);
//
//
//        PrioridadesRunnable runnable3 = new PrioridadesRunnable("Hilo3");
//        Thread thread3 = new Thread(runnable3);
//        thread3.setPriority(3);
//
//        System.out.println("El programa principal ha creado los 3 hilos.");
//        System.out.println("Hilos activos en este momento: " + Thread.activeCount());
//
//        thread1.start();
//        thread2.start();
//        thread3.start();
//        }
    }


