package com.example.crudapp.application.services;

import com.example.crudapp.domain.entities.Prestamo;
import com.example.crudapp.domain.entities.Usuario;
import com.example.crudapp.domain.entities.Libro;
import com.example.crudapp.domain.repositories.PrestamoRepository;
import com.example.crudapp.domain.repositories.UsuarioRepository;
import com.example.crudapp.domain.repositories.LibroRepository;
import com.example.crudapp.application.dto.PrestamoDTO;
import com.example.crudapp.infrastructure.transactions.TransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para gestión de préstamos
 * Coordina las operaciones de negocio relacionadas con préstamos
 * Maneja la lógica compleja que involucra múltiples entidades
 * 
 */
public class PrestamoService {
    
    private static final Logger logger = LoggerFactory.getLogger(PrestamoService.class);
    
    /**
     * Días por defecto para la devolución de un libro
     */
    private static final int DIAS_PRESTAMO_DEFAULT = 14;
    
    /**
     * Estados válidos para un préstamo
     */
    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_DEVUELTO = "DEVUELTO";
    private static final String ESTADO_VENCIDO = "VENCIDO";
    
    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final TransactionManager transactionManager;
    
    /**
     * Constructor del servicio de préstamos
     * 
     * @param prestamoRepository repositorio de préstamos
     * @param usuarioRepository repositorio de usuarios
     * @param libroRepository repositorio de libros
     * @param transactionManager gestor de transacciones
     * @throws IllegalArgumentException si algún parámetro es null
     */
    public PrestamoService(PrestamoRepository prestamoRepository, 
                          UsuarioRepository usuarioRepository,
                          LibroRepository libroRepository,
                          TransactionManager transactionManager) {
        if (prestamoRepository == null) {
            throw new IllegalArgumentException("El repositorio de préstamos no puede ser null");
        }
        if (usuarioRepository == null) {
            throw new IllegalArgumentException("El repositorio de usuarios no puede ser null");
        }
        if (libroRepository == null) {
            throw new IllegalArgumentException("El repositorio de libros no puede ser null");
        }
        if (transactionManager == null) {
            throw new IllegalArgumentException("El gestor de transacciones no puede ser null");
        }
        
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.transactionManager = transactionManager;
        
        logger.info("PrestamoService inicializado correctamente");
    }
    
