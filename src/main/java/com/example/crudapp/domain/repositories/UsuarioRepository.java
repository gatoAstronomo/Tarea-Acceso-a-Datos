package com.example.crudapp.domain.repositories;

import com.example.crudapp.domain.entities.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
/**
 * Repositorio específico para Usuario
 */
public interface UsuarioRepository extends Repository<Usuario, Long> {
    Optional<Usuario> findByEmail(Connection connection, String email) throws SQLException;
    boolean existsByEmail(Connection connection, String email) throws SQLException;
    List<Usuario> findByNombre(Connection connection, String nombre) throws SQLException;
}
