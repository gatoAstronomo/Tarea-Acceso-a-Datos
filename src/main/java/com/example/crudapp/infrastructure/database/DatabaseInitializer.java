package com.example.crudapp.infrastructure.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Inicializador de base de datos
 * Ejecuta scripts SQL y verifica la estructura de la base de datos
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
     * Inicializa la base de datos ejecutando el schema.sql
     * 
     * @throws SQLException si hay error en la inicialización
     */
    public void initializeDatabase() throws SQLException {
        logger.info("Inicializando base de datos...");
        
        if (!database.testConnection()) {
            throw new SQLException("No se puede conectar a la base de datos");
        }
        
        executeSchemaScript();
        insertInitialData();
        
        logger.info("Base de datos inicializada correctamente");
    }
    
    /**
     * Ejecuta el script schema.sql
     * 
     * @throws SQLException si hay error ejecutando el script
     */
    private void executeSchemaScript() throws SQLException {
        logger.debug("Ejecutando schema.sql...");
        
        try (Connection connection = database.getConnection()) {
            String schemaScript = loadResourceAsString("schema.sql");
            
            try (Statement statement = connection.createStatement()) {
                // Ejecutar script completo
                statement.execute(schemaScript);
                connection.commit();
                logger.debug("Schema ejecutado correctamente");
            }
        } catch (IOException e) {
            throw new SQLException("Error al cargar schema.sql", e);
        }
    }
    
    /**
     * Inserta datos iniciales si las tablas están vacías
     * 
     * @throws SQLException si hay error insertando datos
     */
    private void insertInitialData() throws SQLException {
        logger.debug("Verificando datos iniciales...");
        
        try (Connection connection = database.getConnection()) {
            // Verificar si hay datos
            try (Statement statement = connection.createStatement()) {
                var rs = statement.executeQuery("SELECT COUNT(*) FROM usuarios");
                if (rs.next() && rs.getInt(1) == 0) {
                    logger.debug("Insertando datos iniciales...");
                    executeInitialDataScript();
                }
            }
        }
    }
    
    /**
     * Ejecuta script de datos iniciales
     * 
     * @throws SQLException si hay error ejecutando el script
     */
    private void executeInitialDataScript() throws SQLException {
        try (Connection connection = database.getConnection()) {
            String dataScript = loadResourceAsString("initial-data.sql");
            
            try (Statement statement = connection.createStatement()) {
                statement.execute(dataScript);
                connection.commit();
                logger.debug("Datos iniciales insertados correctamente");
            }
        } catch (IOException e) {
            logger.warn("No se encontró initial-data.sql, omitiendo datos iniciales");
        }
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