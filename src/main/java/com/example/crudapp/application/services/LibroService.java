package com.example.crudapp.application.services;

import com.example.crudapp.domain.entities.Libro;
import com.example.crudapp.domain.repositories.LibroRepository;
import com.example.crudapp.application.dto.LibroDTO;
import com.example.crudapp.infrastructure.transactions.TransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para gestión de libros
 * Coordina las operaciones de negocio relacionadas con libros
 * 
 */
public class LibroService {
    
    private static final Logger logger = LoggerFactory.getLogger(LibroService.class);
    
    private final LibroRepository libroRepository;
    private final TransactionManager transactionManager;
    
    /**
     * Constructor del servicio de libros
     * 
     * @param libroRepository repositorio de libros
     * @param transactionManager gestor de transacciones
     * @throws IllegalArgumentException si algún parámetro es null
     */
    public LibroService(LibroRepository libroRepository, TransactionManager transactionManager) {
        if (libroRepository == null) {
            throw new IllegalArgumentException("El repositorio de libros no puede ser null");
        }
        if (transactionManager == null) {
            throw new IllegalArgumentException("El gestor de transacciones no puede ser null");
        }
        
        this.libroRepository = libroRepository;
        this.transactionManager = transactionManager;
        
        logger.info("LibroService inicializado correctamente");
    }
    
    /**
     * Crea un nuevo libro
     * 
     * @param libroDTO datos del libro a crear
     * @return el libro creado
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el DTO es null o inválido
     */
    public Libro crearLibro(LibroDTO libroDTO) throws SQLException {
        if (libroDTO == null) {
            throw new IllegalArgumentException("Los datos del libro no pueden ser null");
        }
        
        logger.debug("Iniciando creación de libro: {}", libroDTO);
        
        validarDatosLibro(libroDTO);
        
        return transactionManager.executeInTransaction(connection -> {
            // Verificar que no exista un libro con el mismo ISBN
            if (libroRepository.existsByIsbn(connection, libroDTO.getIsbn())) {
                String mensaje = String.format("Ya existe un libro con el ISBN: %s", libroDTO.getIsbn());
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            Libro libro = new Libro();
            libro.setTitulo(libroDTO.getTitulo());
            libro.setAutor(libroDTO.getAutor());
            libro.setIsbn(libroDTO.getIsbn());
            libro.setGenero(libroDTO.getGenero());
            libro.setAñoPublicacion(libroDTO.getAñoPublicacion());
            libro.setDisponible(libroDTO.getDisponible() != null ? libroDTO.getDisponible() : true);
            
            Libro libroCreado = libroRepository.save(connection, libro);
            logger.info("Libro creado exitosamente con ID: {}", libroCreado.getId());
            
            return libroCreado;
        });
    }
    
    /**
     * Busca un libro por su ID
     * 
     * @param id identificador del libro
     * @return el libro encontrado o Optional.empty()
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es null
     */
    public Optional<Libro> buscarPorId(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID del libro no puede ser null");
        }
        
        logger.debug("Buscando libro con ID: {}", id);
        
        Optional<Libro> libro = transactionManager.executeInTransaction(connection -> 
            libroRepository.findById(connection, id)
        );
        
        if (libro.isPresent()) {
            logger.debug("Libro encontrado: {}", libro.get().getTitulo());
        } else {
            logger.debug("No se encontró libro con ID: {}", id);
        }
        
        return libro;
    }
    
