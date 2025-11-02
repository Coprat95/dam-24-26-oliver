package vt03;


import java.io.*;
import java.util.Scanner;

/** Clase que gestiona lectura y escritura de archivos,
 * lanzando Excepciones que simular un control de acceso según permisos
 */
public class GestorArchivosSinPermiso {
    /**
     * Escribe un mensaje en el archivo
     * @param mensaje texto a escribir.
     * @throws ArchivoSinPermisoException Si no se puede acceder al archivo se lanza excepción.
     */
    public void escribir(String mensaje) throws ArchivoSinPermisoException {
        File inputFile = new File("src/vt03/ficheroTexto.txt");
        try (FileWriter escritor = new FileWriter(inputFile, true)){ //El true hace que se añada el texto al final
            escritor.write(mensaje);
        } catch (IOException e) {
            throw new ArchivoSinPermisoException("No se puede acceder al archivo " + e.getMessage());

        }
    }

    /**
     * Lee el contenido del archivo línea por línea.
     * @throws ArchivoSinPermisoException Si no se puede acceder al archivo se lanza excepción.
     */
    public void leer() throws ArchivoSinPermisoException {
        File inputFile = new File("src/vt03/ficheroTexto.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))){
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            throw new ArchivoSinPermisoException("Error al acceder al archivo " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        GestorArchivosSinPermiso userHandler = new GestorArchivosSinPermiso();
        try (Scanner scanner = new Scanner(System.in)){
            userHandler.escribir(scanner.nextLine());
            userHandler.leer();
        } catch (ArchivoSinPermisoException e) {
            System.err.println(e.getMessage());
        }
    }
}
