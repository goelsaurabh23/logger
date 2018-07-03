package com.saurabh.logger.sinks;

import com.saurabh.logger.ILoggingEvent;
import com.saurabh.logger.Level;
import com.saurabh.logger.Logger;

/**
 * Sink are used as destination for {@link Logger} log messages
 * Log message are logged to appropriate sink implementations based on {@link Level}
 * 
 * Annotation {@link SinkType} must be added to implemented sink classes for dynamic property association
 * In order to dynamic sink classes to be loaded either sink fully qualified class name to be supplied during configuration 
 * 
 * @author Saurabh
 */
public interface Sink {

    /**
     * Get the name of this sink. The name uniquely identifies the appender.
     */
    String getName();

    /**
     * Update sink name
     * @param name
     */
    void setName(String name);
    
    /**
     * Boolean flag to tell weather sink is currently running or stopped
     */
    
    boolean isStarted();
    
	/**
	 * Initialize the sink (open a file for example).
	 *
	 * @param loggerConfig
	 *            Configuration of logger
	 *
	 * @throws Exception
	 *             Failed to initialize the writer
	 */
	void init();

	/**
	 * Write a log entry.
	 *
	 * @param logMessage
	 *            Log message to output
	 *
	 * @throws Exception
	 *             Failed to write the log entry
	 */
	void write(ILoggingEvent loggingEvent);

	/**
	 * Flush this sink and force any buffered data to output.
	 *
	 * @throws Exception
	 *             Failed to flush
	 */
	void flush();

	/**
	 * Close the sink and release all resources. 
	 *
	 * @throws Exception
	 *             Failed to close the writer
	 */
	void close();

}
