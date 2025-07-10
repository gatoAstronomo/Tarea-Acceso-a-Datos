package com.example.crudapp.domain.repositories;

import com.example.crudapp.domain.entities.Prestamo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio específico para Prestamo
 */
public interface PrestamoRepository extends Repository<Prestamo, Long> {
    List<Prestamo> findByUsuarioId(Connection connection, Long usuarioId) throws SQLException;
    List<Prestamo> findByLibroId(Connection connection, Long libroId) throws SQLException;
    List<Prestamo> findByEstado(Connection connection, String estado) throws SQLException;
    List<Prestamo> findPrestamosVencidos(Connection connection) throws SQLException;
    List<Prestamo> findPrestamosActivos(Connection connection) throws SQLException;
    List<Prestamo> findPrestamosActivosByUsuarioId(Connection connection, Long usuarioId) throws SQLException;
    Optional<Prestamo> findPrestamoActivoByLibroId(Connection connection, Long libroId) throws SQLException;
    List<Prestamo> findPrestamosActivosByLibroId(Connection connection, Long libroId) throws SQLException;
    List<Prestamo> findPrestamosConDetalles(Connection connection) throws SQLException;
    void devolver(Connection connection, Long id) throws SQLException;
}