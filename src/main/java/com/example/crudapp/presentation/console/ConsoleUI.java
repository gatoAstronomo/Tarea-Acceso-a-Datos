package com.example.crudapp.presentation.console;

import com.example.crudapp.Main.ApplicationContext;
import com.example.crudapp.presentation.console.handlers.UsuarioMenuHandler;
import com.example.crudapp.presentation.console.handlers.LibroMenuHandler;
import com.example.crudapp.presentation.console.handlers.PrestamoMenuHandler;
import com.example.crudapp.presentation.utils.InputValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Interfaz de usuario en consola
 * Maneja el menú principal y delega operaciones específicas a los handlers
 * 
 * @author Tu Nombre
 * @version 1.0
 * @since 2025-01-01
 */
public class ConsoleUI {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);
    
    private final ApplicationContext context;
    private final Scanner scanner;
    private final InputValidator inputValidator;
    
    // Handlers especializados
    private final UsuarioMenuHandler usuarioMenuHandler;
    private final LibroMenuHandler libroMenuHandler;
    private final PrestamoMenuHandler prestamoMenuHandler;
    
    /**
     * Constructor de la interfaz de consola
     * 
     * @param context contexto de aplicación con los servicios
     */
    public ConsoleUI(ApplicationContext context) {
        this.context = context;
        this.scanner = new Scanner(System.in);
        this.inputValidator = new InputValidator();
        
        // Inicializar handlers
        this.usuarioMenuHandler = new UsuarioMenuHandler(context.getUsuarioService(), inputValidator);
        this.libroMenuHandler = new LibroMenuHandler(context.getLibroService(), inputValidator);
        this.prestamoMenuHandler = new PrestamoMenuHandler(
            context.getPrestamoService(),
            context.getUsuarioService(),
            context.getLibroService(),
            inputValidator
        );
        
        logger.info("ConsoleUI inicializada correctamente");
    }
    
    /**
     * Inicia la interfaz de usuario
     */
    public void iniciar() {
        context.getClass(); //Dummy call para evitar warning de unused parameter
        logger.info("Iniciando interfaz de usuario");
        
        mostrarBienvenida();
        menuPrincipal();
        
        logger.info("Interfaz de usuario terminada");
    }
    
    /**
     * Muestra el mensaje de bienvenida
     */
    private void mostrarBienvenida() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    SISTEMA DE GESTIÓN DE BIBLIOTECA");
        System.out.println("=".repeat(50));
        System.out.println("Bienvenido al sistema de gestión de biblioteca");
        System.out.println("Versión 1.0\n");
    }
    
    /**
     * Maneja el menú principal de la aplicación
     */
    public void menuPrincipal() {
        boolean continuar = true;
        
        while (continuar) {
            mostrarMenuPrincipal();
            
            int opcion = inputValidator.leerEntero("Seleccione una opción: ", 1, 4);
            
            continuar = procesarOpcionMenuPrincipal(opcion);
        }
        
        mostrarDespedida();
        scanner.close();
    }
    
    /**
     * Muestra las opciones del menú principal
     */
    private void mostrarMenuPrincipal() {
        System.out.println("\n====== MENÚ PRINCIPAL ======");
        System.out.println("1. Gestión de Usuarios");
        System.out.println("2. Gestión de Libros");
        System.out.println("3. Gestión de Préstamos");
        System.out.println("4. Salir");
        System.out.println("============================");
    }
    
    /**
     * Procesa la opción seleccionada del menú principal
     * 
     * @param opcion opción seleccionada
     * @return true si debe continuar, false si debe salir
     */
    private boolean procesarOpcionMenuPrincipal(int opcion) {
        return switch (opcion) {
            case 1 -> {
                logger.debug("Accediendo a gestión de usuarios");
                usuarioMenuHandler.mostrarMenu();
                yield true;
            }
            case 2 -> {
                logger.debug("Accediendo a gestión de libros");
                libroMenuHandler.mostrarMenu();
                yield true;
            }
            case 3 -> {
                logger.debug("Accediendo a gestión de préstamos");
                prestamoMenuHandler.mostrarMenu();
                yield true;
            }
            case 4 -> {
                logger.info("Usuario solicitó salir del sistema");
                yield false;
            }
            default -> {
                System.out.println("Opción no válida. Intente nuevamente.");
                yield true;
            }
        };
    }
    
    /**
     * Muestra el mensaje de despedida
     */
    private void mostrarDespedida() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    GRACIAS POR USAR EL SISTEMA");
        System.out.println("=".repeat(50));
        System.out.println("¡Hasta pronto!\n");
    }
}