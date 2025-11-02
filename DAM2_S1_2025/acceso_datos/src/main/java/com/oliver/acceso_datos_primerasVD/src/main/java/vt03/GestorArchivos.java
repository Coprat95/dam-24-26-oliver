package vt03;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// GestorArchivos.java
public class GestorArchivos {
    // metodo para leerArchivos
    public void leerArchivos(String nombreArchivo) throws ArchivoNoEncontradoException {
       try(BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))){
           String linea ;
           while((linea = br.readLine()) !=null){
               System.out.println(linea);
           }
       } catch(FileNotFoundException e) {
           throw new ArchivoNoEncontradoException("No se ha encontrado el archivo " + nombreArchivo);
       } catch(IOException e) {
           System.err.println("Error al leer el archivo : "+e.getMessage());
       }
    }













//    public void leerArchivo(String nombreArchivo) throws ArchivoNoEncontradoException {
//        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
//            String linea;
//            while ((linea = reader.readLine()) != null) {
//                System.out.println(linea);
//            }
//        } catch (FileNotFoundException e) {
//            throw new ArchivoNoEncontradoException("El archivo " + nombreArchivo + " no se encontró.");
//        } catch (IOException e) {
//            System.out.println("Ha ocurrido un error al leer el archivo.");
//        }
//    }
}