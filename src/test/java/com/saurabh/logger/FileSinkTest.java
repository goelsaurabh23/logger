package com.saurabh.logger;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.saurabh.logger.sinks.FileSink;

/**
 * Basic test cases for {@link FileSink}		
 * @author Saurabh
 */
public class FileSinkTest {
	
	RouteInfo routeInfo;
	String fileLocation = "/var/log/logger/info.log";
	String tsFormat = "dd­-mm­-yyyy-­hh-­mm-­ss";
	
	@Before
	public void loadConfiguration() {
	    Map<String, String> params = new HashMap<String, String>();
	    params.put("ts_format", tsFormat);
	    params.put("log_level", "INFO");
	    params.put("sink_type", "FILE");
	    params.put("file_location", fileLocation);
	    params.put("thread_model", "SINGLE");
	    params.put("write_mode", "SYNC");
	    LoggerConfig.fromMap(params);
	    
	    routeInfo = Logger.getCurrentConfig().getRoutingMap().get(Level.INFO);
	}
	
	@Test
	//Check if routeinfo maps to FileSInk instance
	public void testRoutedSinkIsFileSink() {
		Assert.assertTrue(routeInfo.routedSink instanceof FileSink);
	}
	
	@Test
	//Check if file location is set properly
	public void fileLocationSetProperly() {
		FileSink fileSink = (FileSink) routeInfo.routedSink;
		Assert.assertEquals(fileSink.getFile(), fileLocation);
	}
	
	@Test
	//Check if currently active sinks is equal to 1
	public void checkCurrentlyActiveSinksCount() {
		Assert.assertEquals(Logger.getCurrentConfig().getCurrentlyActiveSinks().size(), 1);
	}
	
	@Test
	//Check if ts format set properly
	public void checkTsFormatSetProperly() {
		Assert.assertEquals(routeInfo.tsFormat, tsFormat);
	}
	
	@Test
	//Check if fileSink initialized or not before as we are doing lazy initialization it shouldn't till be logged
	public void checkFileSinkInitialized() {
		FileSink fileSink = (FileSink) routeInfo.routedSink;
		Assert.assertFalse(fileSink.isStarted());
		Assert.assertNull(fileSink.getOutputStream());
	}
	
	@Test
	//Check if fileSink initialized after logging 
	public void checkFileSinkinitializedAfterLogging() {
		Logger.info(FileSinkTest.class.getSimpleName(), "Testing fileSink");
		FileSink fileSink = (FileSink) routeInfo.routedSink;
		Assert.assertTrue(fileSink.isStarted());
		Assert.assertNotNull(fileSink.getOutputStream());
	}
	
	@After
	public void releaseResources() {
		Logger.init(LoggerConfig.defaultConfig());
	}
}
