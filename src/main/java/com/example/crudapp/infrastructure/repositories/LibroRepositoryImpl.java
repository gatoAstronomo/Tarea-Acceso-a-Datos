package com.example.crudapp.infrastructure.repositories;

import com.example.crudapp.domain.entities.Libro;
import com.example.crudapp.domain.repositories.LibroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Libro
 * Maneja todas las operaciones CRUD para la entidad Libro
 * 
 */
public class LibroRepositoryImpl implements LibroRepository {
    private static final Logger logger = LoggerFactory.getLogger(LibroRepositoryImpl.class);
    
    // Queries SQL
    private static final String INSERT_LIBRO = 
        "INSERT INTO libros (titulo, autor, isbn, genero, año_publicacion, disponible) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID = 
        "SELECT id, titulo, autor, isbn, genero, año_publicacion, disponible FROM libros WHERE id = ?";
    private static final String SELECT_ALL = 
        "SELECT id, titulo, autor, isbn, genero, año_publicacion, disponible FROM libros ORDER BY id";
    private static final String UPDATE_LIBRO = 
        "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, genero = ?, año_publicacion = ?, disponible = ? WHERE id = ?";
    private static final String DELETE_BY_ID = 
        "DELETE FROM libros WHERE id = ?";
    private static final String EXISTS_BY_ID = 
        "SELECT 1 FROM libros WHERE id = ?";
    private static final String SELECT_BY_ISBN = 
        "SELECT id, titulo, autor, isbn, genero, año_publicacion, disponible FROM libros WHERE isbn = ?";
    private static final String EXISTS_BY_ISBN = 
        "SELECT 1 FROM libros WHERE isbn = ?";
    private static final String SELECT_BY_TITULO = 
        "SELECT id, titulo, autor, isbn, genero, año_publicacion, disponible FROM libros WHERE titulo ILIKE ?";
    private static final String SELECT_BY_AUTOR = 
        "SELECT id, titulo, autor, isbn, genero, año_publicacion, disponible FROM libros WHERE autor ILIKE ?";
    private static final String SELECT_BY_GENERO = 
        "SELECT id, titulo, autor, isbn, genero, año_publicacion, disponible FROM libros WHERE genero ILIKE ?";
    private static final String SELECT_DISPONIBLES = 
        "SELECT id, titulo, autor, isbn, genero, año_publicacion, disponible FROM libros WHERE disponible = true ORDER BY titulo";
    private static final String UPDATE_DISPONIBILIDAD = 
        "UPDATE libros SET disponible = ? WHERE id = ?";
    
    @Override
    public Libro save(Connection connection, Libro libro) throws SQLException {
        logger.debug("Guardando libro: {}", libro.getTitulo());
        
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_LIBRO, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getIsbn());
            stmt.setString(4, libro.getGenero());
            stmt.setObject(5, libro.getAñoPublicacion());
            stmt.setBoolean(6, libro.getDisponible());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear libro, no se afectaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    libro.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Error al crear libro, no se obtuvo ID");
                }
            }
            
            logger.debug("Libro guardado exitosamente con ID: {}", libro.getId());
            return libro;
        }
    }
    
    @Override
    public Optional<Libro> findById(Connection connection, Long id) throws SQLException {
        logger.debug("Buscando libro por ID: {}", id);
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Libro libro = mapResultSetToLibro(rs);
                    logger.debug("Libro encontrado: {}", libro.getTitulo());
                    return Optional.of(libro);
                }
            }
        }
        
        logger.debug("Libro no encontrado con ID: {}", id);
        return Optional.empty();
    }
    
    @Override
    public List<Libro> findAll(Connection connection) throws SQLException {
        logger.debug("Obteniendo todos los libros");
        
        List<Libro> libros = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                libros.add(mapResultSetToLibro(rs));
            }
        }
        
        logger.debug("Se encontraron {} libros", libros.size());
        return libros;
    }
    
    @Override
    public void update(Connection connection, Libro libro) throws SQLException {
        logger.debug("Actualizando libro ID: {}", libro.getId());
        
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_LIBRO)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getIsbn());
            stmt.setString(4, libro.getGenero());
            stmt.setObject(5, libro.getAñoPublicacion());
            stmt.setBoolean(6, libro.getDisponible());
            stmt.setLong(7, libro.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar libro, no se encontró el ID: " + libro.getId());
            }
            
            logger.debug("Libro actualizado exitosamente");
        }
    }
    
    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        logger.debug("Eliminando libro ID: {}", id);
        
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_BY_ID)) {
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al eliminar libro, no se encontró el ID: " + id);
            }
            
            logger.debug("Libro eliminado exitosamente");
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
    public Optional<Libro> findByIsbn(Connection connection, String isbn) throws SQLException {
        logger.debug("Buscando libro por ISBN: {}", isbn);
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ISBN)) {
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Libro libro = mapResultSetToLibro(rs);
                    logger.debug("Libro encontrado por ISBN");
                    return Optional.of(libro);
                }
            }
        }
        
        logger.debug("Libro no encontrado con ISBN: {}", isbn);
        return Optional.empty();
    }
    
    @Override
    public boolean existsByIsbn(Connection connection, String isbn) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(EXISTS_BY_ISBN)) {
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    @Override
    public List<Libro> findByTitulo(Connection connection, String titulo) throws SQLException {
        logger.debug("Buscando libros por título: {}", titulo);
        
        List<Libro> libros = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_TITULO)) {
            stmt.setString(1, "%" + titulo + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapResultSetToLibro(rs));
                }
            }
        }
        
        logger.debug("Se encontraron {} libros con título '{}'", libros.size(), titulo);
        return libros;
    }
    
    @Override
    public List<Libro> findByAutor(Connection connection, String autor) throws SQLException {
        logger.debug("Buscando libros por autor: {}", autor);
        
        List<Libro> libros = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_AUTOR)) {
            stmt.setString(1, "%" + autor + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapResultSetToLibro(rs));
                }
            }
        }
        
        logger.debug("Se encontraron {} libros del autor '{}'", libros.size(), autor);
        return libros;
    }
    
    @Override
    public List<Libro> findByGenero(Connection connection, String genero) throws SQLException {
        logger.debug("Buscando libros por género: {}", genero);
        
        List<Libro> libros = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_GENERO)) {
            stmt.setString(1, "%" + genero + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapResultSetToLibro(rs));
                }
            }
        }
        
        logger.debug("Se encontraron {} libros del género '{}'", libros.size(), genero);
        return libros;
    }
    
    @Override
    public List<Libro> findDisponibles(Connection connection) throws SQLException {
        logger.debug("Obteniendo libros disponibles");
        
        List<Libro> libros = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_DISPONIBLES);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                libros.add(mapResultSetToLibro(rs));
            }
        }
        
        logger.debug("Se encontraron {} libros disponibles", libros.size());
        return libros;
    }
    
    @Override
    public void updateDisponibilidad(Connection connection, Long id, boolean disponible) throws SQLException {
        logger.debug("Actualizando disponibilidad del libro ID: {} a {}", id, disponible);
        
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_DISPONIBILIDAD)) {
            stmt.setBoolean(1, disponible);
            stmt.setLong(2, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar disponibilidad, no se encontró el ID: " + id);
            }
            
            logger.debug("Disponibilidad actualizada exitosamente");
        }
    }
    
    private Libro mapResultSetToLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getLong("id"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setGenero(rs.getString("genero"));
        libro.setAñoPublicacion((Integer) rs.getObject("año_publicacion"));
        libro.setDisponible(rs.getBoolean("disponible"));
        return libro;
    }
}