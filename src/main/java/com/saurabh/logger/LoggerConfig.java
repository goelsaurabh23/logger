package com.saurabh.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.saurabh.logger.sinks.ConsoleSink;
import com.saurabh.logger.sinks.Sink;

public final class LoggerConfig {

	public static final String DEFAULT_TS_FORMAT = "dd­-mm­-yyyy-­hh-­mm­-ss";
	public static final Level DEFAULT_LEVEl = Level.INFO;
	
	//Default tsFormat for all the message in case no format is supplied
	private String defaultTsFormat;
			
	//Default Sink to be used in case on level-sink mapping is provided
	private Sink defaultSink;
	
	//Default level to be used in case on info is provided
	private Level defaultLevel;
	
	//Levels & routeInfo Mapping
	private Map<Level, RouteInfo> routingMap;
	
	//Keep a list of currently active sinks
	private List<Sink> currentlyActiveSinks;


	public LoggerConfig(String tsFormat, Level defaultLevel, Sink defaultSink, Map<Level, RouteInfo> routingMap, 
			List<Sink> currentlyActiveSinks) {

		this.defaultTsFormat = tsFormat;
		this.defaultSink = defaultSink;
		this.routingMap = routingMap;
		this.defaultLevel = defaultLevel;
		this.currentlyActiveSinks = currentlyActiveSinks;
	}
	
	/**
	 * Create {@link LoggerConfig} object based on the default configuration.
	 *
	 * @return {@link LoggerConfig}
	 */
	public static LoggerConfig defaultConfig() {
		return new LoggerConfig(DEFAULT_TS_FORMAT, DEFAULT_LEVEl, new ConsoleSink("console"), 
				new HashMap<Level, RouteInfo>(), new ArrayList<Sink>());
	}

	/**
     * Check whether a config is valid or not. This is useful when our library importing new
     * config items, and can prevent user's wrong usage.
     * <p>
     * If all condition check pass, then this method do nothing; otherwise it would throw a
     * Runtime Exception.
     * </p>
     *
     * @param config config object to detect
     * @throws RuntimeException maybe any subclass of RuntimeException if any assertion failed.
     */
    public static void checkConfigSafe(LoggerConfig config) throws RuntimeException {
        if (config == null) {
            throw new NullPointerException("Customized config cannot be null!");
        }
    }
    
    /**
	 * Load properties from a {@link Map}. Can be used to add new or update previously added configurations too
	 *
	 * @param map
	 *            Map with configuration
	 * @return {@link LoggerConfig} object
	 */
	public static LoggerConfig fromMap(final Map<String, String> map) {
		
		Properties properties = new Properties();

		for (Entry<String, String> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value != null) {
				properties.put(entry.getKey(), value.toString());
			}
		}

		return PropertiesLoader.readProperties(properties, Logger.getCurrentConfig());
	}
	
	/**
	 * Maps a {@link Sink} to a log {@link Level}. 
	 * This will always replace the previous mapping as it might be possible some new properties has been added or updated
	 * Before replacing it will close the previous sink. 
	 * Its always good to bring the state back to zero before starting new as less book keeping has to be done
	 * @param level
	 * @param routeInfo
	 */
	public void addOrUpdateLevelRouteInfo(Level level, RouteInfo routeInfo) {
		if ( routingMap.containsKey(level) ) {
			Sink previousSink = routingMap.get(level).routedSink;
			previousSink.close();
		}
		//Every possible has been done, just replace the previous one with new one now
		routingMap.put(level, routeInfo);
	}

	public Map<Level, RouteInfo> getRoutingMap() {
		return routingMap;
	}
	
	public String getDefaultTsFormat() {
		return defaultTsFormat;
	}
	
	public Sink getDefaultSink() {
		return defaultSink;
	}
	
	/**
	 * Get the list of currently active sinks
	 * @return
	 */
	 public List<Sink> getCurrentlyActiveSinks() {
		 return currentlyActiveSinks;
	 }
		 
	 /**
	  * Closes all the writers while keeping the routing and activeSink info intact 
	  */
	 public void closeWriters() {
		 
	 }
	 /**
	  * Add {@link Sink} to currently active sinks
	  * @param sink
	  */
	 public void addActiveSync(Sink sink) {
		 currentlyActiveSinks.add(sink);
	 }
}
