package vt01;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Ejercicio3_1 {
    // Que almacene Strings a partir de un array de Strings
    public static void main(String[] args) {
        File fichero = new File("src/vt01/ficheroArrayStrings.txt");
        try (FileWriter escritor = new FileWriter(fichero)) {
            String cadena = "Ejemplo de array de Strings";
            var arrayStrings = cadena.split(" ");
            for (int i = 0; i < arrayStrings.length; i++) {
            escritor.write(arrayStrings[i]+" ");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
