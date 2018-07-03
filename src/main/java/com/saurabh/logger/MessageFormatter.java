package com.saurabh.logger;

public final class MessageFormatter {
	private static final String FORMATTING_SPACE = " ";
	/**
	 * Formats {@link ILoggingEvent} message before logging 
	 * Current format is of type { Datetime [ThreadName] Level nameSpace - content } 
	 * like { 01-07-2018 07:01:22 [main] INFO com.phonepe.logger.messageFormatter - Assignment is ready
	 * 
	 * Currently formatted message length will be of max 256 characters & exception, throwable logging will be added in future
	 * 
	 * @param event
	 * 			{@link ILoggingEvent} to be logged
	 * @return
	 */
	public static String format(ILoggingEvent event) {
		StringBuilder sb = new StringBuilder(256);
		
		//Add the date
		sb.append(Utils.formatTimeStamp(event.getCurrentTs(), event.tsFormat())).append(FORMATTING_SPACE);
		
		//Add thread name
		sb.append(String.format("[%s]", event.getThreadName())).append(FORMATTING_SPACE);
		
		//Add log level
		sb.append(event.getLevel().name()).append(FORMATTING_SPACE);
		
		//Add nameSpace
		sb.append(event.getNameSpace()).append(FORMATTING_SPACE);
		
		//Add log content
		sb.append(event.getContent());
		
		return sb.toString();
	}
}