    /**
     * Crea un nuevo préstamo
     * Valida que el usuario y libro existan, y que el libro esté disponible
     * 
     * @param prestamoDTO datos del préstamo a crear
     * @return el préstamo creado
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el DTO es null o inválido
     */
    public Prestamo crearPrestamo(PrestamoDTO prestamoDTO) throws SQLException {
        if (prestamoDTO == null) {
            throw new IllegalArgumentException("Los datos del préstamo no pueden ser null");
        }
        
        logger.debug("Iniciando creación de préstamo: {}", prestamoDTO);
        
        validarDatosPrestamo(prestamoDTO);
        
        return transactionManager.executeInTransaction(connection -> {
            // Verificar que el usuario existe
            Optional<Usuario> usuario = usuarioRepository.findById(connection, prestamoDTO.getUsuarioId());
            if (usuario.isEmpty()) {
                String mensaje = String.format("No existe un usuario con ID: %d", prestamoDTO.getUsuarioId());
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            // Verificar que el libro existe y está disponible
            Optional<Libro> libro = libroRepository.findById(connection, prestamoDTO.getLibroId());
            if (libro.isEmpty()) {
                String mensaje = String.format("No existe un libro con ID: %d", prestamoDTO.getLibroId());
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            if (!libro.get().getDisponible()) {
                String mensaje = String.format("El libro '%s' no está disponible para préstamo", libro.get().getTitulo());
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            // Crear el préstamo
            Prestamo prestamo = new Prestamo();
            prestamo.setUsuarioId(prestamoDTO.getUsuarioId());
            prestamo.setLibroId(prestamoDTO.getLibroId());
            prestamo.setFechaPrestamo(prestamoDTO.getFechaPrestamo() != null ? 
                                     prestamoDTO.getFechaPrestamo() : LocalDate.now());
            prestamo.setFechaDevolucionEsperada(prestamoDTO.getFechaDevolucionEsperada() != null ? 
                                               prestamoDTO.getFechaDevolucionEsperada() : 
                                               LocalDate.now().plusDays(DIAS_PRESTAMO_DEFAULT));
            prestamo.setEstado(ESTADO_ACTIVO);
            
            // Guardar préstamo y actualizar disponibilidad del libro
            Prestamo prestamoCreado = prestamoRepository.save(connection, prestamo);
            libroRepository.updateDisponibilidad(connection, prestamoDTO.getLibroId(), false);
            
            logger.info("Préstamo creado exitosamente con ID: {} para usuario: {} y libro: {}", 
                       prestamoCreado.getId(), usuario.get().getNombre(), libro.get().getTitulo());
            
            return prestamoCreado;
        });
    }
    
    /**
     * Busca un préstamo por su ID
     * 
     * @param id identificador del préstamo
     * @return el préstamo encontrado o Optional.empty()
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es null
     */
    public Optional<Prestamo> buscarPorId(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El ID del préstamo no puede ser null");
        }
        
        logger.debug("Buscando préstamo con ID: {}", id);
        
        Optional<Prestamo> prestamo = transactionManager.executeInTransaction(connection -> 
            prestamoRepository.findById(connection, id)
        );
        
        if (prestamo.isPresent()) {
            logger.debug("Préstamo encontrado: {}", prestamo.get());
        } else {
            logger.debug("No se encontró préstamo con ID: {}", id);
        }
        
        return prestamo;
    }
    
    /**
     * Obtiene todos los préstamos
     * 
     * @return lista de todos los préstamos
     * @throws SQLException si ocurre un error de base de datos
     */
    public List<Prestamo> obtenerTodos() throws SQLException {
        logger.debug("Obteniendo todos los préstamos");
        
        List<Prestamo> prestamos = transactionManager.executeInTransaction(connection -> 
            prestamoRepository.findAll(connection)
        );
        
        logger.info("Se encontraron {} préstamos", prestamos.size());
        return prestamos;
    }
    
    /**
     * Obtiene los préstamos activos de un usuario
     * 
     * @param usuarioId ID del usuario
     * @return lista de préstamos activos del usuario
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es null
     */
    public List<Prestamo> obtenerPrestamosActivosUsuario(Long usuarioId) throws SQLException {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser null");
        }
        
        logger.debug("Obteniendo préstamos activos para usuario ID: {}", usuarioId);
        
        return transactionManager.executeInTransaction(connection -> 
            prestamoRepository.findPrestamosActivosByUsuarioId(connection, usuarioId)
        );
    }
    
    /**
     * Obtiene los préstamos vencidos
     * 
     * @return lista de préstamos vencidos
     * @throws SQLException si ocurre un error de base de datos
     */
    public List<Prestamo> obtenerPrestamosVencidos() throws SQLException {
        logger.debug("Obteniendo préstamos vencidos");
        
        return transactionManager.executeInTransaction(connection -> 
            prestamoRepository.findPrestamosVencidos(connection)
        );
    }
    
    /**
     * Devuelve un libro prestado
     * Marca el préstamo como devuelto y actualiza la disponibilidad del libro
     * 
     * @param prestamoId ID del préstamo a devolver
     * @param fechaDevolucion fecha de devolución (opcional, si es null usa la fecha actual)
     * @param observaciones observaciones adicionales
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es null o el préstamo no existe
     */
    public void devolverLibro(Long prestamoId, LocalDate fechaDevolucion, String observaciones) throws SQLException {
        if (prestamoId == null) {
            throw new IllegalArgumentException("El ID del préstamo no puede ser null");
        }
        
        LocalDate fechaDevolucionFinal = fechaDevolucion != null ? fechaDevolucion : LocalDate.now();
        
        logger.debug("Devolviendo libro para préstamo ID: {} en fecha: {}", prestamoId, fechaDevolucionFinal);
        
        transactionManager.executeInTransactionVoid(connection -> {
            Optional<Prestamo> prestamoOpt = prestamoRepository.findById(connection, prestamoId);
            if (prestamoOpt.isEmpty()) {
                String mensaje = String.format("No existe un préstamo con ID: %d", prestamoId);
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            Prestamo prestamo = prestamoOpt.get();
            
            if (ESTADO_DEVUELTO.equals(prestamo.getEstado())) {
                String mensaje = String.format("El préstamo con ID: %d ya fue devuelto", prestamoId);
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            // Actualizar el préstamo
            prestamo.setFechaDevolucionReal(fechaDevolucionFinal);
            prestamo.setEstado(ESTADO_DEVUELTO);

            // Guardar cambios y actualizar disponibilidad del libro
            prestamoRepository.update(connection, prestamo);
            libroRepository.updateDisponibilidad(connection, prestamo.getLibroId(), true);
            
            logger.info("Libro devuelto exitosamente para préstamo ID: {}", prestamoId);
        });
    }
    
    /**
     * Renueva un préstamo existente
     * Extiende la fecha de devolución esperada
     * 
     * @param prestamoId ID del préstamo a renovar
     * @param diasExtension días adicionales para la devolución
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public void renovarPrestamo(Long prestamoId, int diasExtension) throws SQLException {
        if (prestamoId == null) {
            throw new IllegalArgumentException("El ID del préstamo no puede ser null");
        }
        if (diasExtension <= 0) {
            throw new IllegalArgumentException("Los días de extensión deben ser mayores a 0");
        }
        
        logger.debug("Renovando préstamo ID: {} por {} días", prestamoId, diasExtension);
        
        transactionManager.executeInTransactionVoid(connection -> {
            Optional<Prestamo> prestamoOpt = prestamoRepository.findById(connection, prestamoId);
            if (prestamoOpt.isEmpty()) {
                String mensaje = String.format("No existe un préstamo con ID: %d", prestamoId);
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            Prestamo prestamo = prestamoOpt.get();
            
            if (!ESTADO_ACTIVO.equals(prestamo.getEstado())) {
                String mensaje = String.format("Solo se pueden renovar préstamos activos. Estado actual: %s", prestamo.getEstado());
                logger.warn(mensaje);
                throw new IllegalArgumentException(mensaje);
            }
            
            // Extender la fecha de devolución
            LocalDate nuevaFechaDevolucion = prestamo.getFechaDevolucionEsperada().plusDays(diasExtension);
            prestamo.setFechaDevolucionEsperada(nuevaFechaDevolucion);

            prestamoRepository.update(connection, prestamo);
            logger.info("Préstamo renovado exitosamente ID: {} hasta {}", prestamoId, nuevaFechaDevolucion);
        });
    }
    
    /**
     * Actualiza el estado de préstamos vencidos
     * Marca como vencidos todos los préstamos activos que superaron la fecha de devolución
     * 
     * @return número de préstamos marcados como vencidos
     * @throws SQLException si ocurre un error de base de datos
     */
    public int actualizarPrestamosVencidos() throws SQLException {
        logger.debug("Actualizando préstamos vencidos");
        
        return transactionManager.executeInTransaction(connection -> {
            List<Prestamo> prestamosActivos = prestamoRepository.findByEstado(connection, ESTADO_ACTIVO);
            LocalDate fechaActual = LocalDate.now();
            int contador = 0;
            
            for (Prestamo prestamo : prestamosActivos) {
                if (prestamo.getFechaDevolucionEsperada().isBefore(fechaActual)) {
                    prestamo.setEstado(ESTADO_VENCIDO);
                    prestamoRepository.update(connection, prestamo);
                    contador++;
                }
            }
            
            logger.info("Se marcaron {} préstamos como vencidos", contador);
            return contador;
        });
    }
    
    /**
     * Valida los datos del préstamo
     * 
     * @param prestamoDTO datos a validar
     * @throws IllegalArgumentException si los datos son inválidos
     */
    private void validarDatosPrestamo(PrestamoDTO prestamoDTO) {
        if (prestamoDTO.getUsuarioId() == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser null");
        }
        
        if (prestamoDTO.getLibroId() == null) {
            throw new IllegalArgumentException("El ID del libro no puede ser null");
        }
        
        if (prestamoDTO.getFechaDevolucionEsperada() != null && 
            prestamoDTO.getFechaDevolucionEsperada().isBefore(LocalDate.now())) {
            String mensaje = String.format("La fecha de devolución esperada no puede ser anterior a hoy: %s", 
                                          prestamoDTO.getFechaDevolucionEsperada());
            logger.warn(mensaje);
            throw new IllegalArgumentException(mensaje);
        }
        
        logger.debug("Validación exitosa para préstamo usuario ID: {}, libro ID: {}", 
                    prestamoDTO.getUsuarioId(), prestamoDTO.getLibroId());
    }
}
