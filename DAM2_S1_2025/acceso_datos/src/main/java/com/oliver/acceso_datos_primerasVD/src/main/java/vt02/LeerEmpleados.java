package vt02;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LeerEmpleados {
    public static void main(String[] args) throws IOException{
        // Apuntamos hacia el fichero
        File fichero = new File("src/vt02/EscribirEmpleados.txt");
        try(RandomAccessFile ficheroBinario = new RandomAccessFile(fichero,"r")){

        // Declaración variables
        int id, departamento;
        char [] apellido = new char[10];
        double salario;
        long posicion = 0;

        while (ficheroBinario.getFilePointer()<ficheroBinario.length() ){
            ficheroBinario.seek(posicion);
            id = ficheroBinario.readInt();

            for (int i = 0; i < apellido.length; i++){
                apellido[i] = ficheroBinario.readChar();
            }
            String apellidoTrim = new String(apellido).trim();
            departamento = ficheroBinario.readInt();
            salario = ficheroBinario.readDouble();
//            if (apellidoTrim.equals("GIL")) {   Pondríamos esto si quisiéramos filtrar algún resultado.
                System.out.printf("ID: %d, Apellido: %s, Departamento :%d, Salario:%.2f\n",
                        id, apellidoTrim, departamento, salario);
//            }
            // Una vez se ha leido entero, actualizamos la posición
            posicion += 36;
        }

        }catch (IOException e){
            e.printStackTrace();
        }


    }












//    public static void main(String[] args) throws IOException {
//        // Apuntamos al fichero
//        File fichero = new File("src/vt02/LeerEmpleados.txt");
//        try(RandomAccessFile ficheroBinario = new RandomAccessFile(fichero,"r")){
//        // Creamos las variables
//        int id, departamento;
//        double salario;
//        char[] apellido = new char[10];
//        long posicion = 0;
//
//        while (ficheroBinario.getFilePointer() < ficheroBinario.length()){
//            ficheroBinario.seek(posicion);
//
//            id = ficheroBinario.readInt();
//
//            for (int i = 0; i < apellido.length; i++) {
//                apellido[i] = ficheroBinario.readChar();
//            }
//            String apellidoStr = new String(apellido).trim();
//            departamento = ficheroBinario.readInt();
//            salario = ficheroBinario.readDouble();
//
//            System.out.printf("ID : %d, Apellido : %s, Departamento :%d, Salario: %.2f\n",
//            id,apellidoStr,departamento,salario);
//            posicion +=36;
//        }
//
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }


}
