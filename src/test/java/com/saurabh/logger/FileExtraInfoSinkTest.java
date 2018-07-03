package com.saurabh.logger;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.saurabh.logger.sinks.FileExtraInfoSink;

/**
 * Basic test cases for {@link FileExtraInfoSink}
 * @author Saurabh
 */
public class FileExtraInfoSinkTest {

	RouteInfo routeInfo;
	String fileLocation = "/var/log/logger/info.log";
	String fileExtraInfo = "Let's code";
	String tsFormat = "dd­-mm­-yyyy-­hh-­mm-­ss";
	
	@Before
	public void loadConfiguration() {
	    Map<String, String> params = new HashMap();
	    params.put("ts_format", tsFormat);
	    params.put("log_level", "INFO");
	    params.put("sink_type", "FILEEXTRA");
	    params.put("file_location", fileLocation);
	    params.put("file_extra", fileExtraInfo);
	    params.put("thread_model", "SINGLE");
	    params.put("write_mode", "SYNC");
	    LoggerConfig.fromMap(params);
	    
	    routeInfo = Logger.getCurrentConfig().getRoutingMap().get(Level.INFO);
	}
	
	@Test
	//Check if routeinfo maps to FileExtraSInk instance
	public void testRoutedSinkIsFileExtraInfoSink() {
		Assert.assertTrue(routeInfo.routedSink instanceof FileExtraInfoSink);
	}
	
	@Test
	//Check if file location is set properly
	public void fileLocationSetProperly() {
		FileExtraInfoSink fileExtraInfoSink = (FileExtraInfoSink) routeInfo.routedSink;
		Assert.assertEquals(fileExtraInfoSink.getFile(), fileLocation);
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
	//Check if fileExtraInfoSink initialized or not before as we are doing lazy initialization it shouldn't till be logged
	public void checkFileSinkInitialized() {
		FileExtraInfoSink fileExtraInfoSink = (FileExtraInfoSink) routeInfo.routedSink;
		Assert.assertFalse(fileExtraInfoSink.isStarted());
		Assert.assertNull(fileExtraInfoSink.getOutputStream());
	}
	
	@Test
	//Check if fileExtraInfoSink initialized after logging 
	public void checkFileSinkinitializedAfterLogging() {
		Logger.info(FileSinkTest.class.getSimpleName(), "Testing fileExtraInfoSink");
		FileExtraInfoSink fileExtraInfoSink = (FileExtraInfoSink) routeInfo.routedSink;
		Assert.assertTrue(fileExtraInfoSink.isStarted());
		Assert.assertNotNull(fileExtraInfoSink.getOutputStream());
	}
	
	@After
	public void releaseResources() {
		Logger.init(LoggerConfig.defaultConfig());
	}

}
