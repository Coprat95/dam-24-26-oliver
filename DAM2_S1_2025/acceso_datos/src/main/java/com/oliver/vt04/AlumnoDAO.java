package com.oliver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {
    private final Connection connection;

    // Constructor que recibe la conexion
    public AlumnoDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo para insertar un alumno en la base de datos
    public void insertarAlumno(Alumno alumno) {
        String sql = "INSERT INTO alumnos (nombre) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, alumno.getNombre());
            ps.executeUpdate();
            System.out.println("Datos insertados correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al insertar datos. " + e.getMessage());
        }
    }

    // Metodo para listar todos los alumnos

    public List<Alumno> listarAlumnos() {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM alumnos";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Alumno a = new Alumno(rs.getInt("id"), rs.getString("nombre"));
                lista.add(a);
                System.out.println("Alumno añadido a la lista .");
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los alumnos. " + e.getMessage());
        }
        return lista;

    }

    // Metodo para eliminar un alumno por su ID
    public void eliminarAlumno(int id) {
        String sql = "DELETE FROM alumnos WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Se ha eliminado el alumno correctamente.");
            } else {
                System.out.println("No se ha podido eliminar el alumno con ese ID.");
            }
        } catch (SQLException e) {
            System.err.println("No se ha podido conectar a la base de datos.");
        }
    }
}







//    private final Connection connection;
//
//    // Constructor que recibe la conexion
//    public AlumnoDAO(Connection connection){
//        this.connection = connection;
//    }
//    // Metodo para insertar un alumno en la base de datos
//    public void insertarAlumno(Alumno alumno){
//        String sql = "INSERT INTO alumnos (nombre) VALUES (?)";
//        try (PreparedStatement ps = connection.prepareStatement(sql)){
//            ps.setString(1, alumno.getNombre());
//            ps.executeUpdate();
//            System.out.println("Alumno insertado correctamente.");
//        } catch (SQLException e ){
//            System.err.println("Error al insertar el alumno :"+e.getMessage());
//        }
//    }
//    // Metodo para listar todos los alumnos
//    public List<Alumno> listarAlumnos(){
//        List<Alumno> lista = new ArrayList<>();
//        String sql = "SELECT id, nombre FROM alumnos";
//        try (PreparedStatement ps = connection.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                Alumno a = new Alumno(rs.getInt("id"), rs.getString("nombre"));
//                lista.add(a);
//            }
//         } catch (SQLException e){
//            System.err.println("Error al listar alumnos: "+ e.getMessage());
//         }
//        return lista;
//    }
//
//    // Metodo para eliminar un alumno por su ID
//public void eliminarAlumno (int id) {
//    String sql = "DELETE FROM alumnos WHERE ID = ?";
//    try ( PreparedStatement ps = connection.prepareStatement(sql)){
//        ps.setInt(1,id);
//        int filas = ps.executeUpdate();
//        if (filas > 0 ) {
//            System.out.println("Alumno eliminado correctamente.");
//        } else {
//            System.out.println("No se encontró ningún alumno con ese id.");
//        }
//    } catch (SQLException e){
//        System.err.println("Error al eliminar al alumno: "+e.getMessage());
//    }
//    }

