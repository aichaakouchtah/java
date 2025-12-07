package com.infinitpages.util.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class for managing database connections using HikariCP connection pool.
 * 
 * This class provides a centralized way to:
 * - Initialize the connection pool at application startup
 * - Get connections from the pool (reused efficiently)
 * - Close the pool at application shutdown
 * 
 * Benefits of HikariCP:
 * - Fast: Reuses connections instead of creating new ones (100-300x faster)
 * - Efficient: Manages connection lifecycle automatically
 * - Scalable: Handles multiple concurrent requests
 * - Production-ready: Used by major frameworks
 */
public class DatabaseConnection {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static HikariDataSource dataSource;
    private static boolean initialized = false;
    
    /**
     * Initialize the HikariCP connection pool.
     * Call this method once at application startup (e.g., in Main.java).
     * 
     * @param jdbcUrl Database connection URL (e.g., "jdbc:mysql://localhost:3306/infinitpages")
     * @param username Database username
     * @param password Database password
     */
    public static void initialize(String jdbcUrl, String username, String password) {
        if (initialized) {
            logger.warn("Database connection pool already initialized!");
            return;
        }
        
        try {
            HikariConfig config = new HikariConfig();
            
            // Database connection settings
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            // Connection pool settings
            config.setMaximumPoolSize(10);        // Maximum 10 connections in pool
            config.setMinimumIdle(5);             // Keep 5 connections ready
            config.setConnectionTimeout(30000);   // 30 seconds to get connection
            config.setIdleTimeout(600000);        // 10 minutes idle timeout
            config.setMaxLifetime(1800000);       // 30 minutes max connection lifetime
            config.setLeakDetectionThreshold(60000); // Detect connection leaks after 60 seconds
            
            // MySQL-specific settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            // Connection pool name (for monitoring)
            config.setPoolName("InfinitPagesPool");
            
            dataSource = new HikariDataSource(config);
            initialized = true;
            
            logger.info("Database connection pool initialized successfully!");
            logger.info("Pool name: {}", config.getPoolName());
            logger.info("Maximum pool size: {}", config.getMaximumPoolSize());
            logger.info("Minimum idle: {}", config.getMinimumIdle());
            
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Initialize with default MySQL settings (localhost:3306).
     * Useful for development.
     * 
     * @param databaseName Name of the database
     * @param username Database username
     * @param password Database password
     */
    public static void initializeDefault(String databaseName, String username, String password) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/" + databaseName + 
                        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        initialize(jdbcUrl, username, password);
    }
    
    /**
     * Initialize the connection pool using settings from database.properties file.
     * Loads configuration from src/main/resources/database.properties.
     * 
     * @throws RuntimeException if properties file cannot be loaded or is missing required properties
     */
    public static void initialize() {
        try {
            java.util.Properties props = new java.util.Properties();
            java.io.InputStream inputStream = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database.properties");
            
            if (inputStream == null) {
                throw new RuntimeException("database.properties file not found in classpath");
            }
            
            props.load(inputStream);
            inputStream.close();
            
            String host = props.getProperty("db.host", "localhost");
            String port = props.getProperty("db.port", "3306");
            String databaseName = props.getProperty("db.name", "infinitpages");
            String username = props.getProperty("db.username", "root");
            String password = props.getProperty("db.password", "");
            
            String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host, port, databaseName
            );
            
            initialize(jdbcUrl, username, password);
            
        } catch (Exception e) {
            logger.error("Failed to load database properties", e);
            throw new RuntimeException("Failed to initialize database connection from properties file", e);
        }
    }
    
    /**
     * Print pool statistics to console.
     */
    public static void printPoolStats() {
        System.out.println("📊 " + getPoolStats());
    }
    
    /**
     * Get a connection from the connection pool.
     * 
     * The connection is reused from the pool (very fast, <1ms).
     * Remember to close it when done - it will return to the pool, not actually close.
     * 
     * @return A database connection from the pool
     * @throws SQLException If connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized || dataSource == null) {
            throw new SQLException("Database connection pool not initialized! " +
                                  "Call DatabaseConnection.initialize() first.");
        }
        
        try {
            Connection conn = dataSource.getConnection();
            logger.debug("Connection obtained from pool");
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to get connection from pool", e);
            throw e;
        }
    }
    
    /**
     * Test the database connection.
     * Useful for verifying configuration at startup.
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5); // Test with 5 second timeout
        } catch (SQLException e) {
            logger.error("Connection test failed", e);
            return false;
        }
    }
    
    /**
     * Get pool statistics (for monitoring).
     * 
     * @return Pool statistics as a formatted string
     */
    public static String getPoolStats() {
        if (dataSource == null) {
            return "Pool not initialized";
        }
        
        return String.format(
            "Pool: %s | Active: %d | Idle: %d | Total: %d | Waiting: %d",
            dataSource.getPoolName(),
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }
    
    /**
     * Close the connection pool.
     * Call this method at application shutdown (e.g., in Main.java).
     */
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            initialized = false;
            logger.info("Database connection pool closed");
        }
    }
    
    /**
     * Check if the connection pool is initialized.
     * 
     * @return true if initialized, false otherwise
     */
    public static boolean isInitialized() {
        return initialized;
    }
}

