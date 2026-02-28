package com.prasad_v.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import com.prasad_v.config.ConfigurationManager;

/**
 * Manages the logging infrastructure for the framework.
 * Provides centralized access to loggers and handles configuration.
 */
public class LogManager {
    private static Map<Class<?>, CustomLogger> loggers = new HashMap<>();
    private static final String LOG_FOLDER = "logs";
    private static boolean initialized = false;
    private static String executionId;

    /**
     * Initialize the logging system
     */
    public static synchronized void init() {
        if (!initialized) {
            createLogDirectory();
            setupExecutionId();
            configureLogging();
            initialized = true;
        }
    }

    /**
     * Get a logger for the specified class
     *
     * @param clazz The class for which to get a logger
     * @return CustomLogger instance for the class
     */
    public static synchronized CustomLogger getLogger(Class<?> clazz) {
        if (!initialized) {
            init();
        }

        if (!loggers.containsKey(clazz)) {
            loggers.put(clazz, new CustomLogger(clazz));
        }

        return loggers.get(clazz);
    }

    /**
     * Create the log directory if it doesn't exist
     */
    private static void createLogDirectory() {
        Path logDir = Paths.get(LOG_FOLDER);
        try {
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            // Create execution-specific directory
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path executionDir = Paths.get(LOG_FOLDER, "execution_" + timestamp);
            if (!Files.exists(executionDir)) {
                Files.createDirectories(executionDir);
            }

            System.setProperty("log.dir", executionDir.toString());
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
        }
    }

    /**
     * Generate a unique execution ID for this test run
     */
    private static void setupExecutionId() {
        executionId = "exec_" + System.currentTimeMillis();
        System.setProperty("execution.id", executionId);
    }

    /**
     * Configure Log4j2 with the appropriate settings
     */
    private static void configureLogging() {
        try {
            String configFile = ConfigurationManager.getInstance().getProperty("log4j2.config.file", "log4j2.xml");
            File log4jFile = new File(configFile);

            if (log4jFile.exists()) {
                LoggerContext context = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
                context.setConfigLocation(log4jFile.toURI());
            } else {
                // Use default configuration
                Configurator.initialize(null, "log4j2.xml");
            }
        } catch (Exception e) {
            System.err.println("Failed to configure logging: " + e.getMessage());
            // Fall back to default configuration
            Configurator.initialize(null, "log4j2.xml");
        }
    }

    /**
     * Get the current execution ID
     *
     * @return The execution ID string
     */
    public static String getExecutionId() {
        if (!initialized) {
            init();
        }
        return executionId;
    }

    /**
     * Set the log level dynamically
     *
     * @param loggerName The logger name
     * @param level The log level
     */
    public static void setLogLevel(String loggerName, String level) {
        org.apache.logging.log4j.core.config.Configurator.setLevel(loggerName, org.apache.logging.log4j.Level.getLevel(level));
    }

    /**
     * Clean up old log files based on retention policy
     *
     * @param daysToKeep Number of days to keep log files
     */
    public static void cleanupOldLogs(int daysToKeep) {
        try {
            Path logDir = Paths.get(LOG_FOLDER);
            if (!Files.exists(logDir)) {
                return;
            }

            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L);

            Files.list(logDir)
                    .filter(path -> Files.isDirectory(path) && path.getFileName().toString().startsWith("execution_"))
                    .forEach(dir -> {
                        try {
                            if (Files.getLastModifiedTime(dir).toMillis() < cutoffTime) {
                                deleteDirectory(dir);
                            }
                        } catch (IOException e) {
                            System.err.println("Failed to check directory age: " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Failed to cleanup old logs: " + e.getMessage());
        }
    }

    /**
     * Recursively delete a directory
     *
     * @param directory The directory to delete
     * @throws IOException If deletion fails
     */
    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Reverse order to delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path + " - " + e.getMessage());
                        }
                    });
        }
    }
}
