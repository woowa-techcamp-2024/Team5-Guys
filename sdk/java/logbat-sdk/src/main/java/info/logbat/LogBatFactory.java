package info.logbat;

import info.logbat.application.LogSendRequestFactory;
import info.logbat.application.Logbat;
import info.logbat.domain.options.LogBatOptions;
import info.logbat.exception.InvalidOptionException;
import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.LogBuffer;
import info.logbat.infrastructure.LogSender;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * LogBatFactory is a factory class that creates and manages a single instance of the Logbat class.
 * <p>
 * This class implements the singleton pattern to ensure that only one Logbat instance is created
 * and shared across the application. It uses VarHandle for thread-safe lazy initialization.
 * </p>
 * <p>
 * The Logbat instance is created with its required dependencies such as {@link LogBuffer},
 * {@link LogSender}, {@link LogSendRequestFactory}, and {@link AsyncLogWriter}.
 * </p>
 * <p>
 * The Logbat instance is initialized with default configuration options. Custom configuration can
 * be set using the {@link LogBatOptions} class.
 * </p>
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public final class LogBatFactory {

    private static Logbat instance;

    /**
     * Returns the singleton instance of Logbat.
     * <p>
     * This method provides thread-safe lazy initialization of the Logbat instance. If the instance
     * hasn't been created yet, it calls {@link #getDelayedInstance()} to create and initialize it.
     * </p>
     *
     * @return the singleton Logbat instance
     */
    public static Logbat getInstance() {
        Logbat logbat = (Logbat) INSTANCES.getAcquire();
        if (logbat != null) {
            return logbat;
        }
        return getDelayedInstance();
    }

    /**
     * Creates and initializes the Logbat instance if it hasn't been created yet.
     * <p>
     * This method is synchronized to ensure thread safety during the initialization process. It
     * double-checks if the instance has already been created to prevent unnecessary synchronization
     * after the first initialization.
     * </p>
     *
     * @return the initialized Logbat instance
     * @throws RuntimeException if the Logbat instance creation fails
     */
    private static synchronized Logbat getDelayedInstance() {
        Logbat logbat = (Logbat) INSTANCES.getAcquire();
        if (logbat != null) {
            return logbat;
        }
        try {
            logbat = createLogbat();
        } catch (InvalidOptionException e) {
            System.err.println("Failed to create Logbat instance: " + e.getMessage());
            System.exit(100);
        }
        INSTANCES.setRelease(logbat);
        return logbat;
    }

    /**
     * Creates a new Logbat instance with all necessary dependencies.
     * <p>
     * This method initializes and configures all required components for the Logbat instance,
     * including LogBatOptions, ObjectMapper, LogBuffer, LogSender, LogSendRequestFactory, and
     * AsyncLogWriter.
     * </p>
     *
     * @return a new Logbat instance
     * @throws InvalidOptionException if there's an issue with the LogBatOptions configuration
     */
    private static Logbat createLogbat() throws InvalidOptionException {
        LogBatOptions logbatOptions = new LogBatOptions(null);
        LogSendRequestFactory logSendRequestFactory = new LogSendRequestFactory();
        AsyncLogWriter asyncLogWriter = new AsyncLogWriter(new LogBuffer(),
            new LogSender(logbatOptions));
        return new Logbat(asyncLogWriter, logSendRequestFactory);
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
     * VarHandle for the Logbat instance, used for thread-safe lazy initialization.
     */
    private static final VarHandle INSTANCES;

    static {
        try {
            INSTANCES = MethodHandles.lookup()
                .findStaticVarHandle(LogBatFactory.class, "instance", Logbat.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}