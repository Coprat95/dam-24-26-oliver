package com.oliver.vt05;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Main {
    public static void main(String[] args) {
        System.out.println("INICIO DEL PROGRAMA.");

        Configuration cfg = new Configuration().configure();
        SessionFactory sessionFactory = cfg.buildSessionFactory();
        Session session = sessionFactory.openSession();

        System.out.println("CONFIGURACIÓN REALIZADA...");

        Alumno alumno = new Alumno("Oliver Trave", "oliver@email.com");

        Transaction tx = session.beginTransaction();
        session.save(alumno);
        tx.commit();

        session.close();
        sessionFactory.close();

        System.out.println("Alumno guardado correctamente.");
    }
}