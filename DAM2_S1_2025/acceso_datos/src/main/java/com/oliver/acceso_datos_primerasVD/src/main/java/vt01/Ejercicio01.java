package vt01;

import java.io.File;
import java.io.IOException;

public class Ejercicio01 {
    public static void main(String[] args) {
    File miDirectorio3 = new File("src/vt01/miDirectorio3");
    File miFichero3 = new File("src/vt01/miDirectorio3/miFichero3.txt");

    try {
        miDirectorio3.mkdir();
        miFichero3.createNewFile();

        if (miFichero3.exists()) {
            System.out.println("El archivo se ha creado con éxito.");
        }
        else {
            System.out.println("No se ha podido crear el archivo.");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
        System.out.println("Fin del proyecto.");









    }
//        File miDirectorio2 = new File ("src/vt01/miDirectorio2");
//        File miFichero2 = new File("src/vt01/miDirectorio2/miFichero2");
//
//        try{
//            miDirectorio2.mkdir();
//            miFichero2.createNewFile();
//            if (miFichero2.exists()){
//                System.out.println("El archivo ha sido creado con éxito.");
//            } else {
//                System.out.println("No se ha podido crear el archivo.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Fin del programa. ");
//
//    }




















//        File miDirectorio = new File("src/vt01/miDirectorio");
//        File miFichero = new File("src/vt01/miDirectorio/miFichero.txt");
//
//        try {
//            miDirectorio.mkdir();
//            if( miFichero.createNewFile())
//                System.out.println("Fichero creado correctamente.");
//            else
//                System.out.println("ERROR: No se ha podido crear el fichero.");
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        System.out.println("Fin del programa.");
//    }
}
