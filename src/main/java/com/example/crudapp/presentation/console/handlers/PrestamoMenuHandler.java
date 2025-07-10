package com.example.crudapp.presentation.console.handlers;

import com.example.crudapp.application.services.PrestamoService;
import com.example.crudapp.application.services.UsuarioService;
import com.example.crudapp.application.services.LibroService;
import com.example.crudapp.application.dto.PrestamoDTO;
import com.example.crudapp.domain.entities.Prestamo;
import com.example.crudapp.domain.entities.Usuario;
import com.example.crudapp.domain.entities.Libro;
import com.example.crudapp.presentation.utils.InputValidator;
import com.example.crudapp.presentation.utils.TableFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Handler para el menú de gestión de préstamos
 * Maneja todas las operaciones relacionadas con préstamos en la interfaz de consola
 * 
 * @author Tu Nombre
 * @version 1.0
 * @since 2025-01-01
 */
public class PrestamoMenuHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(PrestamoMenuHandler.class);
    
    private final PrestamoService prestamoService;
    private final UsuarioService usuarioService;
    private final LibroService libroService;
    private final Scanner scanner;
    private final InputValidator inputValidator;
    private final TableFormatter tableFormatter;
    
    /**
     * Constructor del handler de préstamos
     * 
     * @param prestamoService servicio de préstamos
     * @param usuarioService servicio de usuarios
     * @param libroService servicio de libros
     * @param scanner scanner para entrada de usuario
     * @param inputValidator validador de entrada
     */
    public PrestamoMenuHandler(PrestamoService prestamoService, UsuarioService usuarioService, 
                              LibroService libroService, Scanner scanner, InputValidator inputValidator) {
        this.prestamoService = prestamoService;
        this.usuarioService = usuarioService;
        this.libroService = libroService;
        this.scanner = scanner;
        this.inputValidator = inputValidator;
        this.tableFormatter = new TableFormatter();
    }
    
    /**
     * Muestra el menú principal de gestión de préstamos
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
     * Muestra las opciones del menú de préstamos
     */
    private void mostrarOpcionesMenu() {
        System.out.println("\n===== GESTIÓN DE PRÉSTAMOS =====");
        System.out.println("1. Crear Préstamo");
        System.out.println("2. Listar Préstamos");
        System.out.println("3. Buscar Préstamo por ID");
        System.out.println("4. Préstamos de un Usuario");
        System.out.println("5. Préstamos Vencidos");
        System.out.println("6. Devolver Libro");
        System.out.println("7. Renovar Préstamo");
        System.out.println("8. Actualizar Préstamos Vencidos");
        System.out.println("9. Volver al Menú Principal");
        System.out.println("================================");
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
                    crearPrestamo();
                    yield true;
                }
                case 2 -> {
                    listarPrestamos();
                    yield true;
                }
                case 3 -> {
                    buscarPrestamoPorId();
                    yield true;
                }
                case 4 -> {
                    listarPrestamosUsuario();
                    yield true;
                }
                case 5 -> {
                    listarPrestamosVencidos();
                    yield true;
                }
                case 6 -> {
                    devolverLibro();
                    yield true;
                }
                case 7 -> {
                    renovarPrestamo();
                    yield true;
                }
                case 8 -> {
                    actualizarPrestamosVencidos();
                    yield true;
                }
                case 9 -> {
                    logger.debug("Regresando al menú principal desde préstamos");
                    yield false;
                }
                default -> {
                    System.out.println("Opción no válida. Intente nuevamente.");
                    yield true;
                }
            };
        } catch (SQLException e) {
            logger.error("Error de base de datos en préstamos: {}", e.getMessage());
            System.err.println("Error de base de datos: " + e.getMessage());
            return true;
        } catch (Exception e) {
            logger.error("Error inesperado en préstamos: {}", e.getMessage(), e);
            System.err.println("Error inesperado: " + e.getMessage());
            return true;
        }
    }
    
    /**
     * Crea un nuevo préstamo
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void crearPrestamo() throws SQLException {
        System.out.println("\n--- CREAR PRÉSTAMO ---");
        
        // Buscar usuario
        Long usuarioId = inputValidator.leerLong("ID del usuario: ");
        Optional<Usuario> usuario = usuarioService.buscarPorId(usuarioId);
        
        if (usuario.isEmpty()) {
            System.out.println("❌ No se encontró un usuario con ID: " + usuarioId);
            return;
        }
        
        System.out.println("Usuario: " + usuario.get().getNombre());
        
        // Buscar libro
        Long libroId = inputValidator.leerLong("ID del libro: ");
        Optional<Libro> libro = libroService.buscarPorId(libroId);
        
        if (libro.isEmpty()) {
            System.out.println("❌ No se encontró un libro con ID: " + libroId);
            return;
        }
        
        System.out.println("Libro: " + libro.get().getTitulo());
        
        if (!libro.get().getDisponible()) {
            System.out.println("❌ El libro no está disponible para préstamo");
            return;
        }
        
        // Fecha de devolución esperada
        boolean usarFechaPorDefecto = inputValidator.leerSiNo("¿Usar fecha de devolución por defecto (14 días)?");
        LocalDate fechaDevolucionEsperada = null;
        
        if (!usarFechaPorDefecto) {
            fechaDevolucionEsperada = inputValidator.leerFecha("Fecha de devolución esperada");
        }
        
        // Observaciones opcionales
        String observaciones = inputValidator.leerCadenaOpcional("Observaciones (opcional): ");
        if (observaciones.isEmpty()) {
            observaciones = null;
        }
        
        PrestamoDTO prestamoDTO = new PrestamoDTO(usuarioId, libroId, fechaDevolucionEsperada);
        //prestamoDTO.setObservaciones(observaciones);
        
        try {
            Prestamo prestamo = prestamoService.crearPrestamo(prestamoDTO);
            System.out.println("✅ Préstamo creado exitosamente:");
            mostrarPrestamo(prestamo);
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    /**
     * Lista todos los préstamos
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void listarPrestamos() throws SQLException {
        System.out.println("\n--- LISTA DE PRÉSTAMOS ---");
        
        List<Prestamo> prestamos = prestamoService.obtenerTodos();
        
        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos registrados.");
            return;
        }
        
        System.out.println(String.format("Se encontraron %d préstamos:", prestamos.size()));
        tableFormatter.mostrarTablaPrestamos(prestamos);
    }
    
    /**
     * Busca un préstamo por ID
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void buscarPrestamoPorId() throws SQLException {
        System.out.println("\n--- BUSCAR PRÉSTAMO POR ID ---");
        
        Long id = inputValidator.leerLong("ID del préstamo: ");
        
        Optional<Prestamo> prestamo = prestamoService.buscarPorId(id);
        
        if (prestamo.isPresent()) {
            System.out.println("✅ Préstamo encontrado:");
            mostrarPrestamo(prestamo.get());
        } else {
            System.out.println("❌ No se encontró un préstamo con ID: " + id);
        }
    }
    
    /**
     * Lista los préstamos de un usuario específico
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void listarPrestamosUsuario() throws SQLException {
        System.out.println("\n--- PRÉSTAMOS DE UN USUARIO ---");
        
        Long usuarioId = inputValidator.leerLong("ID del usuario: ");
        
        Optional<Usuario> usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario.isEmpty()) {
            System.out.println("❌ No se encontró un usuario con ID: " + usuarioId);
            return;
        }
        
        System.out.println("Usuario: " + usuario.get().getNombre());
        
        List<Prestamo> prestamos = prestamoService.obtenerPrestamosActivosUsuario(usuarioId);
        
        if (prestamos.isEmpty()) {
            System.out.println("El usuario no tiene préstamos activos.");
        } else {
            System.out.println(String.format("Préstamos activos (%d):", prestamos.size()));
            tableFormatter.mostrarTablaPrestamos(prestamos);
        }
    }
    
    /**
     * Lista los préstamos vencidos
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void listarPrestamosVencidos() throws SQLException {
        System.out.println("\n--- PRÉSTAMOS VENCIDOS ---");
        
        List<Prestamo> prestamosVencidos = prestamoService.obtenerPrestamosVencidos();
        
        if (prestamosVencidos.isEmpty()) {
            System.out.println("✅ No hay préstamos vencidos.");
        } else {
            System.out.println(String.format("⚠️ Se encontraron %d préstamos vencidos:", prestamosVencidos.size()));
            tableFormatter.mostrarTablaPrestamos(prestamosVencidos);
        }
    }
    
    /**
     * Devuelve un libro prestado
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void devolverLibro() throws SQLException {
        System.out.println("\n--- DEVOLVER LIBRO ---");
        
        Long prestamoId = inputValidator.leerLong("ID del préstamo: ");
        
        Optional<Prestamo> prestamo = prestamoService.buscarPorId(prestamoId);
        
        if (prestamo.isEmpty()) {
            System.out.println("❌ No se encontró un préstamo con ID: " + prestamoId);
            return;
        }
        
        if (!"ACTIVO".equals(prestamo.get().getEstado())) {
            System.out.println("❌ El préstamo no está activo. Estado: " + prestamo.get().getEstado());
            return;
        }
        
        System.out.println("Préstamo a devolver:");
        mostrarPrestamo(prestamo.get());
        
        boolean usarFechaActual = inputValidator.leerSiNo("¿Usar fecha actual como fecha de devolución?");
        LocalDate fechaDevolucion = null;
        
        if (!usarFechaActual) {
            fechaDevolucion = inputValidator.leerFecha("Fecha de devolución");
        }
        
        String observaciones = inputValidator.leerCadenaOpcional("Observaciones de devolución (opcional): ");
        if (observaciones.isEmpty()) {
            observaciones = null;
        }
        
        try {
            prestamoService.devolverLibro(prestamoId, fechaDevolucion, observaciones);
            System.out.println("✅ Libro devuelto exitosamente");
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    /**
     * Renueva un préstamo existente
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void renovarPrestamo() throws SQLException {
        System.out.println("\n--- RENOVAR PRÉSTAMO ---");
        
        Long prestamoId = inputValidator.leerLong("ID del préstamo: ");
        
        Optional<Prestamo> prestamo = prestamoService.buscarPorId(prestamoId);
        
        if (prestamo.isEmpty()) {
            System.out.println("❌ No se encontró un préstamo con ID: " + prestamoId);
            return;
        }
        
        if (!"ACTIVO".equals(prestamo.get().getEstado())) {
            System.out.println("❌ Solo se pueden renovar préstamos activos. Estado: " + prestamo.get().getEstado());
            return;
        }
        
        System.out.println("Préstamo a renovar:");
        mostrarPrestamo(prestamo.get());
        
        int diasExtension = inputValidator.leerEntero("Días de extensión (1-30): ", 1, 30);
        
        try {
            prestamoService.renovarPrestamo(prestamoId, diasExtension);
            System.out.println(String.format("✅ Préstamo renovado exitosamente por %d días", diasExtension));
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza el estado de préstamos vencidos
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void actualizarPrestamosVencidos() throws SQLException {
        System.out.println("\n--- ACTUALIZAR PRÉSTAMOS VENCIDOS ---");
        
        System.out.println("Verificando préstamos vencidos...");
        
        int prestamosActualizados = prestamoService.actualizarPrestamosVencidos();
        
        if (prestamosActualizados == 0) {
            System.out.println("✅ No se encontraron préstamos vencidos para actualizar");
        } else {
            System.out.println(String.format("⚠️ Se marcaron %d préstamos como vencidos", prestamosActualizados));
        }
    }
    
    /**
     * Muestra los detalles de un préstamo con información adicional
     * 
     * @param prestamo préstamo a mostrar
     */
    private void mostrarPrestamo(Prestamo prestamo) {
        try {
            // Obtener información del usuario y libro
            Optional<Usuario> usuario = usuarioService.buscarPorId(prestamo.getUsuarioId());
            Optional<Libro> libro = libroService.buscarPorId(prestamo.getLibroId());
            
            String nombreUsuario = usuario.isPresent() ? usuario.get().getNombre() : "Usuario no encontrado";
            String tituloLibro = libro.isPresent() ? libro.get().getTitulo() : "Libro no encontrado";
            
            System.out.println(String.format("""
                ID: %d
                Usuario: %s (ID: %d)
                Libro: %s (ID: %d)
                Fecha préstamo: %s
                Fecha devolución esperada: %s
                Fecha devolución real: %s
                Estado: %s
                Observaciones: %s
                """, 
                prestamo.getId(),
                nombreUsuario, prestamo.getUsuarioId(),
                tituloLibro, prestamo.getLibroId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionEsperada(),
                prestamo.getFechaDevolucionReal() != null ? prestamo.getFechaDevolucionReal().toString() : "No devuelto",
                prestamo.getEstado()//,
                //prestamo.getObservaciones() != null ? prestamo.getObservaciones() : "Sin observaciones"
            ));
            
        } catch (SQLException e) {
            logger.error("Error al obtener información adicional del préstamo: {}", e.getMessage());
            System.out.println(String.format("""
                ID: %d
                Usuario ID: %d
                Libro ID: %d
                Fecha préstamo: %s
                Fecha devolución esperada: %s
                Fecha devolución real: %s
                Estado: %s
                Observaciones: %s
                """, 
                prestamo.getId(),
                prestamo.getUsuarioId(),
                prestamo.getLibroId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionEsperada(),
                prestamo.getFechaDevolucionReal() != null ? prestamo.getFechaDevolucionReal().toString() : "No devuelto",
                prestamo.getEstado()//,
                //prestamo.getObservaciones() != null ? prestamo.getObservaciones() : "Sin observaciones"
            ));
        }
    }
}