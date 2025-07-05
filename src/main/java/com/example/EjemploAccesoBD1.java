package com.example;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EjemploAccesoBD1 {

    public static void main(String[] args) {
        AnsiConsole.systemInstall(); // Instala Jansi

        EjemploAccesoBD1 ejemplo = new EjemploAccesoBD1();
        ejemplo.menuConsola();

        AnsiConsole.systemUninstall(); // Desinstala Jansi al final
    }

    public void menuConsola() {
        Scanner scanner = new Scanner(System.in);
        String opcion = "";

        do {
            System.out.println("====== Menú ======");
            System.out.println("1. Ingresar nueva persona");
            System.out.println("2. Mostrar personas");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    ingresarPersona();
                    break;
                case "2":
                    mostrarPersonas();
                    break;
                case "3":
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida, intente de nuevo.");
            }

        } while (!opcion.equals("3"));

        scanner.close();
    }

    public boolean esNombreValido(String nombre){
        if (nombre.trim().isEmpty()){
            System.out.println("El nombre no puede estar vacío. Intente nuevamente.");
            return false;
        }
        if (nombre.length() > 100){
            System.out.println("El nombre excede los 100 caracteres");
            return false;
        }
        return true;
    }

    public boolean esFechaValida(String fechaStr) {
        try {
            // Intenta parsear la fecha
            LocalDate fechaNacimiento = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
            
            // Verifica que la fecha no sea futura
            if (fechaNacimiento.isAfter(LocalDate.now())) {
                System.out.println("La fecha no puede ser futura. Intente nuevamente.");
                return false;
            }
            return true; // La fecha es válida
        } catch (DateTimeParseException e) {
            System.out.println("Fecha inválida. Por favor, use el formato YYYY-MM-DD.");
            return false; // La fecha no es válida
        }
    }

    public void ingresarPersona() {
        Scanner scanner = new Scanner(System.in);
    
        // Validación del nombre
        String nombre;
        do {
            System.out.print("Ingrese el nombre de la persona: ");
            nombre = scanner.nextLine();
        } while (!esNombreValido(nombre)); // Cambiar a un chequeo negativo
    
        // Validación de la fecha
        LocalDate fechaNacimiento = null;
        boolean fechaValida = false;
        while (!fechaValida) {
            System.out.print("Ingrese la fecha de nacimiento (YYYY-MM-DD): ");
            String fechaStr = scanner.nextLine();
    
            // Usar el método de validación
            if (esFechaValida(fechaStr)) {
                fechaNacimiento = LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
                fechaValida = true; // La fecha es válida
            }
        }
    
        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        // Crear base de datos y tabla si no existen
        crearBaseDatosYTabla(url, user, password);

        String sql = "INSERT INTO Persona (nombre, fechaNacimiento) VALUES (?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setDate(2, Date.valueOf(fechaNacimiento));

            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Persona agregada exitosamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar persona: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    public void mostrarPersonas() {
        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        crearBaseDatosYTabla(url, user, password);
    
        String sql = "SELECT * FROM Persona";
    
        System.out.println("\n====== Listado de Personas ======\n");
        System.out.printf("+-----+---------------------+------------------+\n");
        System.out.printf("| ID  | Nombre              | Fecha Nacimiento |\n");
        System.out.printf("+-----+---------------------+------------------+\n");
    
        try (Connection conexion = DriverManager.getConnection(url, user, password);
             Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
    
            // Mostrar los resultados en formato tabular
            while (rs.next()) {
                System.out.printf("| %-3d | %-19s | %-16s |\n", 
                    rs.getInt("id"), 
                    rs.getString("nombre"), 
                    rs.getDate("fechaNacimiento"));
            }
            System.out.printf("+-----+---------------------+------------------+\n");
    
        } catch (SQLException e) {
            System.err.println("Error al mostrar personas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para cargar las configuraciones desde un archivo externo
    private Properties cargarConfiguracionDB() {
        Properties config = new Properties();
        try {
            config.load(Files.newInputStream(Paths.get("src/main/java/com/data/config.properties")));
        } catch (IOException e) {
            System.err.println("Error al cargar el archivo de configuración: " + e.getMessage());
            e.printStackTrace();
        }
        return config;
    }

    private void crearBaseDatosYTabla(String url, String user, String password) {
        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            // Crear base de datos si no existe
            String dbName = url.substring(url.lastIndexOf("/") + 1);
            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(createDbSql);

            // Crear tabla 'Persona' si no existe
            String createTableSql = "CREATE TABLE IF NOT EXISTS Persona ("
                                 + "id INT AUTO_INCREMENT PRIMARY KEY, "
                                 + "nombre VARCHAR(100) NOT NULL, "
                                 + "fechaNacimiento DATE NOT NULL)";
            stmt.executeUpdate(createTableSql);

        } catch (SQLException e) {
            System.err.println("Error al crear base de datos o tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

