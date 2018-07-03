package com.saurabh.logger.sinks;

import com.saurabh.logger.ILoggingEvent;
import com.saurabh.logger.LoggingEvent;


/**
 *	Extends {@link FileSink} to append extra info while logging. 
 * Name usage of this class if for checking subclass behavior in case of dynamic loading.
 * file_extra property will be set on this class while file_location property will be set on corresponding {@link FileSink} class
 * 
 * Would be useful for checking same sink implemenations in case of subclassing too
 * @author Saurabh
 */
@SinkType(type="fileextra")
public class FileExtraInfoSink extends FileSink {

	String extraInfo;
	public FileExtraInfoSink() {}
  
	@MethodParam(name="file_extra", type=String.class)
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
  
	public String getExtraInfo() {
		return extraInfo;
	}
	
	@Override
	public void write(ILoggingEvent loggingEvent) {
		LoggingEvent loggingEvent2 = (LoggingEvent)loggingEvent;
		loggingEvent2.setFormattedMessage(String.format("%s %s", new Object[] { loggingEvent2.getFormattedMessage(), extraInfo }));
		super.write(loggingEvent);
	}
  
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (getClass() != obj.getClass()) { 
			return false;
		}
		FileExtraInfoSink fileExtraInfoSink = (FileExtraInfoSink)obj;
		if ( this.getExtraInfo().equals(fileExtraInfoSink.getExtraInfo()) ) {
			return super.equals(obj);
		}
		return false;
	}
}