    /**
     * Busca un libro por su ISBN
     * 
     * @param isbn ISBN del libro
     * @return el libro encontrado o Optional.empty()
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ISBN es null o vacío
     */
    public Optional<Libro> buscarPorIsbn(String isbn) throws SQLException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("El ISBN no puede ser null o vacío");
        }
        
        logger.debug("Buscando libro con ISBN: {}", isbn);
        
        return transactionManager.executeInTransaction(connection -> 
            libroRepository.findByIsbn(connection, isbn)
        );
    }
    
    /**
     * Busca libros por título (búsqueda parcial)
     * 
     * @param titulo título o parte del título a buscar
     * @return lista de libros que coinciden con el título
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el título es null o vacío
     */
    public List<Libro> buscarPorTitulo(String titulo) throws SQLException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede ser null o vacío");
        }
        
        logger.debug("Buscando libros por título: {}", titulo);
        
        List<Libro> libros = transactionManager.executeInTransaction(connection -> 
            libroRepository.findByTitulo(connection, titulo)
        );
        
        logger.info("Se encontraron {} libros con título '{}'", libros.size(), titulo);
        return libros;
    }
    
    /**
     * Busca libros por autor (búsqueda parcial)
     * 
     * @param autor autor o parte del autor a buscar
     * @return lista de libros del autor
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el autor es null o vacío
     */
    public List<Libro> buscarPorAutor(String autor) throws SQLException {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor no puede ser null o vacío");
        }
        
        logger.debug("Buscando libros por autor: {}", autor);
        
        List<Libro> libros = transactionManager.executeInTransaction(connection -> 
            libroRepository.findByAutor(connection, autor)
        );
        
        logger.info("Se encontraron {} libros del autor '{}'", libros.size(), autor);
        return libros;
    }
    
    /**
     * Busca libros por género
     * 
     * @param genero género a buscar
     * @return lista de libros del género
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el género es null o vacío
     */
    public List<Libro> buscarPorGenero(String genero) throws SQLException {
        if (genero == null || genero.trim().isEmpty()) {
            throw new IllegalArgumentException("El género no puede ser null o vacío");
        }
        
        logger.debug("Buscando libros por género: {}", genero);
        
        List<Libro> libros = transactionManager.executeInTransaction(connection -> 
            libroRepository.findByGenero(connection, genero)
        );
        
        logger.info("Se encontraron {} libros del género '{}'", libros.size(), genero);
        return libros;
    }
    
    /**
     * Obtiene todos los libros
     * 
     * @return lista de todos los libros
     * @throws SQLException si ocurre un error de base de datos
     */
    public List<Libro> obtenerTodos() throws SQLException {
        logger.debug("Obteniendo todos los libros");
        
        List<Libro> libros = transactionManager.executeInTransaction(connection -> 
            libroRepository.findAll(connection)
        );
        
        logger.info("Se encontraron {} libros", libros.size());
        return libros;
    }
    
    /**
     * Obtiene todos los libros disponibles
     * 
     * @return lista de libros disponibles
     * @throws SQLException si ocurre un error de base de datos
     */
    public List<Libro> obtenerDisponibles() throws SQLException {
        logger.debug("Obteniendo libros disponibles");
        
        List<Libro> libros = transactionManager.executeInTransaction(connection -> 
            libroRepository.findDisponibles(connection)
        );
        
        logger.info("Se encontraron {} libros disponibles", libros.size());
        return libros;
    }
    
    /**
     * Actualiza un libro existente
     * 
     * @param id identificador del libro a actualizar
     * @param libroDTO nuevos datos del libro
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public void actualizarLibro(Long id, LibroDTO libroDTO) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID del libro no puede ser null");
        }
        if (libroDTO == null) {
            throw new IllegalArgumentException("Los datos del libro no pueden ser null");
        }
        
        logger.debug("Actualizando libro con ID: {} - Datos: {}", id, libroDTO);
        
        validarDatosLibro(libroDTO);
        
        transactionManager.executeInTransactionVoid(connection -> {
            Optional<Libro> libroExistente = libroRepository.findById(connection, id);
            if (libroExistente.isEmpty()) {
                String mensaje = String.format("No existe un libro con ID: %d", id);
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            // Verificar que el ISBN no esté siendo usado por otro libro
            Optional<Libro> libroConIsbn = libroRepository.findByIsbn(connection, libroDTO.getIsbn());
            if (libroConIsbn.isPresent() && !libroConIsbn.get().getId().equals(id)) {
                String mensaje = String.format("Ya existe otro libro con el ISBN: %s", libroDTO.getIsbn());
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            Libro libro = libroExistente.get();
            libro.setTitulo(libroDTO.getTitulo());
            libro.setAutor(libroDTO.getAutor());
            libro.setIsbn(libroDTO.getIsbn());
            libro.setGenero(libroDTO.getGenero());
            libro.setAñoPublicacion(libroDTO.getAñoPublicacion());
            libro.setDisponible(libroDTO.getDisponible() != null ? libroDTO.getDisponible() : true);
            
            libroRepository.update(connection, libro);
            logger.info("Libro actualizado exitosamente con ID: {}", id);
        });
    }
    
    /**
     * Elimina un libro por su ID
     * 
     * @param id identificador del libro a eliminar
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es null
     */
    public void eliminarLibro(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID del libro no puede ser null");
        }
        
        logger.debug("Eliminando libro con ID: {}", id);
        
        transactionManager.executeInTransactionVoid(connection -> {
            if (!libroRepository.existsById(connection, id)) {
                String mensaje = String.format("No existe un libro con ID: %d", id);
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            libroRepository.deleteById(connection, id);
            logger.info("Libro eliminado exitosamente con ID: {}", id);
        });
    }
    
    /**
     * Actualiza la disponibilidad de un libro
     * 
     * @param id identificador del libro
     * @param disponible nueva disponibilidad
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es null
     */
    public void actualizarDisponibilidad(Long id, boolean disponible) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID del libro no puede ser null");
        }
        
        logger.debug("Actualizando disponibilidad del libro ID: {} a {}", id, disponible);
        
        transactionManager.executeInTransactionVoid(connection -> {
            if (!libroRepository.existsById(connection, id)) {
                String mensaje = String.format("No existe un libro con ID: %d", id);
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            libroRepository.updateDisponibilidad(connection, id, disponible);
            logger.info("Disponibilidad actualizada exitosamente para libro ID: {}", id);
        });
    }
    
    /**
     * Valida los datos del libro
     * 
     * @param libroDTO datos a validar
     * @throws IllegalArgumentException si los datos son inválidos
     */
    private void validarDatosLibro(LibroDTO libroDTO) {
        if (libroDTO.getTitulo() == null || libroDTO.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del libro no puede estar vacío");
        }
        
        if (libroDTO.getAutor() == null || libroDTO.getAutor().trim().isEmpty()) {
            throw new IllegalArgumentException("El autor del libro no puede estar vacío");
        }
        
        if (libroDTO.getIsbn() == null || libroDTO.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("El ISBN del libro no puede estar vacío");
        }
        
        if (libroDTO.getGenero() == null || libroDTO.getGenero().trim().isEmpty()) {
            throw new IllegalArgumentException("El género del libro no puede estar vacío");
        }
        
        if (libroDTO.getAñoPublicacion() != null && libroDTO.getAñoPublicacion() < 1000) {
            String mensaje = String.format("El año de publicación debe ser mayor a 1000: %d", libroDTO.getAñoPublicacion());
            logger.warn(mensaje);
            throw new IllegalArgumentException(mensaje);
        }
        
        logger.debug("Validación exitosa para libro: {}", libroDTO.getTitulo());
    }
}