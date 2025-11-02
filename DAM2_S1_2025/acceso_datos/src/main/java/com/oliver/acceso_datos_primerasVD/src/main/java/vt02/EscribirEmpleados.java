package vt02;

import java.io.*;

public class EscribirEmpleados {
    public static void main(String[] args)  throws IOException {
        // Creacion archivo binario
       File archivo = new File("src/vt02/EscribirEmpleados.txt");
       try (RandomAccessFile archivoBinario = new RandomAccessFile(archivo,"rw")){

        // Datos empleados
        String [] apellidos = {"FERNANDEZ","GIL","LOPEZ","RAMOS","SEVILLA"};
        int [] departamentos = {10,20,10,30,20};
        double [] salarios = {1500.50, 2000.75, 1800.00, 2200.25, 1750.00};

        // Introducimos datos
           for (int i = 0; i < apellidos.length; i++) {
               archivoBinario.writeInt(i+1); // ID

               StringBuffer sb = new StringBuffer(apellidos[i]);
               sb.setLength(10);
               archivoBinario.writeChars(sb.toString());
               archivoBinario.writeInt(departamentos[i]);
               archivoBinario.writeDouble(salarios[i]);
           }
       }







    }
}
