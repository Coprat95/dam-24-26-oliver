package com.oliver.vt05.Server;

// Importar clases necesarias para sockets y manejo de datos
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    // Puerto en el que el servidor escuchará conexiones
    private final int PUERTO = 4321;

    // Atributos para el socket del servidor y el socket del cliente conectado
    private ServerSocket serverSocket;
    private Socket socket;

    // Constructor: inicializa el ServerSocket en el puerto definido
    public Servidor() throws IOException {
        serverSocket = new ServerSocket(PUERTO);
    }

    // Método principal para iniciar el servidor y aceptar conexiones
    public void iniciarServer() throws IOException {
        while (true) {
            // Mostrar mensaje de espera antes de aceptar la conexión
            System.out.println("Servidor a la espera de conexiones...");

            // Aceptar la conexión entrante y asignarla al socket
            socket = serverSocket.accept();
            System.out.println("Cliente conectado.");

            // Crear canal de salida para enviar mensaje al cliente
            DataOutputStream mensajeAlCliente = new DataOutputStream(socket.getOutputStream());
            mensajeAlCliente.writeUTF("Petición recibida.");

            // Crear canal de entrada para recibir mensajes del cliente
            DataInputStream mensajeDelCliente = new DataInputStream(socket.getInputStream());
            String mensajeRecibido;

            // Leer mensajes del cliente hasta que se cierre la conexión
            try {
                while (!(mensajeRecibido = mensajeDelCliente.readUTF()).isEmpty()) {
                    // Mostrar cada mensaje recibido por consola
                    System.out.println(mensajeRecibido);
                }
            } catch (EOFException e) {
                // El cliente ha cerrado la conexión
                System.out.println("Fin de la comunicación.");
            }

            // Cierre de canales y socket
            mensajeDelCliente.close();
            mensajeAlCliente.close();
            socket.close();
            System.out.println("Conexión cerrada.\n");
        }
    }

    // Método opcional para cerrar el servidor manualmente
    public void finalizarServer() throws IOException {
        serverSocket.close();
    }
}