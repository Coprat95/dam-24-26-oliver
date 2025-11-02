package vt01;

import java.io.*;

public class Ejercicio3OperacionesArchivos {
    public void escribir (File archivo) throws IOException{
        try(FileWriter escritor = new FileWriter(archivo)) {
            escritor.write("Texto de prueba.");
        }
    }
    public void leer (File archivo) throws IOException{
        try(FileReader lector = new FileReader(archivo)) {
            int valor = lector.read();

            while (valor != -1) {
                System.out.println("Char: " + (char) valor + "\t ascii: " + valor);
                valor = lector.read();
            }
        }
    }
}
