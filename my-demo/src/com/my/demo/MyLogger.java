package com.my.demo;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;
import org.apache.logging.log4j.util.Supplier;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;


@Untraced
public class MyLogger {
    ////////////////////////////////////////////////////////////////////////
    //
    // Fields
    //
    ////////////////////////////////////////////////////////////////////////

    /**
     * Holds Logger for the calling class.
     */
    private final ExtendedLoggerWrapper logger;

    /**
     * Fully qualified class name [FQCN] to show the correct class, method and line number.
     * Log4j remembers the FQCN of the logger and
     * uses this to walk the stack trace for every log event.
     * Providing the correct FQCN solves the problem with custom logger wrapper [this]
     * since it has a different FQCN than the actual logger [caller].
     */
    private static final String FQCN = MyLogger.class.getName();

    /**
     * The text to use to replace sensitive logging information.
     */
    private static final String SENSITIVE_VALUE_REPLACEMENT = "*****";

    /**
     * Used to create custom log level "DIAG" whose logging level exists between
     * the standard levels DEBUG and TRACE.
     */
    private static final Level DIAG = Level.forName("DIAG", 550);

    ////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    ////////////////////////////////////////////////////////////////////////

    /**
     * Default constructor
     *
     * @param logger
     *            Logger for the calling class.
     *
     */
    private MyLogger(Logger logger) {
        this.logger = new ExtendedLoggerWrapper(
                (AbstractLogger) logger,
                logger.getName(),
                logger.getMessageFactory());
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // Single static interface for class instantiation
    //
    ////////////////////////////////////////////////////////////////////////

    /**
     * Returns a Logger with the name of the calling class.
     *
     * @param clazz
     *            A calling class name.
     * @return The Logger for the calling class.
     */
    public static MyLogger getLogger(Class<?> clazz) {
        return new MyLogger(LogManager.getLogger(clazz));
    }

    /**
     * Returns a Logger with the name of the calling class.
     *
     * @param clazz
     *            A calling class name.
     * @return The Logger for the calling class.
     */
    public static MyLogger getLogger(String clazz) {
        return new MyLogger(LogManager.getLogger(clazz));
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // Convenience methods for logging
    //
    ////////////////////////////////////////////////////////////////////////

    /**
     * Logs a message which is only to be constructed if the logging level is
     * the ERROR level.
     * <p>
     * An error is a serious issue and represents the failure of something
     * important going on in an application. Use the ERROR log level to indicate
     * a problem that has to be dealt with.
     * </P>
     * <p>
     * Examples </br>
     * {@code log.error(() -> "All hell broke loose.");} </br>
     * {@code log.error(() -> "All hell broke loose. " + fooMsg);} </br>
     * {@code log.error(() -> "All hell broke loose. " + e.getMessage());}
     * </p>
     *
     * @param supplier
     *            A lambda function, which when called, produces the desired log
     *            message.
     */
    public void error(Supplier<?> supplier) {
        if (logger.isErrorEnabled())
            logger.logIfEnabled(FQCN, Level.ERROR, null, filteredMsg(supplier), (Throwable) null);
    }

    /**
     * Logs a message (only to be constructed if the logging level is the ERROR
     * level) including the stack trace of the Throwable t passed as parameter.
     * <p>
     * An error is a serious issue and represents the failure of something
     * important going on in an application. Use the ERROR log level to indicate
     * a problem that has to be dealt with.
     * </P>
     * <p>
     * Examples </br>
     * {@code log.error(() -> "All hell broke loose. " + fooMsg, e);} </br>
     * {@code log.error(() -> "All hell broke loose. " + e.getMessage(), e);}
     * </p>
     *
     * @param supplier
     *            A lambda function, which when called, produces the desired log
     *            message.
     * @param t
     *            An exception to log, including its stack trace.
     */
    public void error(Supplier<?> supplier, Throwable t) {
        if (logger.isErrorEnabled())
            logger.logIfEnabled(FQCN, Level.ERROR, null, filteredMsg(supplier), t);
    }

    /**
     * Logs a message which is only to be constructed if the logging level is
     * the WARN level.
     * <p>
     * The WARN level designates potentially harmful occurrences but no real
     * harm done. Use the WARN log level to indicate an unusual and unexpected
     * situation, but no real harm done.
     * </P>
     * <p>
     * Example </br>
     * {@code log.warn(() -> "User record skipped.");} </br>
     * {@code log.warn(() -> "User record skipped. " + fooMsg);} </br>
     * {@code log.warn(() -> "User record skipped. " + e.getMessage());}
     * </p>
     *
     * @param supplier
     *            A lambda function, which when called, produces the desired log
     *            message.
     */
    public void warn(Supplier<?> supplier) {
        if (logger.isWarnEnabled())
            logger.logIfEnabled(FQCN, Level.WARN, null, filteredMsg(supplier), (Throwable) null);
    }

    /**
     * Logs a message (only to be constructed if the logging level is the WARN
     * level) including the stack trace of the Throwable t passed as parameter.
     * <p>
     * The WARN level designates potentially harmful occurrences but no real
     * harm done. Use the WARN log level to indicate an unusual and unexpected
     * situation, but no real harm done.
     * </P>
     * <p>
     * <strong>Caveat:</strong> It is advisable not to log a stack trace when
     * using the WARN log level. It may frighten end-user even though there is
     * no real harm done. Fall back to {@link #warn(Supplier)}
     * </p>
     * <p>
     * Example </br>
     * {@code log.warn(() -> "User record skipped. " + fooMsg, e);} </br>
     * {@code log.warn(() -> "User record skipped. " + e.getMessage(), e);}
     * </p>
     *
     * @param supplier
     *            A lambda function, which when called, produces the desired log
     *            message.
     * @param t
     *            An exception to log, including its stack trace.
     */
    public void warn(Supplier<?> supplier, Throwable t) {
        if (logger.isWarnEnabled())
            logger.logIfEnabled(FQCN, Level.WARN, null, filteredMsg(supplier), t);
    }

    /**
     * Logs a message which is only to be constructed if the logging level is
     * the INFO level.
     * <p>
     * INFO messages correspond to normal application behavior and milestones.
     * We probably won't care too much about these entries during normal
     * operations, but they provide the skeleton of what happened. Use the INFO
     * logging level to record messages about routine application operation. A
     * service started or stopped. You added a new user to the database. That
     * sort of thing.
     * </p>
     * <p>
     * Examples </br>
     * {@code log.info(() -> "Universal manager is on.");} </br>
     * {@code log.info(() -> "Universal manager is on. " + fooMsg);}
     * </p>
     *
     * @param supplier
     *            A lambda function, which when called, produces the desired log
     *            message.
     */
    public void info(Supplier<?> supplier) {
        if (logger.isInfoEnabled())
            logger.logIfEnabled(FQCN, Level.INFO, null, filteredMsg(supplier), (Throwable) null);
    }

    /**
     * Logs a message which is only to be constructed if the logging level is
     * the DEBUG level.
     * <p>
     * It includes information in a granular way so developers and other IT
     * professionals can then use that information to perform diagnostics on the
     * application. Use the DEBUG log level to provide low level information.
     * Furnish more information than you'd want in normal production situations.
     * Application object printing. That sort of thing.
     * </p>
     * <p>
     * Example </br>
     * {@code log.debug("Logging in user " + user.getName() + " with birthday " + user.getBirthdayCalendar());}
     * </p>
     *
     * @param supplier
     *            A lambda function, which when called, produces the desired log
     *            message.
     */
    public void debug(Supplier<?> supplier) {
        if (logger.isDebugEnabled())
            logger.logIfEnabled(FQCN, Level.DEBUG, null, filteredMsg(supplier), (Throwable) null);
    }

    /**
     * Logs a message which is only to be constructed if the logging level is
     * the DIAG level.
     * <p>
     * Think of this like DEBUG, but on steroids. This level logs information in
     * a very fine-grained way. This is really fine-grained information, finer
     * even than DEBUG. Use the DIAG log level to provide very low level
     * information. Printing SOAP/HTML request and responses. That sort of
     * things. Don't consider flow tracing information for this log level.
     * </p>
     * <p>
     * Example </br>
     * {@code log.diag(() -> "System responded with - " + response.toString());}
     * </p>
     *
     * @param supplier
     *            A lambda function, which when called, produces the desired log
     *            message.
     */
    public void diag(Supplier<?> supplier) {
        if (logger.isEnabled(DIAG))
            logger.logIfEnabled(FQCN, DIAG, null, filteredMsg(supplier), (Throwable) null);
    }

    /**
     * This method should be used for trace level logging. Will be consumed by
     * tracing aspect only. Scope is set to default.
     * <p>
     * e.g. {@code log.trace(() -> "Entering method " + methodName);}
     *
     * @param supplier
     *            lambda expression
     */
    void trace(Supplier<?> supplier) {
        if (logger.isTraceEnabled())
            logger.logIfEnabled(FQCN, Level.TRACE, null, filteredMsg(supplier), (Throwable) null);
    }

    /**
     * This method filters the log output and gets rid of all the sensitive
     * content.
     *
     * @param supplier
     *            lambda expression
     * @return String value filtered of all sensitive contents
     */
    private String filteredMsg(Supplier<?> supplier) {
        return null;
    }
}
