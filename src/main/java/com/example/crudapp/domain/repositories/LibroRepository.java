package com.example.crudapp.domain.repositories;

import com.example.crudapp.domain.entities.Libro;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
/**
 * Repositorio específico para Libro
 */
public interface LibroRepository extends Repository<Libro, Long> {
    Optional<Libro> findByIsbn(Connection connection, String isbn) throws SQLException;
    boolean existsByIsbn(Connection connection, String isbn) throws SQLException;
    List<Libro> findByTitulo(Connection connection, String titulo) throws SQLException;
    List<Libro> findByAutor(Connection connection, String autor) throws SQLException;
    List<Libro> findByGenero(Connection connection, String genero) throws SQLException;
    List<Libro> findDisponibles(Connection connection) throws SQLException;
    void updateDisponibilidad(Connection connection, Long id, boolean disponible) throws SQLException;
}