package com.oliver.vt04;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    protected final String BBDD;
    protected final String USER;
    protected final String PASSWORD;
    protected Connection connection;

    // Constructor por defecto. Delega para que se ejecute el segundo constructor con éstos parámetros por defecto.
    public ConnectDB() {
        this("jdbc:postgresql://localhost:5432/alumnos_db", "oliver", "backend123");
    }

    // Constructor parametrizado
    public ConnectDB(String BBDD, String USER, String PASSWORD) {
        this.BBDD = BBDD;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        connectToDB();
    }

    private void connectToDB() {
        try {
            connection = DriverManager.getConnection(BBDD, USER, PASSWORD);
            System.out.println("Conexión establecida correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}