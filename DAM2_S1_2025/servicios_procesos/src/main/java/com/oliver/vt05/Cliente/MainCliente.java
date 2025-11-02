package com.oliver.vt05.Cliente;

import java.io.IOException;

public class MainCliente {
    public static void main(String[] args) throws IOException {
        Cliente cliente = new Cliente();
        try{
            System.out.println("Iniciando cliente...");
            cliente.iniciarCliente();

        } catch (IOException e) {
            System.err.println("Error en la salida/entrada de datos del cliente. "+e.getMessage());
        }
    }
}
