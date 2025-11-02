package vt01;

import java.io.*;

public class Ejercicio5 {
    public static void main(String[] args) {
        File fichero = new File("src/vt01/Empleados.txt");
        String [] empleados = {"Oliver","Alicia","Paco","Manolo","Antonio"};
        try(FileWriter escritor = new FileWriter(fichero)){
            escritor.write("Lista de empleados:");
            for (int i = 0; i<empleados.length; i++){
                escritor.write("\nID : "+(i+1)+"\tNombre: "+empleados[i]);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        try(BufferedReader bf = new BufferedReader(new FileReader(fichero))){
            String linea = bf.readLine();
            while (linea != null){
                System.out.println(linea);
                linea = bf.readLine();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
