package com.example.crudapp.infrastructure.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Configuración de la base de datos con pool de conexiones HikariCP
 * Da una conexion 
 * Implementa patrón Singleton para gestión centralizada de conexiones
 * 
 */
public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static Database instance;
    private final HikariDataSource dataSource;
    
    private Database() throws SQLException {
        try {
            Properties props = loadDatabaseProperties();
            this.dataSource = createDataSource(props);
            logger.info("Pool de conexiones inicializado correctamente");
        } catch (IOException e) {
            logger.error("Error al cargar propiedades de base de datos", e);
            throw new SQLException("No se pudo inicializar la configuración de base de datos", e);
        }
    }
    
    public static synchronized Database getInstance() throws SQLException {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    private Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IOException("Archivo db.properties no encontrado en resources");
            }
            props.load(input);
            logger.debug("Propiedades de base de datos cargadas exitosamente");
        }
        return props;
    }
    
    private HikariDataSource createDataSource(Properties props) {
        HikariConfig config = new HikariConfig();
        
        // Configuración básica
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password"));
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuración del pool para transacciones ACID
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000); // 30 segundos
        config.setIdleTimeout(600000); // 10 minutos
        config.setMaxLifetime(1800000); // 30 minutos
        
        // Configuración para transacciones
        config.setAutoCommit(false); // Crucial para manejo manual de transacciones
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        
        // Configuración de validación
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        config.setLeakDetectionThreshold(60000);
        
        return new HikariDataSource(config);
    }
    
    public Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false); // Asegurar transacciones manuales
        return connection;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Pool de conexiones cerrado");
        }
    }
    
    // Método para verificar conectividad
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            logger.error("Error al probar conexión", e);
            return false;
        }
    }
}