package com.saurabh.logger;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.saurabh.logger.sinks.FileSink;

/**
 * Basic test cases to check if same {@link FileSink} instance will be there in case same file location is given for different log levels
 * @author Saurabh
 *
 */
public class FileSinkSingleInstanceTest {

	String fileLocation = "/var/log/logger/info.log";
	String infoTsFormat = "dd­-mm­-yyyy-­hh-­mm-­ss";
	String debugTsFormat = "dd­:mm­:yyyy:hh:­mm:­ss";
	
	RouteInfo infoRoute;
	RouteInfo debugRoute;
	
	@Before
	public void loadConfiguration() {
	    Map<String, String> params = new HashMap<String, String>();;
	    params.put("ts_format", infoTsFormat);
	    params.put("log_level", "INFO");
	    params.put("sink_type", "FILE");
	    params.put("file_location", fileLocation);
	    params.put("thread_model", "SINGLE");
	    params.put("write_mode", "SYNC");
	    LoggerConfig.fromMap(params);

	    //Add new settings for debug level
	    params = new HashMap<String, String>();
	    params.put("ts_format", debugTsFormat);
	    params.put("log_level", "DEBUG");
	    params.put("sink_type", "FILE");
	    params.put("file_location", fileLocation);
	    params.put("thread_model", "SINGLE");
	    params.put("write_mode", "SYNC");
	    LoggerConfig.fromMap(params);

	    infoRoute = Logger.getCurrentConfig().getRoutingMap().get(Level.INFO);
	    debugRoute = Logger.getCurrentConfig().getRoutingMap().get(Level.DEBUG);
	}
	
	@Test
	//Check if route info maps to FileSInk instance
	public void testRoutedSinkIsFileSink() {
		Assert.assertTrue(infoRoute.routedSink instanceof FileSink);
		Assert.assertTrue(debugRoute.routedSink instanceof FileSink);
	}
	
	@Test
	//Check if info and debug routed sinks are equals
	public void checkInfoDebugRoutesSingleFileSinkInstance() {
		Assert.assertTrue(infoRoute.routedSink.equals(debugRoute.routedSink));
	}
	
	@Test
	//Check if currently active sinks is equal to 1
	public void checkCurrentlyActiveSinksCount() {
		Assert.assertEquals(Logger.getCurrentConfig().getCurrentlyActiveSinks().size(), 1);
	}
	
	@Test
	//Check if ts format differs for info and debug route
	public void checkTsFormatSetProperly() {
		Assert.assertEquals(infoRoute.tsFormat, infoTsFormat);
		Assert.assertEquals(debugRoute.tsFormat, debugTsFormat);
	}
	
	@After
	public void releaseResources() {
		Logger.init(LoggerConfig.defaultConfig());
	}

}
