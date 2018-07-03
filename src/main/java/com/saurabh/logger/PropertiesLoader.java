package com.saurabh.logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Properties;

import com.saurabh.logger.sinks.AsyncSink;
import com.saurabh.logger.sinks.MethodParam;
import com.saurabh.logger.sinks.Sink;
import com.saurabh.logger.sinks.SinkType;

/**
 * Loads {@link Logger} configuration properties from {@link Map} passed during initialization.
 * 
 * <ul>
 * 		<li>Read order will be based on <b>log_level</b> and corresponding <b> sink_type</b> fields. </li>
 * 		<li>In case same <b>log_level</b> is encountered before then new sink info will overwrite the previous one </li>
 * </ul>
 * @author Saurabh 
 */
public final class PropertiesLoader {
	/**
	 * Logger level property name.
	 */
	public static final String LOG_LEVEL_PROPERTY = "log_level";

	/**
	 * Datetime format property name.
	 */
	public static final String TS_FORMAT_PROPERTY = "ts_format";
	
	/**
	 * Sink Type property name
	 */
	public static final String SINK_TYPE_PROPERTY = "sink_type";

	/**
	 * Sink fully qualified class name 
	 */
	public static final String SINK_CLASS_PROPERTY = "sink_class";

	/**
	 * Sink thread model property name
	 */
	public static final String THREAD_MODEL_PROPERTY = "thread_model";
	
	/**
	 * Sink write mode property name
	 */
	public static final String WRITE_MODE_PROPERTY = "write_mode";
	
	/**
	 * Update logger configuration from .
	 *
	 * @param 
	 * 		properties {@link Properties} 
	 *            
	 * @return 
	 * 		Updated {@link LoggerConfig} 
	 *            
	 */
	public static LoggerConfig readProperties(final Properties properties, final LoggerConfig loggerConfig) {
		Level level  = readLevel(properties);
		String tsFormat = readTsFormat(properties);
		Sink sink = readSink(properties);

		if ( sink == null ) {
			//No sink association can be done return
			return loggerConfig;
		}
		
	    Sink alreadyActiveSink = getAlreadyActiveSink(sink, loggerConfig);
	    sink = alreadyActiveSink == null ? sink : alreadyActiveSink;

		String threadModel  = readThreadModel(properties);
		String writeMode = readWriteMode(properties);
		
		//Check if write mode is of Async type, if yes than wrap the current sink class with Async sink implementation
		if ( writeMode != null && writeMode.equalsIgnoreCase(GlobalConstants.ASYNC_WRITE_MODE) ) {
			AsyncSink asyncSink = new AsyncSink();
			asyncSink.setWrappedSink(sink);
			if ( threadModel != null ) {
				asyncSink.setThreadModel(threadModel);
			}
			
			Sink alreadyActiveAsyncSink = getAlreadyActiveSink(asyncSink, loggerConfig);
		    asyncSink = alreadyActiveAsyncSink == null ? asyncSink : (AsyncSink)alreadyActiveAsyncSink;

			//Update the level route mapping
			loggerConfig.addOrUpdateLevelRouteInfo(level, new RouteInfo(tsFormat, asyncSink));
			if (alreadyActiveAsyncSink == null) {
				loggerConfig.addActiveSync(asyncSink);
			}
		} else {
			loggerConfig.addOrUpdateLevelRouteInfo(level, new RouteInfo(tsFormat, sink));
		    if (alreadyActiveSink == null) {
		    	loggerConfig.addActiveSync(sink);
		    }
		}
		return loggerConfig;
	}
	
	private static Sink getAlreadyActiveSink(Sink sink, LoggerConfig loggerConfig) {
		for (Sink activeSink : loggerConfig.getCurrentlyActiveSinks()) {
			if (activeSink.equals(sink)) {
				return activeSink;
			}
		}
		return null;
	}

	private static Level readLevel(final Properties properties) {
		String levelName = properties.getProperty(LOG_LEVEL_PROPERTY);
		if (levelName != null && levelName.length() > 0) {
			return Level.valueOf(levelName.toUpperCase(Locale.ENGLISH));
		} else {
			return null;
		}
	}

	private static String readTsFormat(final Properties properties) {
		return properties.getProperty(TS_FORMAT_PROPERTY);
	}
	
	private static String readThreadModel(final Properties properties) {
		if ( properties.containsKey(THREAD_MODEL_PROPERTY) ) {
			return properties.getProperty(THREAD_MODEL_PROPERTY);
		} else {
			return null;
		}
	}
	
	private static String readWriteMode(final Properties properties) {
		if ( properties.containsKey(WRITE_MODE_PROPERTY) ) {
			return properties.getProperty(WRITE_MODE_PROPERTY);
		} else {
			return null;
		}
	}
	
