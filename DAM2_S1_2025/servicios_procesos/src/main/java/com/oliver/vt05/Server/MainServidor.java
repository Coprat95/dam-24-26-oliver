package com.oliver.vt05.Server;

import java.io.IOException;

public class MainServidor {
    public static void main(String[] args) throws IOException {
        Servidor server = new Servidor();
        try {
            System.out.println("Iniciando servidor...");
            server.iniciarServer();

        } catch (IOException e) {
            System.err.println("Error en la salida o entrada de datos del servidor. " + e.getMessage());
        }
    }
}
