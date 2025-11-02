package vt01;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Ejercicio2 {
    public static void main(String[] args) {
        // Primero creamos el directorio de ejemplo con su fichero

        File directorio = new File("src/vt01/directorio");
        File fichero = new File("src/vt01/directorio/fichero_de_texto");

        try{

        // Añadimos un if para asegurarnos de que se ha creado correctamente.
        if (directorio.mkdir()) {
            System.out.println("Directorio creado con éxito.");
        } else {
            System.out.println("No se ha podido crear el directorio.");
        }
        if (fichero.createNewFile()){
            System.out.println("Fichero creado con éxito.");
        } else {
            System.out.println("No se ha podido crear el fichero.");
        }
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Primera parte del programa finalizada.");

        // Ampliamos el programa
        System.out.println("*** Parte 2: ***");

        Scanner scanner = new Scanner(System.in);
        var salir = false;
        while (!salir) {
            System.out.println("Elija una opción de entre las siguientes : ");
            System.out.println("""
                    1: Crear directorio nuevoDirectorio.
                    2: Crear fichero fichero_de_texto2 en nuevoDirectorio.
                    3: Eliminar fichero_de_texto
                    4: Eliminar el directorio nuevoDirectorio.
                    5: Salir.
                    """);

            try {
                var opcion = Integer.parseInt(scanner.nextLine());
                switch (opcion) {
                    case 1 -> {
                        File nuevoDirectorio = new File("src/vt01/nuevoDirectorio");
                        if (nuevoDirectorio.mkdir()) {
                            System.out.print("Directorio nuevoDirectorio creado con éxito en la ruta: ");
                            System.out.println(nuevoDirectorio.getAbsolutePath());

                        } else {
                            System.out.println("El directorio ya existía.");
                        }
                    }
                    case 2 -> {
                        try {
                        File nuevoFichero = new File("src/vt01/nuevoDirectorio/fichero_de_texto2");

                            if (nuevoFichero.createNewFile()) {
                                System.out.print("Fichero creado con éxito en la ruta : ");
                                System.out.println(nuevoFichero.getAbsolutePath());
                            } else {
                                System.out.println("No se ha podido crear el fichero.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception f) {
                            System.out.println(" No se ha encontrado la ruta. ");
                        }
                    }
                    case 3 -> {

                        if (fichero.delete()) {
                            System.out.println("Se ha eliminado fichero_de_texto con éxito.");
                        } else {
                            System.out.println("No se ha podido eliminar fichero_de_texto.");
                        }
                    }
                    case 4 -> {
                        File nuevoFichero = new File("src/vt01/nuevoDirectorio/fichero_de_texto2");
                        if (nuevoFichero.delete()) {
                            System.out.println("Se ha eliminado fichero_de_texto2 con éxito.");
                        } else {
                            System.out.println("No se ha podido eliminar fichero_de_texto2 .");
                        }

                        File nuevoDirectorio = new File("src/vt01/nuevoDirectorio");

                        if (nuevoDirectorio.delete()) {
                            System.out.println("Se ha eliminado nuevoDirectorio con éxito.");
                        } else {
                            System.out.println("No se ha podido eliminar nuevoDirectorio.");
                        }
                    }
                    case 5 -> {
                        salir = true;
                        System.out.println("Saliendo del programa...");
                    }
                    default ->  System.out.println("Introduce un número del 1 al 5");
                }
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido. Introduce un número del 1 al 5.");
            }
        }
        scanner.close();
        System.out.println("Programa finalizado.");
    }
}
