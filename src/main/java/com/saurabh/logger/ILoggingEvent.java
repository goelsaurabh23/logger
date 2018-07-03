package com.saurabh.logger;

import com.saurabh.logger.sinks.Sink;

/**
 * Looging event contract containing the key properties required for logging. 
 * {@link Sink} will receive this event in their {@link Sink #write(loggingEvent)} method
 * @author Saurabh
 */
public interface ILoggingEvent {
	
	/**
	 * Get the current logging thread name
	 * @return
	 */
    String getThreadName();

    /**
     * {@link Level} Log current level
     * @return
     */
    Level getLevel();

    /**
     * {@link LogMessage #getContent()} to be logged
     * @return
     */
    String getContent();

    /**
     * Get object array to be added while logging, currently this feature is not supported will be added in future
     * @return
     */
    Object[] getArgumentArray();

    /**
     * Get formatted message which has to be logged through different sinks
     * @return
     */
    String getFormattedMessage();

    /**
     * {@link LogMessage #getNameSpace()} to identify the part of application sending the message
     * @return
     */
    String getNameSpace();
    
    /**
     * Timestamp format to be used while logging or formatting the message
     * @return
     */
    String tsFormat();
    
    /**
     * Current timestamp when {@link LogMessage} is routed to desired {@link Sink}
     * @return
     */
    long getCurrentTs();
}
