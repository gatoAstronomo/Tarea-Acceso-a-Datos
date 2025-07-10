package com.example.crudapp.domain.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio base genérico
 */
public interface Repository<T, ID> {
    T save(Connection connection, T entity) throws SQLException;
    Optional<T> findById(Connection connection, ID id) throws SQLException;
    List<T> findAll(Connection connection) throws SQLException;
    void update(Connection connection, T entity) throws SQLException;
    void deleteById(Connection connection, ID id) throws SQLException;
    boolean existsById(Connection connection, ID id) throws SQLException;
}