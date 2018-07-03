package com.saurabh.logger.sinks;

import com.saurabh.logger.GlobalConstants;
import com.saurabh.logger.ILoggingEvent;

/**
 * ConsoleSink writes log events to <code>System.out</code>. 
 * Since all logging level are equivalent for the time being, we will logging them to the same system stream.
 *
 * @author Saurabh
 */
@SinkType(type = GlobalConstants.CONSOLE_SINK_DEFAULT_TYPE)
public class ConsoleSink implements Sink {
	
	private String name;
	
	public ConsoleSink() {
		
	}
	
	public ConsoleSink(String name) {
		this.name = name;
	}
	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public void flush() {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Since not too much initialization is required for this simple sink, it's just appending to already opened system stream it will 
	 * always be in started state
	 */
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return true;
	}

	public void write(ILoggingEvent loggingEvent) {
		// TODO Auto-generated method stub
		System.out.println(loggingEvent.getFormattedMessage());
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (getClass() != obj.getClass()) { 
			return false;
	    }
	    return true;
	  }
}
