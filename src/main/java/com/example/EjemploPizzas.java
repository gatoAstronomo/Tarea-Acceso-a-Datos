package com.example;

import java.sql.*;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EjemploPizzas {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        EjemploPizzas ejemplo = new EjemploPizzas();
        ejemplo.menuConsola();
        AnsiConsole.systemUninstall();
    }

    public void menuConsola() {
        Scanner scanner = new Scanner(System.in);
        String opcion = "";
        final String salir_char = "5";

        do {
            System.out.println("====== Menú de Pizzas ======");
            System.out.println("1. Agregar nueva pizza");
            System.out.println("2. Mostrar pizzas");
            System.out.println("3. Eliminar pizza");
            System.out.println("4. Actualizar pizza");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextLine();
           
            switch (opcion) {
                case "1":
                    agregarPizza();
                    break;
                case "2":
                    mostrarPizzas();
                    break;
                case "3":
                    eliminarPizza();
                    break;
                case "4":
                    updatePizza();
                case salir_char:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida, intente de nuevo.");
            }

        } while (!opcion.equals(salir_char));

        scanner.close();
    }

    public void agregarPizza() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el nombre de la pizza: ");
        String nombrePizza = scanner.nextLine();

        System.out.print("Ingrese el valor de la pizza: ");
        int valorPizza = Integer.parseInt(scanner.nextLine());

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        try {
            crearBaseDatosYTabla(url, user, password);

            String sql = "INSERT INTO Pizza (nombrePizza, valorPizza) VALUES (?, ?)";

            try (Connection conexion = DriverManager.getConnection(url, user, password);
                 PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, nombrePizza);
                ps.setInt(2, valorPizza);

                int filasInsertadas = ps.executeUpdate();
                if (filasInsertadas > 0) {
                    System.out.println("Pizza agregada exitosamente.");
                }

            } catch (SQLException e) {
                System.err.println("Error al insertar pizza: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Error al crear base de datos o tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mostrarPizzas() {
        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        try {
            crearBaseDatosYTabla(url, user, password);

            String sql = "SELECT * FROM Pizza";

            System.out.println("\n====== Listado de Pizzas ======\n");
            System.out.print("+------------+------------+---------------+\n");
            System.out.print("| Código Pizza| Valor Pizza| Nombre Pizza  |\n");
            System.out.print("+------------+------------+---------------+\n");

            try (Connection conexion = DriverManager.getConnection(url, user, password);
                 Statement statement = conexion.createStatement();
                 ResultSet rs = statement.executeQuery(sql)) {

                while (rs.next()) {
                    System.out.printf("| %-10d | %-10d | %-13s |\n",
                        rs.getInt("codigoPizza"),
                        rs.getInt("valorPizza"),
                        rs.getString("nombrePizza"));
                }
                System.out.print("+------------+------------+---------------+\n");

            } catch (SQLException e) {
                System.err.println("Error al mostrar pizzas: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Error al crear base de datos o tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarPizza() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el codigo de la pizza: ");
        int codigoPizza = scanner.nextInt();

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        try {
            crearBaseDatosYTabla(url, user, password);

            String sql = "Delete FROM Pizza WHERE codigoPizza = ?";

            try (Connection conexion = DriverManager.getConnection(url, user, password);
                 PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setInt(1, codigoPizza);

                int filasEliminadas = ps.executeUpdate();
                if (filasEliminadas > 0) {
                    System.out.println("Pizza eliminada exitosamente.");
                }

            } catch (SQLException e) {
                System.err.println("Error al eliminar pizza: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Error al crear base de datos o tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePizza() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el codigo de la pizza a actualizar: ");
        int codigoPizza = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        System.out.print("Ingrese el nuevo nombre de la pizza: ");
        String nuevoNombrePizza = scanner.nextLine();

        System.out.print("Ingrese el nuevo valor de la pizza: ");
        int nuevoValorPizza = Integer.parseInt(scanner.nextLine());

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        try {
            crearBaseDatosYTabla(url, user, password);

            String sql = "UPDATE Pizza SET nombrePizza = ?, valorPizza = ? WHERE codigoPizza = ?";

            try (Connection conexion = DriverManager.getConnection(url, user, password);
                 PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setString(1, nuevoNombrePizza);
                ps.setInt(2, nuevoValorPizza);
                ps.setInt(3, codigoPizza);

                int filasActualizadas = ps.executeUpdate();
                if (filasActualizadas > 0) {
                    System.out.println("Pizza actualizada exitosamente.");
                }

            } catch (SQLException e) {
                System.err.println("Error al actualizar pizza: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Error al crear base de datos o tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    private void crearBaseDatosYTabla(String url, String user, String password) throws SQLException {
        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            String dbName = url.substring(url.lastIndexOf("/") + 1);
            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(createDbSql);

            String createTableSql = "CREATE TABLE IF NOT EXISTS Pizza ("
                                 + "codigoPizza INT AUTO_INCREMENT PRIMARY KEY, "
                                 + "valorPizza INT NOT NULL, "
                                 + "nombrePizza VARCHAR(30) NOT NULL)";
            stmt.executeUpdate(createTableSql);
        }
    }

}