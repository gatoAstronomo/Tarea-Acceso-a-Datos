package com.example.crudapp;

import com.example.crudapp.infrastructure.transactions.TransactionManager;
import com.example.crudapp.infrastructure.repositories.UsuarioRepositoryImpl;
import com.example.crudapp.infrastructure.database.Database;
import com.example.crudapp.infrastructure.database.DatabaseInitializer;
import com.example.crudapp.infrastructure.repositories.LibroRepositoryImpl;
import com.example.crudapp.infrastructure.repositories.PrestamoRepositoryImpl;
import com.example.crudapp.application.services.UsuarioService;
import com.example.crudapp.application.services.LibroService;
import com.example.crudapp.application.services.PrestamoService;
import com.example.crudapp.presentation.console.ConsoleUI;

import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Clase principal de la aplicación
 * Punto de entrada que configura las dependencias e inicia la interfaz de
 * usuario
 * 
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Punto de entrada de la aplicación
     * 
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        try {
            // Configurar dependencias
            ApplicationContext context = createApplicationContext();

            // Programar actualización diaria de préstamos vencidos
            programarActualizacionVencidos(context.getPrestamoService());

            // Iniciar interfaz de usuario
            ConsoleUI consoleUI = new ConsoleUI(context);
            consoleUI.iniciar();

            // Al salir del menú de la consola termina el pool de conexiones a la base de datos
            Database.getInstance().close();

        } catch (SQLException e) {
            logger.error("Error al inicializar la aplicación: {}", e.getMessage());
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            System.err.println("Error inesperado: " + e.getMessage());
            System.exit(1);
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    /**
     * Crea y configura el contexto de la aplicación con todas las dependencias
     * 
     * @return contexto de aplicación configurado
     * @throws SQLException si hay error en la configuración de base de datos
     */
    private static ApplicationContext createApplicationContext() throws SQLException {
        logger.info("Inicializando contexto de aplicación...");

        // Configura las conexiones a la base de datos
        Database database = Database.getInstance();

        // Inicializa la base de datos
        DatabaseInitializer dataInitializer = new DatabaseInitializer(database);
        dataInitializer.initializeDatabase();

        // Gestor de transacciones
        TransactionManager transactionManager = new TransactionManager(database);

        // Repositorios
        UsuarioRepositoryImpl usuarioRepository = new UsuarioRepositoryImpl();
        LibroRepositoryImpl libroRepository = new LibroRepositoryImpl();
        PrestamoRepositoryImpl prestamoRepository = new PrestamoRepositoryImpl();

        // Servicios
        UsuarioService usuarioService = new UsuarioService(usuarioRepository, transactionManager);
        LibroService libroService = new LibroService(libroRepository, transactionManager);
        PrestamoService prestamoService = new PrestamoService(prestamoRepository, usuarioRepository, libroRepository,
                transactionManager);

        // Actualizar préstamos vencidos al iniciar
        int vencidos = prestamoService.actualizarPrestamosVencidos();
        if (vencidos > 0) {
            System.out.println("Se actualizaron " + vencidos + " préstamos a estado VENCIDO");
        }

        ApplicationContext context = new ApplicationContext(
                usuarioService,
                libroService,
                prestamoService);

        logger.info("Contexto de aplicación inicializado correctamente");
        return context;
    }

    /**
     * Contexto de aplicación que contiene todas las dependencias
     * Simple contenedor de servicios para dependency injection manual
     *
     */
    public static class ApplicationContext {

        private final UsuarioService usuarioService;
        private final LibroService libroService;
        private final PrestamoService prestamoService;

        /**
         * Constructor del contexto de aplicación
         * 
         * @param usuarioService  servicio de usuarios
         * @param libroService    servicio de libros
         * @param prestamoService servicio de préstamos
         */
        public ApplicationContext(UsuarioService usuarioService,
                LibroService libroService,
                PrestamoService prestamoService) {
            this.usuarioService = usuarioService;
            this.libroService = libroService;
            this.prestamoService = prestamoService;
        }

        /**
         * Obtiene el servicio de usuarios
         * 
         * @return servicio de usuarios
         */
        public UsuarioService getUsuarioService() {
            return usuarioService;
        }

        /**
         * Obtiene el servicio de libros
         * 
         * @return servicio de libros
         */
        public LibroService getLibroService() {
            return libroService;
        }

        /**
         * Obtiene el servicio de préstamos
         * 
         * @return servicio de préstamos
         */
        public PrestamoService getPrestamoService() {
            return prestamoService;
        }
    }

    /**
     * Programa la actualización de préstamos vencidos para ejecutarse a medianoche
     */
    private static void programarActualizacionVencidos(PrestamoService prestamoService) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Calcular tiempo hasta la próxima medianoche
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proximaMedianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
        long segundosHastaMedianoche = ChronoUnit.SECONDS.between(ahora, proximaMedianoche);

        // Programar la tarea
        scheduler.scheduleAtFixedRate(() -> {
            try {
                int actualizados = prestamoService.actualizarPrestamosVencidos();
                if (actualizados > 0) {
                    System.out.println("🕛 [Tarea programada] " + actualizados +
                            " préstamos marcados como VENCIDOS");
                }
            } catch (Exception e) {
                System.err.println("Error en tarea programada: " + e.getMessage());
            }
        }, segundosHastaMedianoche, 86400, TimeUnit.SECONDS); // 86400 segundos = 1 día

        logger.info("Actualización de préstamos vencidos programada para ejecutarse a medianoche");
    }
}