package com.example;

import java.sql.*;
import java.util.Scanner;
import org.fusesource.jansi.AnsiConsole;
import java.io.IOException;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SistemaBiblioteca {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        SistemaBiblioteca sistema = new SistemaBiblioteca();
        sistema.menuPrincipal();
        AnsiConsole.systemUninstall();
    }

    public void menuPrincipal() {
        Scanner scanner = new Scanner(System.in);
        String opcion = "";
        final String salir_char = "4";

        do {
            System.out.println("\n====== Sistema de Gestión de Biblioteca ======");
            System.out.println("1. Gestión de Usuarios");
            System.out.println("2. Gestión de Libros");
            System.out.println("3. Gestión de Préstamos");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextLine();
           
            switch (opcion) {
                case "1":
                    menuUsuarios();
                    break;
                case "2":
                    menuLibros();
                    break;
                case "3":
                    menuPrestamos();
                    break;
                case salir_char:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida, intente de nuevo.");
            }

        } while (!opcion.equals(salir_char));

        scanner.close();
    }

    public void menuUsuarios() {
        Scanner scanner = new Scanner(System.in);
        String opcion = "";
        final String volver_char = "5";

        do {
            System.out.println("\n====== Gestión de Usuarios ======");
            System.out.println("1. Agregar nuevo usuario");
            System.out.println("2. Mostrar usuarios");
            System.out.println("3. Actualizar usuario");
            System.out.println("4. Eliminar usuario");
            System.out.println("5. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextLine();
           
            switch (opcion) {
                case "1":
                    agregarUsuario();
                    break;
                case "2":
                    mostrarUsuarios();
                    break;
                case "3":
                    actualizarUsuario();
                    break;
                case "4":
                    eliminarUsuario();
                    break;
                case volver_char:
                    break;
                default:
                    System.out.println("Opción no válida, intente de nuevo.");
            }

        } while (!opcion.equals(volver_char));
    }

    public void agregarUsuario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el nombre del usuario: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese el email del usuario: ");
        String email = scanner.nextLine();

        System.out.print("Ingrese el teléfono del usuario: ");
        String telefono = scanner.nextLine();

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        String sql = "INSERT INTO usuarios (nombre, email, telefono) VALUES (?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, telefono);

            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Usuario agregado exitosamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mostrarUsuarios() {
        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        String sql = "SELECT * FROM usuarios ORDER BY id";

        System.out.println("\n====== Listado de Usuarios ======");
        System.out.println("+----+----------------------+----------------------+---------------+----------------+");
        System.out.println("| ID | Nombre               | Email                | Teléfono      | Fecha Registro |");
        System.out.println("+----+----------------------+----------------------+---------------+----------------+");

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("| %-2d | %-20s | %-20s | %-13s | %-14s |\n",
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getDate("fecha_registro"));
            }
            System.out.println("+----+----------------------+----------------------+---------------+----------------+");

        } catch (SQLException e) {
            System.err.println("Error al mostrar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarUsuario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el ID del usuario a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Ingrese el nuevo nombre: ");
        String nuevoNombre = scanner.nextLine();

        System.out.print("Ingrese el nuevo email: ");
        String nuevoEmail = scanner.nextLine();

        System.out.print("Ingrese el nuevo teléfono: ");
        String nuevoTelefono = scanner.nextLine();

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        String sql = "UPDATE usuarios SET nombre = ?, email = ?, telefono = ? WHERE id = ?";

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevoEmail);
            ps.setString(3, nuevoTelefono);
            ps.setInt(4, id);

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Usuario actualizado exitosamente.");
            } else {
                System.out.println("No se encontró un usuario con el ID especificado.");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarUsuario() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el ID del usuario a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            
            // Verificar si el usuario tiene préstamos activos
            String verificarSql = "SELECT COUNT(*) FROM prestamos WHERE usuario_id = ? AND estado = 'activo'";
            PreparedStatement verificarPs = conexion.prepareStatement(verificarSql);
            verificarPs.setInt(1, id);
            ResultSet rs = verificarPs.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("ERROR: No se puede eliminar el usuario porque tiene préstamos activos.");
                System.out.println("Debe devolver todos los libros antes de eliminar el usuario.");
                return;
            }
            
            // Si no tiene préstamos activos, proceder con la eliminación
            String eliminarSql = "DELETE FROM usuarios WHERE id = ?";
            PreparedStatement eliminarPs = conexion.prepareStatement(eliminarSql);
            eliminarPs.setInt(1, id);

            int filasEliminadas = eliminarPs.executeUpdate();
            if (filasEliminadas > 0) {
                System.out.println("Usuario eliminado exitosamente.");
            } else {
                System.out.println("No se encontró un usuario con el ID especificado.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos auxiliares
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

    public void menuLibros() {
        Scanner scanner = new Scanner(System.in);
        String opcion = "";
        final String volver_char = "5";

        do {
            System.out.println("\n====== Gestión de Libros ======");
            System.out.println("1. Agregar nuevo libro");
            System.out.println("2. Mostrar libros");
            System.out.println("3. Actualizar libro");
            System.out.println("4. Eliminar libro");
            System.out.println("5. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextLine();
           
            switch (opcion) {
                case "1":
                    agregarLibro();
                    break;
                case "2":
                    mostrarLibros();
                    break;
                case "3":
                    actualizarLibro();
                    break;
                case "4":
                    eliminarLibro();
                    break;
                case volver_char:
                    break;
                default:
                    System.out.println("Opción no válida, intente de nuevo.");
            }

        } while (!opcion.equals(volver_char));
    }

    public void agregarLibro() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine();

        System.out.print("Ingrese el autor del libro: ");
        String autor = scanner.nextLine();

        System.out.print("Ingrese el ISBN del libro: ");
        String isbn = scanner.nextLine();

        System.out.print("Ingrese el género del libro: ");
        String genero = scanner.nextLine();

        System.out.print("Ingrese el año de publicación: ");
        int anio = Integer.parseInt(scanner.nextLine());

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        String sql = "INSERT INTO libros (titulo, autor, isbn, genero, año_publicacion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, titulo);
            ps.setString(2, autor);
            ps.setString(3, isbn);
            ps.setString(4, genero);
            ps.setInt(5, anio);

            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Libro agregado exitosamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar libro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void mostrarLibros() {
        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        String sql = "SELECT * FROM libros ORDER BY id";

        System.out.println("\n====== Listado de Libros ======");
        System.out.println("+----+----------------------+----------------------+-------------------+----------+------+------------+");
        System.out.println("| ID | Título               | Autor                | ISBN              | Género   | Año  | Disponible |");
        System.out.println("+----+----------------------+----------------------+-------------------+----------+------+------------+");

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("| %-2d | %-20s | %-20s | %-17s | %-8s | %-4d | %-10s |\n",
                    rs.getInt("id"),
                    rs.getString("titulo").substring(0, Math.min(rs.getString("titulo").length(), 20)),
                    rs.getString("autor").substring(0, Math.min(rs.getString("autor").length(), 20)),
                    rs.getString("isbn"),
                    rs.getString("genero"),
                    rs.getInt("año_publicacion"),
                    rs.getBoolean("disponible") ? "Sí" : "No");
            }
            System.out.println("+----+----------------------+----------------------+-------------------+----------+------+------------+");

        } catch (SQLException e) {
            System.err.println("Error al mostrar libros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarLibro() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el ID del libro a actualizar: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Ingrese el nuevo título: ");
        String nuevoTitulo = scanner.nextLine();

        System.out.print("Ingrese el nuevo autor: ");
        String nuevoAutor = scanner.nextLine();

        System.out.print("Ingrese el nuevo ISBN: ");
        String nuevoIsbn = scanner.nextLine();

        System.out.print("Ingrese el nuevo género: ");
        String nuevoGenero = scanner.nextLine();

        System.out.print("Ingrese el nuevo año de publicación: ");
        int nuevoAnio = Integer.parseInt(scanner.nextLine());

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, genero = ?, año_publicacion = ? WHERE id = ?";

        try (Connection conexion = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevoTitulo);
            ps.setString(2, nuevoAutor);
            ps.setString(3, nuevoIsbn);
            ps.setString(4, nuevoGenero);
            ps.setInt(5, nuevoAnio);
            ps.setInt(6, id);

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Libro actualizado exitosamente.");
            } else {
                System.out.println("No se encontró un libro con el ID especificado.");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarLibro() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el ID del libro a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());

        Properties config = cargarConfiguracionDB();
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        try (Connection conexion = DriverManager.getConnection(url, user, password)) {
            
            // Verificar si el libro tiene préstamos activos
            String verificarSql = "SELECT COUNT(*) FROM prestamos WHERE libro_id = ? AND estado = 'activo'";
            PreparedStatement verificarPs = conexion.prepareStatement(verificarSql);
            verificarPs.setInt(1, id);
            ResultSet rs = verificarPs.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("ERROR: No se puede eliminar el libro porque está prestado actualmente.");
                System.out.println("Debe esperarse a que el libro sea devuelto antes de eliminarlo.");
                return;
            }
            
            // Si no tiene préstamos activos, proceder con la eliminación
            String eliminarSql = "DELETE FROM libros WHERE id = ?";
            PreparedStatement eliminarPs = conexion.prepareStatement(eliminarSql);
            eliminarPs.setInt(1, id);

            int filasEliminadas = eliminarPs.executeUpdate();
            if (filasEliminadas > 0) {
                System.out.println("Libro eliminado exitosamente.");
            } else {
                System.out.println("No se encontró un libro con el ID especificado.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void menuPrestamos() {
        System.out.println("Gestión de Préstamos - Por implementar");
    }
}