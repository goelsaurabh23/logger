package com.saurabh.logger;

import com.saurabh.logger.sinks.Sink;

/**
 * Core logic class for {@link Logger}.
 * Route {@link LogMessage} to associated {@link Sink} while lazy initializaing it if neccessary
 * Convert messages to {@link ILoggingEvent} while adding common logging info like current timestamp, threadname etc.
 * @author Saurabh
 */
public final class LogEngine {

	public static void handleLogMessage(LogMessage message) {
		//Basic checks
		if ( message == null || message.getContent() == null || message.getLevel() == null || message.getNameSpace() == null ) {
			throw new IllegalStateException("Bad request, message params are missing");
		}
		
		//Check if logging configuration is set
		if ( Logger.getCurrentConfig() == null ) {
			throw new IllegalStateException("Please initialize logger before logging");
		}
		
		Sink routedSink;
		LoggingEvent loggingEvent;
		
		//Route log message based on level
		//Check if mapping is their else use default values
		if ( !Logger.getCurrentConfig().getRoutingMap().containsKey(message.getLevel()) ) {
			routedSink = Logger.getCurrentConfig().getDefaultSink();
			loggingEvent = new LoggingEvent(message, Logger.getCurrentConfig().getDefaultTsFormat());
		} else {
			RouteInfo routeInfo = Logger.getCurrentConfig().getRoutingMap().get(message.getLevel());
			routedSink  = routeInfo.routedSink;
			loggingEvent = new LoggingEvent(message, routeInfo.tsFormat);
		}
		
		//Format the log message as per message formatter currently being used
		loggingEvent.setFormattedMessage(MessageFormatter.format(loggingEvent));
		
		//Write the message to associated sink 
		//Check if sink is running or not
		if ( !routedSink.isStarted() ) {
			routedSink.init();
		}
		routedSink.write(loggingEvent);
	}
}
