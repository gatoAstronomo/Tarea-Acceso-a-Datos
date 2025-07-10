package com.example.crudapp.infrastructure.repositories;

import com.example.crudapp.domain.entities.Libro;
import com.example.crudapp.domain.entities.Prestamo;
import com.example.crudapp.domain.entities.Usuario;
import com.example.crudapp.domain.repositories.PrestamoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Préstamo
 * Maneja todas las operaciones CRUD para la entidad Prestamo
 * 
 */
public class PrestamoRepositoryImpl implements PrestamoRepository {
    private static final Logger logger = LoggerFactory.getLogger(PrestamoRepositoryImpl.class);

    // Queries SQL
    private static final String INSERT_SQL = "INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado) "
            +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE id = ?";

    private static final String SELECT_ALL = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos ORDER BY id";

    private static final String UPDATE_SQL = "UPDATE prestamos SET usuario_id = ?, libro_id = ?, fecha_prestamo = ?, " +
            "fecha_devolucion_esperada = ?, fecha_devolucion_real = ?, estado = ? WHERE id = ?";

    private static final String DELETE_BY_ID = "DELETE FROM prestamos WHERE id = ?";

    private static final String SELECT_BY_USUARIO_ID = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE usuario_id = ?";

    private static final String SELECT_BY_LIBRO_ID = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE libro_id = ?";

    private static final String SELECT_BY_ESTADO = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE estado = ?";

    private static final String SELECT_VENCIDOS = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE estado = 'ACTIVO' AND fecha_devolucion_esperada < CURRENT_DATE";

    private static final String SELECT_ACTIVOS = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE estado = 'ACTIVO'";

    private static final String SELECT_ACTIVOS_BY_USUARIO_ID = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE usuario_id = ? AND estado = 'ACTIVO'";

    private static final String SELECT_ACTIVO_BY_LIBRO_ID = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE libro_id = ? AND estado = 'ACTIVO'";
    
    private static final String SELECT_ACTIVOS_BY_LIBRO_ID = "SELECT id, usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, fecha_devolucion_real, estado "
            +
            "FROM prestamos WHERE libro_id = ? AND estado = 'ACTIVO'";


    private static final String SELECT_CON_DETALLES = "SELECT p.id, p.usuario_id, p.libro_id, p.fecha_prestamo, p.fecha_devolucion_esperada, "
            +
            "p.fecha_devolucion_real, p.estado, " +
            "u.nombre AS usuario_nombre, u.email, u.telefono, u.fecha_registro, " +
            "l.titulo, l.autor, l.isbn, l.genero, l.año_publicacion, l.disponible " +
            "FROM prestamos p " +
            "JOIN usuarios u ON p.usuario_id = u.id " +
            "JOIN libros l ON p.libro_id = l.id";

    private static final String DEVOLVER_SQL = "UPDATE prestamos SET estado = 'devuelto', fecha_devolucion_real = CURRENT_DATE WHERE id = ?";

    // Agregar constante para existsById
    private static final String EXISTS_BY_ID = "SELECT 1 FROM prestamos WHERE id = ?";

