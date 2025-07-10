package com.example.crudapp.infrastructure.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Inicializador de base de datos
 * Ejecuta scripts SQL de forma idempotente (puede ejecutarse múltiples veces)
 * 
 */
public class DatabaseInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private final Database database;
    
    /**
     * Constructor del inicializador
     * 
     * @param database configuración de base de datos
     */
    public DatabaseInitializer(Database database) {
        this.database = database;
    }
    
    /**
     * Inicializa la base de datos de forma idempotente
     * 
     * @throws SQLException si hay error en la inicialización
     */
    public void initializeDatabase() throws SQLException {
        logger.info("Inicializando base de datos...");
        
        if (!database.testConnection()) {
            throw new SQLException("No se puede conectar a la base de datos");
        }
        
        // 1. Ejecutar funciones PL/pgSQL primero
        executeFunctionsScript();
        
        // 2. Luego ejecutar schema (tablas, triggers, índices)
        executeSchemaScript();
        
        // 3. Finalmente insertar datos iniciales si es necesario
        insertInitialDataIfNeeded();
        
        logger.info("Base de datos inicializada correctamente");
    }
    
    /**
     * Ejecuta el script de funciones PL/pgSQL
     * 
     * @throws SQLException si hay error ejecutando el script
     */
    private void executeFunctionsScript() throws SQLException {
        logger.debug("Ejecutando schema-functions.sql...");
        
        try (Connection connection = database.getConnection()) {
            String functionsScript = loadResourceAsString("schema-functions.sql");
            
            // Ejecutar script completo (funciones PL/pgSQL pueden tener $$ delimiters)
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(functionsScript);
            }
            
            connection.commit();
            logger.debug("Funciones PL/pgSQL ejecutadas correctamente");
            
        } catch (IOException e) {
            logger.warn("No se encontró schema-functions.sql, omitiendo funciones");
        }
    }
    
    /**
     * Ejecuta el script schema.sql (tablas, triggers, índices)
     * 
     * @throws SQLException si hay error ejecutando el script
     */
    private void executeSchemaScript() throws SQLException {
        logger.debug("Ejecutando schema.sql...");
        
        try (Connection connection = database.getConnection()) {
            String schemaScript = loadResourceAsString("schema.sql");
            
            // Dividir el script en statements individuales por ;
            String[] statements = schemaScript.split(";");
            
            for (String statement : statements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty()) {
                    logger.trace("Ejecutando: {}", trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())) + "...");
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(trimmedStatement);
                    }
                }
            }
            
            connection.commit();
            logger.debug("Schema ejecutado correctamente");
            
        } catch (IOException e) {
            throw new SQLException("Error al cargar schema.sql", e);
        }
    }
    
    /**
     * Inserta datos iniciales solo si las tablas están vacías
     * 
     * @throws SQLException si hay error insertando datos
     */
    private void insertInitialDataIfNeeded() throws SQLException {
        logger.debug("Verificando si se necesitan datos iniciales...");
        
        try (Connection connection = database.getConnection()) {
            
            // Verificar si ya hay datos
            if (hasInitialData(connection)) {
                logger.info("Ya existen datos iniciales, omitiendo inserción");
                return;
            }
            
            logger.debug("Insertando datos iniciales...");
            executeInitialDataScript(connection);
            connection.commit();
            
        } catch (IOException e) {
            logger.warn("No se encontró initial-data.sql, omitiendo datos iniciales");
        }
    }
    
    /**
     * Verifica si ya existen datos iniciales
     * 
     * @param connection conexión a la base de datos
     * @return true si ya hay datos, false si está vacía
     * @throws SQLException si hay error en la consulta
     */
    private boolean hasInitialData(Connection connection) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM usuarios";
        
        try (PreparedStatement stmt = connection.prepareStatement(checkQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.debug("Usuarios existentes: {}", count);
                return count > 0;
            }
            
            return false;
        }
    }
    
    /**
     * Ejecuta el script de datos iniciales
     * 
     * @param connection conexión a la base de datos
     * @throws SQLException si hay error ejecutando el script
     * @throws IOException si no se puede leer el archivo
     */
    private void executeInitialDataScript(Connection connection) throws SQLException, IOException {
        String dataScript = loadResourceAsString("initial-data.sql");
        
        // Dividir el script en statements individuales por ;
        String[] statements = dataScript.split(";");
        
        for (String statement : statements) {
            String trimmedStatement = statement.trim();
            if (!trimmedStatement.isEmpty()) {
                logger.trace("Ejecutando data: {}", trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())) + "...");
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(trimmedStatement);
                }
            }
        }
        
        logger.debug("Datos iniciales insertados correctamente");
    }
    
    /**
     * Carga un archivo de recursos como string
     * 
     * @param resourceName nombre del recurso
     * @return contenido del archivo
     * @throws IOException si no se puede leer el archivo
     */
    private String loadResourceAsString(String resourceName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException(String.format("Recurso no encontrado: %s", resourceName));
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
    
    /**
     * Verifica si la base de datos está inicializada
     * 
     * @return true si está inicializada, false en caso contrario
     */
    public boolean isDatabaseInitialized() {
        try (Connection connection = database.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery("SELECT 1 FROM usuarios LIMIT 1");
                return true;
            }
        } catch (SQLException e) {
            logger.debug("Base de datos no inicializada: {}", e.getMessage());
            return false;
        }
    }
}