package vt01;

import java.io.FileWriter;
import java.io.IOException;

public class ejercicio4 {
    public static void main(String[] args) {
        try(FileWriter escritor = new FileWriter("src/vt01/textoañadidochar.txt")){
       String cadena = "Esto es una frase que quiero almacenar en el fichero. ";
       var charCadena = cadena.toCharArray();
       for (int i  = 0; i < charCadena.length; i++){
           escritor.write(charCadena[i]);
       }
       escritor.append("*");
        } catch (IOException e){
            e.printStackTrace();
        }


    }
}
