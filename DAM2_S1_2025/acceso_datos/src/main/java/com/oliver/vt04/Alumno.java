package com.oliver;

import java.io.Serializable;

public class Alumno implements Serializable {
    private int id;
    private String nombre;

    public Alumno(int id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
//    private int id;
//    private String nombre;
//    public Alumno(){}
//    public Alumno(int id , String nombre){
//        this.id = id;
//        this.nombre = nombre;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getNombre() {
//        return nombre;
//    }
//
//    public void setNombre(String nombre) {
//        this.nombre = nombre;
//    }
//
//    @Override
//    public String toString() {
//        return "Alumno{" +
//                "id=" + id +
//                ", nombre='" + nombre + '\'' +
//                '}';
//    }
//}
