package com.saurabh.logger;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.saurabh.logger.sinks.AsyncSink;

/**
 * Basic test cases to check if multi {@link AsyncSink} instances will be there in case same file location is given for different log levels
 * @author Saurabh
 */
public class AsyncSinkMultiInstanceTest {

	String infoFileLocation = "/var/log/logger/info.log";
	String debugFileLocation = "/var/log/logger/debug.log";
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
	    params.put("file_location", infoFileLocation);
	    params.put("thread_model", "MULTI");
	    params.put("write_mode", "ASYNC");
	    LoggerConfig.fromMap(params);

	    //Add new settings for debug level
	    params = new HashMap<String, String>();
	    params.put("ts_format", debugTsFormat);
	    params.put("log_level", "DEBUG");
	    params.put("sink_type", "FILE");
	    params.put("file_location", debugFileLocation);
	    params.put("thread_model", "MULTI");
	    params.put("write_mode", "ASYNC");
	    LoggerConfig.fromMap(params);

	    infoRoute = Logger.getCurrentConfig().getRoutingMap().get(Level.INFO);
	    debugRoute = Logger.getCurrentConfig().getRoutingMap().get(Level.DEBUG);
	}
	
	@Test
	//Check if route info maps to asyncsink instance
	public void testRoutedSinkIsFileSink() {
		Assert.assertTrue(infoRoute.routedSink instanceof AsyncSink);
		Assert.assertTrue(debugRoute.routedSink instanceof AsyncSink);
	}
	
	@Test
	//Check if info and debug routed sinks are not equals
	public void checkInfoDebugRoutesMultiFileSinkInstance() {
		Assert.assertFalse(infoRoute.routedSink.equals(debugRoute.routedSink));
	}
	
	@Test
	//Check if currently active sinks is equal to 2
	public void checkCurrentlyActiveSinksCount() {
		Assert.assertEquals(Logger.getCurrentConfig().getCurrentlyActiveSinks().size(), 2);
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