	/**
	 * Read {@link Sink} data from {@link Properties} & updates level-sink mapping through 
	 * {@link LoggerConfig #addOrUpdateLevelSinkMapping(Level, Sink)}
	 * @param properties
	 * @param loggerConfig
	 * @return
	 */
	private static Sink readSink(final Properties properties) {
		Sink sink = null;
		//All sinks will always starts with type, then other optional properties
		//Check the sink type
		String sinkType = properties.getProperty(SINK_TYPE_PROPERTY);
		
		if ( sinkType != null && sinkType.length() > 0 ) {
			String sinkClassName = null;
			//Check if sink class name property has been given
			if ( properties.containsKey(SINK_CLASS_PROPERTY) ) {
				sinkClassName = properties.getProperty(SINK_CLASS_PROPERTY);
			} else {
				//Check if sink type has already been defined in sink package
				String preDefinedSinkClass = Utils.getSinkImplementation(sinkType);
				if ( preDefinedSinkClass != null ) {
					sinkClassName = preDefinedSinkClass;
				}
			}

			if ( sinkClassName != null && sinkClassName.length() > 0 ) {
				try {
					Class<?> sinkClass = Class.forName(sinkClassName);
					//Check if class adheres to sink implementation
					if ( !Sink.class.isAssignableFrom(sinkClass) ) {
						//This sink can't be used, log an internal log warning 
						InternalLog.warn("Given class " + sinkClassName + " doesn't implement Sink interface");
					} else {
						//Build the sink class
						SinkType sinkTypeAnnotation= sinkClass.getAnnotation(SinkType.class);
						if ( sinkTypeAnnotation != null ) {
							if ( sinkType.equalsIgnoreCase(sinkTypeAnnotation.type()) ) {
								sink = loadSink(properties, sinkClass);
							}
						}
					}
				} catch (ClassNotFoundException ex) {
					InternalLog.warn("Cannot find class " + sinkClassName);
				}
			}
		}
		if ( sink == null ) {
			InternalLog.error("Failed to load or initialize sink type " + sinkType);
		} else {
			//Set the sink name to sink type
			sink.setName(sinkType);
	    }
		return sink;
	}
	
	/**
	 * Initialize {@link Sink} object using {@link SinkType} & {@link MethodParam} annotations
	 * While initializing {@link Sink} object first create its object from the default constructor and then set the configuration property 
	 * based on setter methods implementing {@link MethodParam} annotation

	 * @param properties
	 * 			{@link Properties} object passed during configuration 
	 * @param sinkClass
	 * 			Fully qualified name of the class which matches the given sink type
	 * @return
	*/
	private static Sink loadSink(Properties properties, Class<?> sinkClass) {
		Sink sink = null;
		for ( Constructor<?> constructor : sinkClass.getDeclaredConstructors()) {
			if (constructor.getParameterCount() == 0) {
				try {
					sink = (Sink)constructor.newInstance(new Object[0]);
				} catch (Exception e) {
					InternalLog.error(e, "Exception while initializing sink object, please check if default constructor is there");
				}
			}
		}
		
		if (sink != null) {
			for ( Method method : sinkClass.getMethods() ) {
				if ( (method.isAnnotationPresent(MethodParam.class)) && (method.getParameterCount() == 1) ) {
					MethodParam methodParam = (MethodParam)method.getAnnotation(MethodParam.class);
					
					if ( properties.containsKey(methodParam.name()) ) {
						String propertyValue = properties.getProperty(methodParam.name());
						Object paramValue = convertToDesiredType(methodParam.type(), propertyValue);
						try{
							if ( paramValue != null ) {
								method.invoke(sink, new Object[] { paramValue });
							}
						} catch (Exception e) {
							InternalLog.error(e, "Exception while property method invocation, please check if MethodParam annotation is properly written");
						}
					}
				}
			}
			return sink;
		}
		return null;
	}

	public static Object convertToDesiredType(Class<?> type, String value) {
		Object obj = null;
		if (boolean.class.equals(type)) {
			if ("true".equalsIgnoreCase(value)) {
				obj = Boolean.TRUE;
			} else if ("false".equalsIgnoreCase(value)) {
				obj = Boolean.FALSE;
			} else {
				InternalLog.error("Invalid boolean passed");
				return null;
			}
		} else if (int.class.equals(type)) {
			try {
				obj = Integer.parseInt(value);
			} catch (NumberFormatException ex) {
		        InternalLog.error("Invalid number" + value + " passed for property ");
				return null;
			}
		} else if (String.class.equals(type)) {
			obj = value;
		} else if (String[].class.equals(type)) {
			obj = parseStrings(value);
		} else {
			InternalLog.error(
		            "An unsupported type" + type.getName() + " is passed, currently supported types are String, String[], int, boolean");
		}
		return obj;
	}

	private static String[] parseStrings(final String value) {
		int size = 1;
		for (int i = 0; i < value.length(); ++i) {
			if (value.charAt(i) == ',') {
				++size;
			}
		}

		String[] values = new String[size];

		int start = 0;
		int counter = 0;
		for (int i = 0; i < value.length(); ++i) {
			if (value.charAt(i) == ',') {
				values[counter] = start >= i ? "" : value.substring(start, i).trim();
				start = i + 1;
				++counter;
			}
		}
		values[counter] = start >= value.length() ? "" : value.substring(start).trim();

		return values;
	}
}
