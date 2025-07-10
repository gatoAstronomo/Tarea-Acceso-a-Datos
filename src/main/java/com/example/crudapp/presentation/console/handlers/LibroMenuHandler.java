package com.example.crudapp.presentation.console.handlers;

import com.example.crudapp.application.services.LibroService;
import com.example.crudapp.application.dto.LibroDTO;
import com.example.crudapp.domain.entities.Libro;
import com.example.crudapp.presentation.utils.InputValidator;
import com.example.crudapp.presentation.utils.TableFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Handler para el menú de gestión de libros
 * Maneja todas las operaciones relacionadas con libros en la interfaz de consola
 * 
 */
public class LibroMenuHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(LibroMenuHandler.class);
    
    private final LibroService libroService;
    private final InputValidator inputValidator;
    private final TableFormatter tableFormatter;
    
    /**
     * Constructor del handler de libros
     * 
     * @param libroService servicio de libros
     * @param scanner scanner para entrada de usuario
     * @param inputValidator validador de entrada
     */
    public LibroMenuHandler(LibroService libroService, InputValidator inputValidator) {
        this.libroService = libroService;
        this.inputValidator = inputValidator;
        this.tableFormatter = new TableFormatter();
    }
    
    /**
     * Muestra el menú principal de gestión de libros
     */
    public void mostrarMenu() {
        boolean continuar = true;
        
        while (continuar) {
            mostrarOpcionesMenu();
            
            int opcion = inputValidator.leerEntero("Seleccione una opción: ", 1, 9);
            
            continuar = procesarOpcionMenu(opcion);
        }
    }
    
    /**
     * Muestra las opciones del menú de libros
     */
    private void mostrarOpcionesMenu() {
        System.out.println("\n===== GESTIÓN DE LIBROS =====");
        System.out.println("1. Crear Libro");
        System.out.println("2. Listar Libros");
        System.out.println("3. Buscar por ID");
        System.out.println("4. Buscar por ISBN");
        System.out.println("5. Buscar por Título");
        System.out.println("6. Buscar por Autor");
        System.out.println("7. Actualizar Libro");
        System.out.println("8. Eliminar Libro");
        System.out.println("9. Volver al Menú Principal");
        System.out.println("=============================");
    }
    
    /**
     * Procesa la opción seleccionada del menú
     * 
     * @param opcion opción seleccionada
     * @return true si debe continuar, false si debe volver al menú principal
     */
    private boolean procesarOpcionMenu(int opcion) {
        try {
            return switch (opcion) {
                case 1 -> {
                    crearLibro();
                    yield true;
                }
                case 2 -> {
                    listarLibros();
                    yield true;
                }
                case 3 -> {
                    buscarLibroPorId();
                    yield true;
                }
                case 4 -> {
                    buscarLibroPorIsbn();
                    yield true;
                }
                case 5 -> {
                    buscarLibrosPorTitulo();
                    yield true;
                }
                case 6 -> {
                    buscarLibrosPorAutor();
                    yield true;
                }
                case 7 -> {
                    actualizarLibro();
                    yield true;
                }
                case 8 -> {
                    eliminarLibro();
                    yield true;
                }
                case 9 -> {
                    logger.debug("Regresando al menú principal desde libros");
                    yield false;
                }
                default -> {
                    System.out.println("Opción no válida. Intente nuevamente.");
                    yield true;
                }
            };
        } catch (SQLException e) {
            logger.error("Error de base de datos en libros: {}", e.getMessage());
            System.err.println("Error de base de datos: " + e.getMessage());
            return true;
        } catch (Exception e) {
            logger.error("Error inesperado en libros: {}", e.getMessage(), e);
            System.err.println("Error inesperado: " + e.getMessage());
            return true;
        }
    }
    
    /**
     * Crea un nuevo libro
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void crearLibro() throws SQLException {
        System.out.println("\n--- CREAR LIBRO ---");
        
        String titulo = inputValidator.leerCadenaNoVacia("Título: ");
        String autor = inputValidator.leerCadenaNoVacia("Autor: ");
        String isbn = inputValidator.leerCadenaNoVacia("ISBN: ");
        String genero = inputValidator.leerCadenaNoVacia("Género: ");
        Integer añoPublicacion = inputValidator.leerEntero("Año de publicación: ", 1000, 2025);
        
        LibroDTO libroDTO = new LibroDTO(titulo, autor, isbn, genero, añoPublicacion);
        
        try {
            Libro libro = libroService.crearLibro(libroDTO);
            System.out.println("✅ Libro creado exitosamente:");
            mostrarLibro(libro);
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    /**
     * Lista todos los libros
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void listarLibros() throws SQLException {
        System.out.println("\n--- LISTA DE LIBROS ---");
        
        List<Libro> libros = libroService.obtenerTodos();
        
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        
        System.out.println(String.format("Se encontraron %d libros:", libros.size()));
        tableFormatter.mostrarTablaLibros(libros);
    }
    
    /**
     * Busca un libro por ID
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void buscarLibroPorId() throws SQLException {
        System.out.println("\n--- BUSCAR LIBRO POR ID ---");
        
        Long id = inputValidator.leerLong("ID del libro: ");
        
        Optional<Libro> libro = libroService.buscarPorId(id);
        
        if (libro.isPresent()) {
            System.out.println("✅ Libro encontrado:");
            mostrarLibro(libro.get());
        } else {
            System.out.println("❌ No se encontró un libro con ID: " + id);
        }
    }
    
    /**
     * Busca un libro por ISBN
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void buscarLibroPorIsbn() throws SQLException {
        System.out.println("\n--- BUSCAR LIBRO POR ISBN ---");
        
        String isbn = inputValidator.leerCadenaNoVacia("ISBN: ");
        
        Optional<Libro> libro = libroService.buscarPorIsbn(isbn);
        
        if (libro.isPresent()) {
            System.out.println("✅ Libro encontrado:");
            mostrarLibro(libro.get());
        } else {
            System.out.println("❌ No se encontró un libro con ISBN: " + isbn);
        }
    }
    
    /**
     * Busca libros por título
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void buscarLibrosPorTitulo() throws SQLException {
        System.out.println("\n--- BUSCAR LIBROS POR TÍTULO ---");
        
        String titulo = inputValidator.leerCadenaNoVacia("Título (o parte del título): ");
        
        List<Libro> libros = libroService.buscarPorTitulo(titulo);
        
        if (libros.isEmpty()) {
            System.out.println("❌ No se encontraron libros con el título: " + titulo);
        } else {
            System.out.println(String.format("✅ Se encontraron %d libros:", libros.size()));
            tableFormatter.mostrarTablaLibros(libros);
        }
    }
    
    /**
     * Busca libros por autor
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void buscarLibrosPorAutor() throws SQLException {
        System.out.println("\n--- BUSCAR LIBROS POR AUTOR ---");
        
        String autor = inputValidator.leerCadenaNoVacia("Autor (o parte del autor): ");
        
        List<Libro> libros = libroService.buscarPorAutor(autor);
        
        if (libros.isEmpty()) {
            System.out.println("❌ No se encontraron libros del autor: " + autor);
        } else {
            System.out.println(String.format("✅ Se encontraron %d libros:", libros.size()));
            tableFormatter.mostrarTablaLibros(libros);
        }
    }
    
    /**
     * Actualiza un libro existente
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void actualizarLibro() throws SQLException {
        System.out.println("\n--- ACTUALIZAR LIBRO ---");
        
        Long id = inputValidator.leerLong("ID del libro a actualizar: ");
        
        Optional<Libro> libroExistente = libroService.buscarPorId(id);
        
        if (libroExistente.isEmpty()) {
            System.out.println("❌ No se encontró un libro con ID: " + id);
            return;
        }
        
        Libro libro = libroExistente.get();
        System.out.println("Libro actual:");
        mostrarLibro(libro);
        
        System.out.println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):");
        
        String titulo = inputValidator.leerCadenaOpcional("Título (" + libro.getTitulo() + "): ");
        if (titulo.isEmpty()) titulo = libro.getTitulo();
        
        String autor = inputValidator.leerCadenaOpcional("Autor (" + libro.getAutor() + "): ");
        if (autor.isEmpty()) autor = libro.getAutor();
        
        String isbn = inputValidator.leerCadenaOpcional("ISBN (" + libro.getIsbn() + "): ");
        if (isbn.isEmpty()) isbn = libro.getIsbn();
        
        String genero = inputValidator.leerCadenaOpcional("Género (" + libro.getGenero() + "): ");
        if (genero.isEmpty()) genero = libro.getGenero();
        
        Integer añoPublicacion = inputValidator.leerEnteroOpcional("Año de publicación (" + libro.getAñoPublicacion() + "): ", 1000, 2025);
        if (añoPublicacion == null) añoPublicacion = libro.getAñoPublicacion();
        
        LibroDTO libroDTO = new LibroDTO(titulo, autor, isbn, genero, añoPublicacion, libro.getDisponible());
        
        try {
            libroService.actualizarLibro(id, libroDTO);
            System.out.println("✅ Libro actualizado exitosamente");
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un libro
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void eliminarLibro() throws SQLException {
        System.out.println("\n--- ELIMINAR LIBRO ---");
        
        Long id = inputValidator.leerLong("ID del libro a eliminar: ");
        
        Optional<Libro> libro = libroService.buscarPorId(id);
        
        if (libro.isEmpty()) {
            System.out.println("❌ No se encontró un libro con ID: " + id);
            return;
        }
        
        System.out.println("Libro a eliminar:");
        mostrarLibro(libro.get());
        
        boolean confirmar = inputValidator.leerSiNo("¿Está seguro de que desea eliminar este libro?");
        
        if (confirmar) {
            try {
                libroService.eliminarLibro(id);
                System.out.println("✅ Libro eliminado exitosamente");
                
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
        } else {
            System.out.println("Eliminación cancelada");
        }
    }
    
    /**
     * Muestra los detalles de un libro
     * 
     * @param libro libro a mostrar
     */
    private void mostrarLibro(Libro libro) {
        System.out.println(String.format("""
            ID: %d
            Título: %s
            Autor: %s
            ISBN: %s
            Género: %s
            Año de publicación: %d
            Disponible: %s
            """, 
            libro.getId(), 
            libro.getTitulo(), 
            libro.getAutor(), 
            libro.getIsbn(), 
            libro.getGenero(), 
            libro.getAñoPublicacion(), 
            libro.getDisponible() ? "Sí" : "No"
        ));
    }
}
