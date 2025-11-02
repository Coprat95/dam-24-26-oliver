package vt01;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ejercicio3_2 {
    public static void main(String[] args) {
        File fichero = new File("src/vt01/ficheroArrayStrings.txt");
        try(FileReader lector = new FileReader(fichero)){
            int texto = lector.read();
            while (texto != -1) {
                System.out.print((char)texto);
                texto = lector.read();
            }
            System.out.println("\nFin del texto.");
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
