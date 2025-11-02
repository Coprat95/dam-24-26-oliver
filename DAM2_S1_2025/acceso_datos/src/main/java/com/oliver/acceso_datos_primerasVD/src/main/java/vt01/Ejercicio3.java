package vt01;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Ejercicio3 {
    public static void main(String[] args) {
        File ficheroDatos = new File("src/vt01/datos.txt");
        if (!ficheroDatos.exists()) {
            try {
                if (ficheroDatos.createNewFile()) {
                    System.out.println("Fichero creado correctamente.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Ejercicio3OperacionesArchivos operador = new Ejercicio3OperacionesArchivos();
        try{
            operador.escribir(ficheroDatos);
            operador.leer(ficheroDatos);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}