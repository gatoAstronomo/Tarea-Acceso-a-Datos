package com.example.crudapp.application.services;

import com.example.crudapp.domain.entities.Usuario;
import com.example.crudapp.domain.repositories.UsuarioRepository;
import com.example.crudapp.application.dto.UsuarioDTO;
import com.example.crudapp.infrastructure.transactions.TransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para gestión de usuarios
 * Coordina las operaciones de negocio relacionadas con usuarios
 * 
 */
public class UsuarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    
    private final UsuarioRepository usuarioRepository;
    private final TransactionManager transactionManager;
    
    /**
     * Constructor del servicio de usuarios
     * 
     * @param usuarioRepository repositorio de usuarios
     * @param transactionManager gestor de transacciones
     * @throws IllegalArgumentException si algún parámetro es null
     */
    public UsuarioService(UsuarioRepository usuarioRepository, TransactionManager transactionManager) {
        if (usuarioRepository == null) {
            throw new IllegalArgumentException("El repositorio de usuarios no puede ser null");
        }
        if (transactionManager == null) {
            throw new IllegalArgumentException("El gestor de transacciones no puede ser null");
        }
        
        this.usuarioRepository = usuarioRepository;
        this.transactionManager = transactionManager;
        
        logger.info("UsuarioService inicializado correctamente");
    }
    
    /**
     * Crea un nuevo usuario
     * 
     * @param usuarioDTO datos del usuario a crear
     * @return el usuario creado
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el DTO es null o inválido
     */
    public Usuario crearUsuario(UsuarioDTO usuarioDTO) throws SQLException {
        if (usuarioDTO == null) {
            throw new IllegalArgumentException("Los datos del usuario no pueden ser null");
        }
        
        logger.debug("Iniciando creación de usuario: {}", usuarioDTO);
        
        validarDatosUsuario(usuarioDTO);
        
        return transactionManager.executeInTransaction(connection -> {
            // Verificar que no exista un usuario con el mismo email
            Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(connection, usuarioDTO.getEmail());
            if (usuarioExistente.isPresent()) {
                String mensaje = String.format("Ya existe un usuario con el email: %s", usuarioDTO.getEmail());
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            Usuario usuario = new Usuario();
            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setTelefono(usuarioDTO.getTelefono());
            usuario.setFechaRegistro(null); // Se asigna automáticamente en el repositorio
            
            Usuario usuarioCreado = usuarioRepository.save(connection, usuario);
            logger.info("Usuario creado exitosamente con ID: {}", usuarioCreado.getId());
            
            return usuarioCreado;
        });
    }
    
    /**
     * Busca un usuario por su ID
     * 
     * @param id identificador del usuario
     * @return el usuario encontrado o Optional.empty()
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es null
     */
    public Optional<Usuario> buscarPorId(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser null");
        }
        
        logger.debug("Buscando usuario con ID: {}", id);
        
        Optional<Usuario> usuario = transactionManager.executeInTransaction(connection -> 
            usuarioRepository.findById(connection, id)
        );
        
        if (usuario.isPresent()) {
            logger.debug("Usuario encontrado: {}", usuario.get());
        } else {
            logger.debug("No se encontró usuario con ID: {}", id);
        }
        
        return usuario;
    }
    
    /**
     * Obtiene todos los usuarios
     * 
     * @return lista de todos los usuarios
     * @throws SQLException si ocurre un error de base de datos
     */
    public List<Usuario> obtenerTodos() throws SQLException {
        logger.debug("Obteniendo todos los usuarios");
        
        List<Usuario> usuarios = transactionManager.executeInTransaction(connection -> 
            usuarioRepository.findAll(connection)
        );
        
        logger.info("Se encontraron {} usuarios", usuarios.size());
        return usuarios;
    }
    
    /**
     * Actualiza un usuario existente
     * 
     * @param id identificador del usuario a actualizar
     * @param usuarioDTO nuevos datos del usuario
     * @return el usuario actualizado
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public void actualizarUsuario(Long id, UsuarioDTO usuarioDTO) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser null");
        }
        if (usuarioDTO == null) {
            throw new IllegalArgumentException("Los datos del usuario no pueden ser null");
        }
        
        logger.debug("Actualizando usuario con ID: {} - Datos: {}", id, usuarioDTO);
        
        validarDatosUsuario(usuarioDTO);
        
        transactionManager.executeInTransactionVoid(connection -> {
            Optional<Usuario> usuarioExistente = usuarioRepository.findById(connection, id);
            if (usuarioExistente.isEmpty()) {
                String mensaje = String.format("No existe un usuario con ID: %d", id);
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            Usuario usuario = usuarioExistente.get();
            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setTelefono(usuarioDTO.getTelefono());
            
            usuarioRepository.update(connection, usuario);
            logger.info("Usuario actualizado exitosamente: {}", usuario.getId());

        });
    }
    
    /**
     * Elimina un usuario por su ID
     * 
     * @param id identificador del usuario a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es null
     */
    public void eliminarUsuario(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser null");
        }
        
        logger.debug("Eliminando usuario con ID: {}", id);
        
        transactionManager.executeInTransactionVoid(connection -> {
            Optional<Usuario> usuario = usuarioRepository.findById(connection, id);
            if (usuario.isEmpty()) {
                String mensaje = String.format("No existe un usuario con ID: %d", id);
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            usuarioRepository.deleteById(connection, id);

        });
    }
    
    /**
     * Valida los datos del usuario
     * 
     * @param usuarioDTO datos a validar
     * @throws IllegalArgumentException si los datos son inválidos
     */
    private void validarDatosUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getNombre() == null || usuarioDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        }
        
        if (usuarioDTO.getEmail() == null || usuarioDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del usuario no puede estar vacío");
        }
        
        if (!usuarioDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            String mensaje = String.format("El formato del email es inválido: %s", usuarioDTO.getEmail());
            logger.warn(mensaje);
            throw new IllegalArgumentException(mensaje);
        }
        
        logger.debug("Validación exitosa para usuario: {}", usuarioDTO.getNombre());
    }
}
