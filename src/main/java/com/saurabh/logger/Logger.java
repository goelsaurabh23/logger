package com.saurabh.logger;

/**
 * Entry of all library public API; you can set configurations and launch log requests using this
 * class with only a static method call.
 *
 * @author Saurabh
 */
public final class Logger {

	private static LoggerConfig loggerConfig;
	
	 //The constructor of this class is meaningless
    private Logger() {
    	 throw new UnsupportedOperationException();
    }
    
    /**
     * Check and set {@link #loggerConfig} field. this operation guarantee the reading of config for
     * later operation is safe.
     */
    private static synchronized void safelySetConfig(LoggerConfig config) throws RuntimeException {
        if (config == null) {
            throw new NullPointerException("Customized config cannot be null!");
        }
        loggerConfig = config;
    }
    
    /**
     * Initialize using customized config.
     */
    public static void init(LoggerConfig config) {
        safelySetConfig(config);
    }
    
    /**
     * Get current configuration.
     *
     * @return Current configuration; or default configuration if {@link #init(LoggerConfig)} is not called yet.
     */
    public static LoggerConfig getCurrentConfig() {
        if ( loggerConfig == null ) {
            init(LoggerConfig.defaultConfig());
        }
        return loggerConfig;
    }

    public static void log(LogMessage message) {
    	LogEngine.handleLogMessage(message);
    }
    
    /**
     * Follows standard java debug logging format 
     * @param nameSpace
     * 		Identify part of the application that sent the message
     * @param content
     * 		Content to be logged
     */
    public static void debug(String nameSpace, String content) {
    	LogMessage logMessage = new LogMessage(content, Level.DEBUG, nameSpace);
    	LogEngine.handleLogMessage(logMessage);
    }
    
    /**
     * Follows standard java info logging format 
     * @param nameSpace
     * 		Identify part of the application that sent the message
     * @param content
     * 		Content to be logged
     */
    public static void info(String nameSpace, String content) {
    	LogMessage logMessage = new LogMessage(content, Level.INFO, nameSpace);
    	LogEngine.handleLogMessage(logMessage);
    }

    /**
     * Follows standard java info logging format 
     * @param nameSpace
     * 		Identify part of the application that sent the message
     * @param content
     * 		Content to be logged
     */
    public static void error(String nameSpace, String content) {
    	LogMessage logMessage = new LogMessage(content, Level.ERROR, nameSpace);
    	LogEngine.handleLogMessage(logMessage);
    }

    /**
     * Follows standard java info logging format 
     * @param nameSpace
     * 		Identify part of the application that sent the message
     * @param content
     * 		Content to be logged
     */
    public static void warn(String nameSpace, String content) {
    	LogMessage logMessage = new LogMessage(content, Level.WARN, nameSpace);
    	LogEngine.handleLogMessage(logMessage);
    }
    
    /**
     * Follows standard java info logging format 
     * @param nameSpace
     * 		Identify part of the application that sent the message
     * @param content
     * 		Content to be logged
     */
    public static void fatal(String nameSpace, String content) {
    	LogMessage logMessage = new LogMessage(content, Level.FATAL, nameSpace);
    	LogEngine.handleLogMessage(logMessage);
    }

    /**
     * Allows logging without specifying log level, default log level defined in configuration will be taken
     * @param nameSpace
     * 		Identify part of the application that sent the message
     * @param content
     * 		Content to be logged
     */
    public static void log(String nameSpace, String content) {
    	LogMessage logMessage = new LogMessage(content, LoggerConfig.DEFAULT_LEVEl, nameSpace);
    	LogEngine.handleLogMessage(logMessage);
    }
    
    /**
     * Log message with all three fields
     * @param level
     * @param content
     * @param nameSpace
     */
    public static void log(Level level, String content, String nameSpace) {
      LogMessage logMessage = new LogMessage(content, level, nameSpace);
      LogEngine.handleLogMessage(logMessage);
    }
}
