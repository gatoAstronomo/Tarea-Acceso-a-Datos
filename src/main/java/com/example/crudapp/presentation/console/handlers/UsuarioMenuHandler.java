package com.example.crudapp.presentation.console.handlers;

import com.example.crudapp.application.services.UsuarioService;
import com.example.crudapp.application.dto.UsuarioDTO;
import com.example.crudapp.domain.entities.Usuario;
import com.example.crudapp.presentation.utils.InputValidator;
import com.example.crudapp.presentation.utils.TableFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Handler para el menú de gestión de usuarios
 * Maneja todas las operaciones relacionadas con usuarios en la interfaz de consola
 * 
 */
public class UsuarioMenuHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioMenuHandler.class);
    
    private final UsuarioService usuarioService;
    private final InputValidator inputValidator;
    private final TableFormatter tableFormatter;
    
    /**
     * Constructor del handler de usuarios
     * 
     * @param usuarioService servicio de usuarios
     * @param scanner scanner para entrada de usuario
     * @param inputValidator validador de entrada
     */
    public UsuarioMenuHandler(UsuarioService usuarioService, InputValidator inputValidator) {
        this.usuarioService = usuarioService;
        this.inputValidator = inputValidator;
        this.tableFormatter = new TableFormatter();
    }
    
    /**
     * Muestra el menú principal de gestión de usuarios
     */
    public void mostrarMenu() {
        boolean continuar = true;
        
        while (continuar) {
            mostrarOpcionesMenu();
            
            int opcion = inputValidator.leerEntero("Seleccione una opción: ", 1, 6);
            
            continuar = procesarOpcionMenu(opcion);
        }
    }
    
    /**
     * Muestra las opciones del menú de usuarios
     */
    private void mostrarOpcionesMenu() {
        System.out.println("\n===== GESTIÓN DE USUARIOS =====");
        System.out.println("1. Crear Usuario");
        System.out.println("2. Listar Usuarios");
        System.out.println("3. Buscar Usuario");
        System.out.println("4. Actualizar Usuario");
        System.out.println("5. Eliminar Usuario");
        System.out.println("6. Volver al Menú Principal");
        System.out.println("===============================");
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
                    crearUsuario();
                    yield true;
                }
                case 2 -> {
                    listarUsuarios();
                    yield true;
                }
                case 3 -> {
                    buscarUsuario();
                    yield true;
                }
                case 4 -> {
                    actualizarUsuario();
                    yield true;
                }
                case 5 -> {
                    eliminarUsuario();
                    yield true;
                }
                case 6 -> {
                    logger.debug("Regresando al menú principal desde usuarios");
                    yield false;
                }
                default -> {
                    System.out.println("Opción no válida. Intente nuevamente.");
                    yield true;
                }
            };
        } catch (SQLException e) {
            logger.error("Error de base de datos en usuarios: {}", e.getMessage());
            System.err.println("Error de base de datos: " + e.getMessage());
            return true;
        } catch (Exception e) {
            logger.error("Error inesperado en usuarios: {}", e.getMessage(), e);
            System.err.println("Error inesperado: " + e.getMessage());
            return true;
        }
    }
    
    /**
     * Crea un nuevo usuario
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void crearUsuario() throws SQLException {
        System.out.println("\n--- CREAR USUARIO ---");
        
        String nombre = inputValidator.leerCadenaNoVacia("Nombre: ");
        String email = inputValidator.leerEmail("Email: ");
        String telefono = inputValidator.leerTelefono("Teléfono: ");
        
        UsuarioDTO usuarioDTO = new UsuarioDTO(nombre, email, telefono);
        
        try {
            Usuario usuario = usuarioService.crearUsuario(usuarioDTO);
            System.out.println("✅ Usuario creado exitosamente:");
            mostrarUsuario(usuario);
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    /**
     * Lista todos los usuarios
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void listarUsuarios() throws SQLException {
        System.out.println("\n--- LISTA DE USUARIOS ---");
        
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        
        System.out.println(String.format("Se encontraron %d usuarios:", usuarios.size()));
        tableFormatter.mostrarTablaUsuarios(usuarios);
    }
    
    /**
     * Busca un usuario por ID
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void buscarUsuario() throws SQLException {
        System.out.println("\n--- BUSCAR USUARIO ---");
        
        Long id = inputValidator.leerLong("ID del usuario: ");
        
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        
        if (usuario.isPresent()) {
            System.out.println("✅ Usuario encontrado:");
            mostrarUsuario(usuario.get());
        } else {
            System.out.println("❌ No se encontró un usuario con ID: " + id);
        }
    }
    
    /**
     * Actualiza un usuario existente
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void actualizarUsuario() throws SQLException {
        System.out.println("\n--- ACTUALIZAR USUARIO ---");
        
        Long id = inputValidator.leerLong("ID del usuario a actualizar: ");
        
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorId(id);
        
        if (usuarioExistente.isEmpty()) {
            System.out.println("❌ No se encontró un usuario con ID: " + id);
            return;
        }
        
        Usuario usuario = usuarioExistente.get();
        System.out.println("Usuario actual:");
        mostrarUsuario(usuario);
        
        System.out.println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):");
        
        String nombre = inputValidator.leerCadenaOpcional("Nombre (" + usuario.getNombre() + "): ");
        if (nombre.isEmpty()) nombre = usuario.getNombre();
        
        String email = inputValidator.leerEmailOpcional("Email (" + usuario.getEmail() + "): ");
        if (email.isEmpty()) email = usuario.getEmail();
        
        String telefono = inputValidator.leerTelefonoOpcional("Teléfono (" + usuario.getTelefono() + "): ");
        if (telefono.isEmpty()) telefono = usuario.getTelefono();
        
        UsuarioDTO usuarioDTO = new UsuarioDTO(nombre, email, telefono);
        
        try {
            usuarioService.actualizarUsuario(id, usuarioDTO);
            System.out.println("✅ Usuario actualizado exitosamente:");
            mostrarUsuario(usuarioService.buscarPorId(id).get());
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un usuario
     * 
     * @throws SQLException si hay error en la base de datos
     */
    private void eliminarUsuario() throws SQLException {
        System.out.println("\n--- ELIMINAR USUARIO ---");
        
        Long id = inputValidator.leerLong("ID del usuario a eliminar: ");
        
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        
        if (usuario.isEmpty()) {
            System.out.println("❌ No se encontró un usuario con ID: " + id);
            return;
        }
        
        System.out.println("Usuario a eliminar:");
        mostrarUsuario(usuario.get());
        
        boolean confirmar = inputValidator.leerSiNo("¿Está seguro de que desea eliminar este usuario?");
        
        if (confirmar) {
            try {
                usuarioService.eliminarUsuario(id);
                System.out.println("✅ Usuario eliminado exitosamente.");
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
        } else {
            System.out.println("Eliminación cancelada");
        }
    }
    
    /**
     * Muestra los detalles de un usuario
     * 
     * @param usuario usuario a mostrar
     */
    private void mostrarUsuario(Usuario usuario) {
        System.out.println(String.format("""
            ID: %d
            Nombre: %s
            Email: %s
            Teléfono: %s
            """, 
            usuario.getId(), 
            usuario.getNombre(), 
            usuario.getEmail(), 
            usuario.getTelefono()
        ));
    }
}
