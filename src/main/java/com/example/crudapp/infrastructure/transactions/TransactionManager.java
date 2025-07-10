package com.example.crudapp.infrastructure.transactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.crudapp.infrastructure.database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Gestor de transacciones ACID
 * Implementa el patrón Template Method para manejo consistente de transacciones
 * 
 */
public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    private final Database database;
    
    public TransactionManager(Database database) {
        this.database = database;
    }
    
    /**
     * Ejecuta una operación dentro de una transacción
     * Si la operación es exitosa, hace commit automáticamente
     * Si ocurre una excepción, hace rollback automáticamente
     * 
     * @param <T> tipo de retorno de la operación
     * @param operation operación a ejecutar
     * @return el resultado de la operación
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si la operación es null
     */
    public <T> T executeInTransaction(TransactionOperation<T> operation) throws SQLException {
        if (operation == null) {
            throw new IllegalArgumentException("La operación no puede ser null");
        }

        Connection connection = null;  // Va a afuera del try para poder hacer rollback en caso de error
        try {
            connection = database.getConnection(); // La obtiene del pool de conexiones (Hikaru)
            // Esto ya esta preconfigurado en el pool de conexiones
            // connection.setAutoCommit(false);
            // connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            
            logger.debug("Iniciando transacción");

            // Ejecutar la operación dentro de la transacción
            T result = operation.apply(connection);
            connection.commit();
            logger.debug("Transacción confirmada exitosamente");
            
            return result;
            
        } catch (SQLException e) {
            // Rollback en caso de error
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.warn("Transacción revertida debido a error: {} class: {}", e.getMessage(), e.getClass().getName());
                } catch (SQLException rollbackException) {
                    logger.error("Error al revertir transacción", rollbackException);
                    e.addSuppressed(rollbackException);
                }
            }
            throw e;
        } catch (Exception e) {
            // Rollback para excepciones no SQL
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.warn("Transacción revertida debido a error no SQL: {}", e.getMessage());
                } catch (SQLException rollbackException) {
                    logger.error("Error al revertir transacción", rollbackException);
                }
            }
            throw new SQLException("Error en transacción: " + e.getMessage(), e);
        } finally {
            // Cerrar conexión
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error al cerrar conexión", e);
                }
            }
        }
    }
    
    /**
     * Ejecuta una operación que no requiere resultado dentro de una transacción
     */
    public void executeInTransactionVoid(VoidTransactionOperation operation) throws SQLException {
        executeInTransaction(connection -> {
            operation.apply(connection);
            return null;
        });
    }
    
    /**
     * Ejecuta múltiples operaciones en una sola transacción
     * Útil para operaciones complejas que involucran múltiples tablas
     */
    public void executeMultipleInTransaction(List<VoidTransactionOperation> operations) throws SQLException {
        executeInTransaction(connection -> {
            for (VoidTransactionOperation operation : operations) {
                operation.apply(connection);
            }
            return null;
        });
    }

}