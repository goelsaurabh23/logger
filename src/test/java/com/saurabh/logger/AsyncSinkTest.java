package com.saurabh.logger;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.saurabh.logger.sinks.AsyncSink;
import com.saurabh.logger.sinks.FileExtraInfoSink;
import com.saurabh.logger.sinks.FileSink;

/**
 * Basic test cases for {@link FileExtraInfoSink}
 * @author Saurabh
 */
public class AsyncSinkTest {

	RouteInfo routeInfo;
	String fileLocation = "/var/log/logger/info.log";
	String tsFormat = "dd­-mm­-yyyy-­hh-­mm-­ss";
	
	@Before
	public void loadConfiguration() {
	    Map<String, String> params = new HashMap();
	    params.put("ts_format", tsFormat);
	    params.put("log_level", "INFO");
	    params.put("sink_type", "FILE");
	    params.put("file_location", fileLocation);
	    params.put("thread_model", "MULTI");
	    params.put("write_mode", "ASYNC");
	    LoggerConfig.fromMap(params);
	    
	    routeInfo = Logger.getCurrentConfig().getRoutingMap().get(Level.INFO);
	}
	
	@Test
	//Check if routeinfo maps to AsyncSink instance
	public void testRoutedSinkIsFileExtraInfoSink() {
		Assert.assertTrue(routeInfo.routedSink instanceof AsyncSink);
	}
	
	@Test
	//Check if AsyncSink wrapping FileSink Instance
	public void testAsyncSinkWrappingFileSink() {
		AsyncSink asyncSink = (AsyncSink) routeInfo.routedSink;
		Assert.assertTrue(asyncSink.getWrappedSink() instanceof FileSink);
	}
	
	@Test
	//Check if file location is set properly
	public void fileLocationSetProperly() {
		AsyncSink asyncSink = (AsyncSink) routeInfo.routedSink;
		FileSink fileSink = (FileSink) asyncSink.getWrappedSink();
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
	//Check if async worker threads count is currently default max count
	public void checkMaxWorkerCount() {
		AsyncSink asyncSink = (AsyncSink) routeInfo.routedSink;
		Assert.assertEquals(asyncSink.getWorkerThreadsCount(), GlobalConstants.DEFAULT_ASYNC_WORKERS);
	}
	
	@Test
	//Check if asyncSink & wrapped fileSink instances are initialized or not before as we are doing lazy initialization it shouldn't till be logged
	public void checkAsyncSinkInitialized() {
		AsyncSink asyncSink = (AsyncSink) routeInfo.routedSink;
		FileSink fileSink = (FileSink) asyncSink.getWrappedSink();
		
		Assert.assertFalse(asyncSink.isStarted());
		Assert.assertFalse(fileSink.isStarted());
		
		Assert.assertNull(fileSink.getOutputStream());
	}
	
	@Test
	//Check if asyncSink & wrapped fileSink instances are initialized after logging 
	public void checkAsyncSinkInitializedAfterLogging() {
		Logger.info(FileSinkTest.class.getSimpleName(), "Testing AsyncSink");
		AsyncSink asyncSink = (AsyncSink) routeInfo.routedSink;
		FileSink fileSink = (FileSink) asyncSink.getWrappedSink();

		Assert.assertTrue(asyncSink.isStarted());
		Assert.assertTrue(fileSink.isStarted());
		
		Assert.assertNotNull(fileSink.getOutputStream());
	}
	
	@After
	public void releaseResources() {
		Logger.init(LoggerConfig.defaultConfig());
	}

}
