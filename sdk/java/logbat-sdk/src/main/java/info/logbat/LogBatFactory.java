package info.logbat;

import info.logbat.application.LogBat;
import info.logbat.application.LogSendRequestFactory;
import info.logbat.config.LogBatConfigLoader;
import info.logbat.domain.options.LogBatOptions;
import info.logbat.exception.InvalidOptionException;
import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.LogBuffer;
import info.logbat.infrastructure.LogSender;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * LogBatFactory is a factory class that creates and manages a single instance of the LogBat class.
 * <p>
 * This class implements the singleton pattern to ensure that only one LogBat instance is created
 * and shared across the application. It uses VarHandle for thread-safe lazy initialization.
 * </p>
 * <p>
 * The LogBat instance is created with its required dependencies such as {@link LogBuffer},
 * {@link LogSender}, {@link LogSendRequestFactory}, and {@link AsyncLogWriter}.
 * </p>
 * <p>
 * The Logbat instance is initialized with default configuration options. Custom configuration can
 * be set using the {@link LogBatOptions} class.
 * </p>
 *
 * @author KyungMin Lee <a href="https://github.com/tidavid1">GitHub</a>, JungJae Park <a
 * href="https://github.com/LuizyHub">GitHub</a>
 * @version 0.1.2
 * @since 0.1.0
 */
public final class LogBatFactory {

    private static volatile LogBat instance;

    /**
     * Returns the singleton instance of LogBat.
     * <p>
     * This method provides thread-safe lazy initialization of the LogBat instance. If the instance
     * hasn't been created yet, it calls {@link #getDelayedInstance()} to create and initialize it.
     * </p>
     *
     * @return the singleton LogBat instance
     */
    public static LogBat getInstance() {
        LogBat logbat = instance;
        if (logbat != null) {
            return logbat;
        }
        return getDelayedInstance();
    }

    /**
     * Creates and initializes the LogBat instance if it hasn't been created yet.
     * <p>
     * This method is synchronized to ensure thread safety during the initialization process. It
     * double-checks if the instance has already been created to prevent unnecessary synchronization
     * after the first initialization.
     * </p>
     *
     * @return the initialized LogBat instance
     * @throws RuntimeException if the LogBat instance creation fails
     */
    private static LogBat getDelayedInstance() {
        try {
            LogBat logbat = createLogbat();
            if (!INSTANCE.compareAndSet(null, logbat)) {
                logbat = instance;
            }
            return logbat;
        } catch (InvalidOptionException e) {
            System.err.println("Failed to create LogBat instance: " + e.getMessage());
            System.exit(100);
        }
        // Should never reach this point
        return null;
    }

    /**
     * Creates a new LogBat instance with all necessary dependencies.
     * <p>
     * This method initializes and configures all required components for the LogBat instance,
     * including LogBatOptions, LogSendRequestFactory, and AsyncLogWriter.
     * </p>
     *
     * @return a new LogBat instance
     * @throws InvalidOptionException if there's an issue with the LogBatOptions configuration
     */
    private static LogBat createLogbat() throws InvalidOptionException {
        LogBatOptions logbatOptions = new LogBatOptions(LogBatConfigLoader.loadConfig());
        LogSendRequestFactory logSendRequestFactory = new LogSendRequestFactory();
        AsyncLogWriter asyncLogWriter = new AsyncLogWriter(new LogBuffer(),
            new LogSender(logbatOptions));
        return new LogBat(asyncLogWriter, logSendRequestFactory);
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated
     */
    private LogBatFactory() {
        throw new UnsupportedOperationException("LogBatFactory cannot be instantiated");
    }

    /**
     * VarHandle for the LogBat instance, used for thread-safe lazy initialization.
     */
    private static final VarHandle INSTANCE;

    static {
        try {
            INSTANCE = MethodHandles.lookup()
                .findStaticVarHandle(LogBatFactory.class, "instance", LogBat.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}