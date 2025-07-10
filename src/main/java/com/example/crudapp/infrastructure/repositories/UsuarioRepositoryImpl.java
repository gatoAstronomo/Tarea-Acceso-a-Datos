package com.example.crudapp.infrastructure.repositories;

import com.example.crudapp.domain.entities.Usuario;
import com.example.crudapp.domain.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Usuario
 * Maneja todas las operaciones CRUD para la entidad Usuario
 * 
 */
public class UsuarioRepositoryImpl implements UsuarioRepository {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioRepositoryImpl.class);
    
    // Queries SQL
    private static final String INSERT_USUARIO = 
        "INSERT INTO usuarios (nombre, email, telefono) VALUES (?, ?, ?)";
    private static final String SELECT_BY_ID = 
        "SELECT id, nombre, email, telefono, fecha_registro FROM usuarios WHERE id = ?";
    private static final String SELECT_ALL = 
        "SELECT id, nombre, email, telefono, fecha_registro FROM usuarios ORDER BY id";
    private static final String UPDATE_USUARIO = 
        "UPDATE usuarios SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
    private static final String DELETE_BY_ID = 
        "DELETE FROM usuarios WHERE id = ?";
    private static final String EXISTS_BY_ID = 
        "SELECT 1 FROM usuarios WHERE id = ?";
    private static final String SELECT_BY_EMAIL = 
        "SELECT id, nombre, email, telefono, fecha_registro FROM usuarios WHERE email = ?";
    private static final String EXISTS_BY_EMAIL = 
        "SELECT 1 FROM usuarios WHERE email = ?";
    private static final String SELECT_BY_NOMBRE = 
        "SELECT id, nombre, email, telefono, fecha_registro FROM usuarios WHERE nombre ILIKE ?";
    
    @Override
    public Usuario save(Connection connection, Usuario usuario) throws SQLException {
        logger.debug("Guardando usuario: {}", usuario.getEmail());
        
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_USUARIO, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefono());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear usuario, no se afectaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Error al crear usuario, no se obtuvo ID");
                }
            }
            
            logger.debug("Usuario guardado exitosamente con ID: {}", usuario.getId());
            return usuario;
        }
    }
    
    @Override
    public Optional<Usuario> findById(Connection connection, Long id) throws SQLException {
        logger.debug("Buscando usuario por ID: {}", id);
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);
                    logger.debug("Usuario encontrado: {}", usuario.getEmail());
                    return Optional.of(usuario);
                }
            }
        }
        
        logger.debug("Usuario no encontrado con ID: {}", id);
        return Optional.empty();
    }
    
    @Override
    public List<Usuario> findAll(Connection connection) throws SQLException {
        logger.debug("Obteniendo todos los usuarios");
        
        List<Usuario> usuarios = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        }
        
        logger.debug("Se encontraron {} usuarios", usuarios.size());
        return usuarios;
    }
    
    @Override
    public void update(Connection connection, Usuario usuario) throws SQLException {
        logger.debug("Actualizando usuario ID: {}", usuario.getId());
        
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_USUARIO)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefono());
            stmt.setLong(4, usuario.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar usuario, no se encontró el ID: " + usuario.getId());
            }
            
            logger.debug("Usuario actualizado exitosamente");
        }
    }
    
    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        logger.debug("Eliminando usuario ID: {}", id);
        
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_BY_ID)) {
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al eliminar usuario, no se encontró el ID: " + id);
            }
            
            logger.debug("Usuario eliminado exitosamente");
        }
    }
    
    @Override
    public boolean existsById(Connection connection, Long id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(EXISTS_BY_ID)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    @Override
    public Optional<Usuario> findByEmail(Connection connection, String email) throws SQLException {
        logger.debug("Buscando usuario por email: {}", email);
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_EMAIL)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);
                    logger.debug("Usuario encontrado por email");
                    return Optional.of(usuario);
                }
            }
        }
        
        logger.debug("Usuario no encontrado con email: {}", email);
        return Optional.empty();
    }
    
    @Override
    public boolean existsByEmail(Connection connection, String email) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(EXISTS_BY_EMAIL)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    @Override
    public List<Usuario> findByNombre(Connection connection, String nombre) throws SQLException {
        logger.debug("Buscando usuarios por nombre: {}", nombre);
        
        List<Usuario> usuarios = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_NOMBRE)) {
            stmt.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
        }
        
        logger.debug("Se encontraron {} usuarios con nombre '{}'", usuarios.size(), nombre);
        return usuarios;
    }
    
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
        return usuario;
    }
}