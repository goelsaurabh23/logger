package com.saurabh.logger.examples;

import com.saurabh.logger.Logger;
import com.saurabh.logger.LoggerConfig;
import com.saurabh.logger.sinks.FileExtraInfoSink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Example {
	private static final String NAMESPACE = Example.class.getSimpleName();
	
	public Example() {}
  
	public static void main(String[] args) {
		
		//checkSubclassEquality();
		//checkSinkClassProperty();
		
		String url = "jdbc:mysql://localhost/test";
		try {
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Connection conn = DriverManager.getConnection (url, "root", "password");
			System.out.println(conn.toString());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Check if same properties will be given to subclasses then same instance will be used for logging
	 */
	public static void checkSubclassEquality() {
	    Map<String, String> params = new HashMap();
	    params.put("ts_format", "dd­-mm­-yyyy-­hh-­mm-­ss");
	    params.put("log_level", "INFO");
	    params.put("sink_type", "FILEEXTRA");
	    params.put("file_location", "/var/log/logger/info.log");
	    params.put("file_extra", "Add extra info");
	    params.put("thread_model", "SINGLE");
	    params.put("write_mode", "SYNC");
	    LoggerConfig.fromMap(params);
    
	    Map<String, String> params2 = new HashMap();
	    params2.put("ts_format", "dd­-mm­-yyyy-­hh-­mm-­ss");
	    params2.put("log_level", "DEBUG");
	    params2.put("sink_type", "FILEEXTRA");
	    params2.put("file_location", "/var/log/logger/info.log");
	    params2.put("file_extra", "Add extra info");
	    params2.put("thread_model", "SINGLE");
	    params2.put("write_mode", "SYNC");
	    LoggerConfig.fromMap(params2);
	    
	    Logger.info(NAMESPACE, "Log checkSubClassEquality");
	    Logger.debug(NAMESPACE, "Log checkSubClassEquality");

	    //Check if single instance of the same class is there
	    System.out.println("CheckSubClassEquality : active instances :" + Logger.getCurrentConfig().getCurrentlyActiveSinks().size());
	}
	
	/**
	 * Check if we pass sink_class property during configuration then does it gets loaded or not
	 * Do it with {@link FileExtraInfoSink} class as this will give us wider scope
	 */
	public static void checkSinkClassProperty() {
	    Map<String, String> params = new HashMap();
	    params.put("ts_format", "dd­-mm­-yyyy-­hh-­mm-­ss");
	    params.put("log_level", "INFO");
	    params.put("sink_type", "FILEEXTRA");
	    params.put("sink_class", "com.saurabh.logger.sinks.FileExtraInfoSink");
	    params.put("file_location", "/var/log/logger/info.log");
	    params.put("file_extra", "Add extra info");
	    params.put("thread_model", "SINGLE");
	    params.put("write_mode", "SYNC");
	    LoggerConfig.fromMap(params);
	    
	    //Are we able to log messages
	    Logger.info(NAMESPACE, "Log checkSinkClassProperty");

	}

}