    @Override
    public Prestamo save(Connection connection, Prestamo prestamo) throws SQLException {
        logger.debug("Guardando préstamo para usuario_id: {} y libro_id: {}", prestamo.getUsuarioId(),
                prestamo.getLibroId());

        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, prestamo.getUsuarioId());
            stmt.setLong(2, prestamo.getLibroId());
            stmt.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            stmt.setDate(4, Date.valueOf(prestamo.getFechaDevolucionEsperada()));
            stmt.setDate(5,
                    prestamo.getFechaDevolucionReal() != null ? Date.valueOf(prestamo.getFechaDevolucionReal()) : null);
            stmt.setString(6, prestamo.getEstado());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear préstamo, no se afectaron filas");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prestamo.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Error al crear préstamo, no se obtuvo ID");
                }
            }

            logger.debug("Préstamo guardado exitosamente con ID: {}", prestamo.getId());
            return prestamo;
        }
    }

    @Override
    public Optional<Prestamo> findById(Connection connection, Long id) throws SQLException {
        logger.debug("Buscando préstamo por ID: {}", id);

        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Prestamo prestamo = mapResultSetToPrestamo(rs);
                    logger.debug("Préstamo encontrado: ID {}", id);
                    return Optional.of(prestamo);
                }
            }
        }

        logger.debug("Préstamo no encontrado con ID: {}", id);
        return Optional.empty();
    }

    @Override
    public List<Prestamo> findAll(Connection connection) throws SQLException {
        logger.debug("Obteniendo todos los préstamos");

        List<Prestamo> prestamos = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
        }

        logger.debug("Se encontraron {} préstamos", prestamos.size());
        return prestamos;
    }

    @Override
    public void update(Connection connection, Prestamo prestamo) throws SQLException {
        logger.debug("Actualizando préstamo ID: {}", prestamo.getId());

        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setLong(1, prestamo.getUsuarioId());
            stmt.setLong(2, prestamo.getLibroId());
            stmt.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            stmt.setDate(4, Date.valueOf(prestamo.getFechaDevolucionEsperada()));
            stmt.setDate(5,
                    prestamo.getFechaDevolucionReal() != null ? Date.valueOf(prestamo.getFechaDevolucionReal()) : null);
            stmt.setString(6, prestamo.getEstado());
            stmt.setLong(7, prestamo.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar préstamo, no se encontró el ID: " + prestamo.getId());
            }

            logger.debug("Préstamo actualizado exitosamente");
        }
    }

    @Override
    public void deleteById(Connection connection, Long id) throws SQLException {
        logger.debug("Eliminando préstamo ID: {}", id);

        try (PreparedStatement stmt = connection.prepareStatement(DELETE_BY_ID)) {
            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al eliminar préstamo, no se encontró el ID: " + id);
            }

            logger.debug("Préstamo eliminado exitosamente");
        }
    }

    // Implementar método faltante
    @Override
    public boolean existsById(Connection connection, Long id) throws SQLException {
        logger.debug("Verificando existencia de préstamo ID: {}", id);

        try (PreparedStatement stmt = connection.prepareStatement(EXISTS_BY_ID)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<Prestamo> findByUsuarioId(Connection connection, Long usuarioId) throws SQLException {
        logger.debug("Buscando préstamos por usuario_id: {}", usuarioId);

        return findByParameter(connection, SELECT_BY_USUARIO_ID, usuarioId);
    }

    @Override
    public List<Prestamo> findByLibroId(Connection connection, Long libroId) throws SQLException {
        logger.debug("Buscando préstamos por libro_id: {}", libroId);

        return findByParameter(connection, SELECT_BY_LIBRO_ID, libroId);
    }

    @Override
    public List<Prestamo> findByEstado(Connection connection, String estado) throws SQLException {
        logger.debug("Buscando préstamos por estado: {}", estado);

        return findByParameter(connection, SELECT_BY_ESTADO, estado);
    }

    @Override
    public List<Prestamo> findPrestamosVencidos(Connection connection) throws SQLException {
        logger.debug("Buscando préstamos vencidos");

        return queryWithoutParameters(connection, SELECT_VENCIDOS);
    }

    @Override
    public List<Prestamo> findPrestamosActivos(Connection connection) throws SQLException {
        logger.debug("Buscando préstamos activos");

        return queryWithoutParameters(connection, SELECT_ACTIVOS);
    }

    @Override
    public List<Prestamo> findPrestamosActivosByUsuarioId(Connection connection, Long usuarioId) throws SQLException {
        logger.debug("Buscando préstamos activos por usuario_id: {}", usuarioId);

        return findByParameter(connection, SELECT_ACTIVOS_BY_USUARIO_ID, usuarioId);
    }

    @Override
    public Optional<Prestamo> findPrestamoActivoByLibroId(Connection connection, Long libroId) throws SQLException {
        logger.debug("Buscando préstamo activo por libro_id: {}", libroId);

        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ACTIVO_BY_LIBRO_ID)) {
            stmt.setLong(1, libroId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Prestamo prestamo = mapResultSetToPrestamo(rs);
                    logger.debug("Préstamo activo encontrado para libro_id: {}", libroId);
                    return Optional.of(prestamo);
                }
            }
        }

        logger.debug("No se encontró préstamo activo para libro_id: {}", libroId);
        return Optional.empty();
    }

    /*
     * Para revisar si se prestaron libros a la misma personas dos veces solo con
     * intenciones de testing
     */
    @Override
    public List<Prestamo> findPrestamosActivosByLibroId(Connection connection, Long libroId) throws SQLException {
        logger.debug("Buscando préstamos Activos por libro Id ");
        return findByParameter(connection, SELECT_ACTIVOS_BY_LIBRO_ID, libroId);
    }

    @Override
    public List<Prestamo> findPrestamosConDetalles(Connection connection) throws SQLException {
        logger.debug("Buscando préstamos con detalles");

        List<Prestamo> prestamos = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(SELECT_CON_DETALLES);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamoConDetalles(rs));
            }
        }

        logger.debug("Se encontraron {} préstamos con detalles", prestamos.size());
        return prestamos;
    }

    @Override
    public void devolver(Connection connection, Long id) throws SQLException {
        logger.debug("Registrando devolución de préstamo ID: {}", id);

        try (PreparedStatement stmt = connection.prepareStatement(DEVOLVER_SQL)) {
            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al registrar devolución, no se encontró el préstamo con ID: " + id);
            }

            logger.debug("Devolución registrada exitosamente para préstamo ID: {}", id);
        }
    }

    // Métodos auxiliares
    private Prestamo mapResultSetToPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getLong("id"));
        prestamo.setUsuarioId(rs.getLong("usuario_id"));
        prestamo.setLibroId(rs.getLong("libro_id"));
        prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
        prestamo.setFechaDevolucionEsperada(rs.getDate("fecha_devolucion_esperada").toLocalDate());

        Date fechaDevReal = rs.getDate("fecha_devolucion_real");
        if (fechaDevReal != null) {
            prestamo.setFechaDevolucionReal(fechaDevReal.toLocalDate());
        }

        prestamo.setEstado(rs.getString("estado"));
        return prestamo;
    }

    private Prestamo mapResultSetToPrestamoConDetalles(ResultSet rs) throws SQLException {
        Prestamo prestamo = mapResultSetToPrestamo(rs);

        // Mapear usuario
        Usuario usuario = new Usuario();
        usuario.setId(prestamo.getUsuarioId());
        usuario.setNombre(rs.getString("usuario_nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
        prestamo.setUsuario(usuario);

        // Mapear libro
        Libro libro = new Libro();
        libro.setId(prestamo.getLibroId());
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setGenero(rs.getString("genero"));
        libro.setAñoPublicacion(rs.getInt("año_publicacion"));
        libro.setDisponible(rs.getBoolean("disponible"));
        prestamo.setLibro(libro);

        return prestamo;
    }

    private List<Prestamo> findByParameter(Connection connection, String sql, Object parameter) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (parameter instanceof Long) {
                stmt.setLong(1, (Long) parameter);
            } else {
                stmt.setString(1, (String) parameter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(mapResultSetToPrestamo(rs));
                }
            }
        }

        return prestamos;
    }

    private List<Prestamo> queryWithoutParameters(Connection connection, String sql) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
        }

        return prestamos;
    }
}