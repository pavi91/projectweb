package com.hotelreservation.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Thread-safe Singleton for managing database connections.
 * Uses HikariCP connection pooling for optimal performance.
 *
 * Implements double-checked locking for thread-safe lazy initialization.
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static volatile DatabaseConnection instance;
    private static final Object lock = new Object();
    private HikariDataSource dataSource;
    private Properties config;

    /**
     * Private constructor - prevents direct instantiation
     */
    private DatabaseConnection() {
        loadConfiguration();
        initializeDataSource();
    }

    /**
     * Thread-safe getInstance using double-checked locking pattern
     * @return singleton instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                    logger.info("DatabaseConnection singleton initialized");
                }
            }
        }
        return instance;
    }

    /**
     * Load database configuration from properties file
     */
    private void loadConfiguration() {
        config = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                config.load(input);
                logger.info("Configuration loaded from application.properties");
            } else {
                logger.warn("application.properties not found, using defaults");
                config.setProperty("db.url", "jdbc:mysql://localhost:3306/hotel_reservation");
                config.setProperty("db.username", "root");
                config.setProperty("db.password", "");
                config.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                config.setProperty("db.pool.size.min", "5");
                config.setProperty("db.pool.size.max", "20");
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration", e);
            throw new RuntimeException("Database configuration loading failed", e);
        }
    }

    /**
     * Initialize HikariCP connection pool
     */
    private void initializeDataSource() {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getProperty("db.url"));
            hikariConfig.setUsername(config.getProperty("db.username"));
            hikariConfig.setPassword(config.getProperty("db.password"));
            hikariConfig.setMinimumIdle(Integer.parseInt(config.getProperty("db.pool.size.min", "5")));
            hikariConfig.setMaximumPoolSize(Integer.parseInt(config.getProperty("db.pool.size.max", "20")));
            hikariConfig.setConnectionTimeout(30000);
            hikariConfig.setIdleTimeout(600000);
            hikariConfig.setMaxLifetime(1800000);
            hikariConfig.setAutoCommit(true);

            this.dataSource = new HikariDataSource(hikariConfig);
            logger.info("HikariCP connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool", e);
            throw new RuntimeException("Connection pool initialization failed", e);
        }
    }

    /**
     * Get a database connection from the pool
     * @return database connection
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (lock) {
                if (dataSource == null || dataSource.isClosed()) {
                    initializeDataSource();
                }
            }
        }
        return dataSource.getConnection();
    }

    /**
     * Close the connection pool and release resources
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }

    /**
     * Check if connection pool is active
     * @return true if pool is active, false otherwise
     */
    public boolean isActive() {
        return dataSource != null && !dataSource.isClosed();
    }

    /**
     * Get connection pool statistics (useful for monitoring)
     * @return statistics string
     */
    public String getPoolStats() {
        if (dataSource != null) {
            return String.format("Active: %d, Idle: %d, Total: %d",
                    dataSource.getHikariPoolMXBean().getActiveConnections(),
                    dataSource.getHikariPoolMXBean().getIdleConnections(),
                    dataSource.getHikariPoolMXBean().getTotalConnections());
        }
        return "Connection pool not initialized";
    }
}

