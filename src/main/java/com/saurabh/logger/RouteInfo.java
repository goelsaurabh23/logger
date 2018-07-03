package com.saurabh.logger;

import com.saurabh.logger.sinks.Sink;

/**
 * Wraps {@link Sink} & timestamp format info for routing logs based on {@link Level} 
 * @author Saurabh
 */
public class RouteInfo {
	//Timestamp format to be used when log message is routed to given sink
	public String tsFormat;
	
	public Sink routedSink;

	public RouteInfo(String tsFormat, Sink roSink ) {
		this.tsFormat = tsFormat;
		this.routedSink = roSink;
	}
	
	public void setTsFormat(String tsFormat) {
		this.tsFormat = tsFormat;
	}
}

