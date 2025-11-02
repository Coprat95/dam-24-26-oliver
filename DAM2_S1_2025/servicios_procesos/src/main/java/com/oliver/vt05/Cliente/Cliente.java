package com.oliver.vt05.Cliente;

// Importar clases necesarias para sockets y manejo de datos
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {

    // Constantes para la dirección del servidor y el puerto de conexión
    private final String HOST = "localhost";
    private final int PUERTO = 4321;

    // Atributo para el socket que conecta con el servidor
    private Socket socket;

    // Constructor: establece la conexión con el servidor
    public Cliente() throws IOException {
        socket = new Socket(HOST, PUERTO);
    }

    // Metodo principal para iniciar la comunicación con el servidor
    public void iniciarCliente() throws IOException {

        // Crear canal de entrada para recibir mensaje del servidor
        DataInputStream entradaDelServidor = new DataInputStream(socket.getInputStream());

        // Leer y mostrar el mensaje recibido del servidor
        System.out.println(entradaDelServidor.readUTF());

        // Crear canal de salida para enviar mensajes al servidor
        DataOutputStream salidaAlServidor = new DataOutputStream(socket.getOutputStream());

        // Enviar 3 mensajes al servidor mediante un bucle
        for (int i = 0; i < 3; i++) {
            salidaAlServidor.writeUTF("Mensaje al servidor número " + (i + 1));
        }

        // Cerrar los canales y el socket
        salidaAlServidor.close();
        entradaDelServidor.close();
        socket.close();

        // Confirmar cierre de conexión en consola
        System.out.println("Fin de la conexión del cliente.");
    }
}