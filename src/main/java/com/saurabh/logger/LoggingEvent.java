package com.saurabh.logger;

public class LoggingEvent implements ILoggingEvent {

    /**
     * The name of thread in which this logging event was generated.
     */
    private String threadName;
    private Level level;
    private String content;
    private String nameSpace;
    private String formattedMessage;
    private Object[] argumentArray;

     //The number of milliseconds elapsed from 1/1/1970 until logging event was created.
    private long timeStamp;
    private String tsFormat;

    public LoggingEvent(LogMessage message, String tsFormat) {           
        this.level = message.getLevel();
        this.content = message.getContent();
        this.nameSpace = message.getNameSpace();
        this.timeStamp = System.currentTimeMillis();
        this.tsFormat = tsFormat;
        this.threadName = Thread.currentThread().getName();
    }

    public void setFormattedMessage(String formattedMessage) {
    	this.formattedMessage = formattedMessage;
    }

	public String getThreadName() {
		return threadName;
	}

	public Level getLevel() {
		return level;
	}

	public String getContent() {
		return content;
	}

	public Object[] getArgumentArray() {
		return argumentArray;
	}

	public String getFormattedMessage() {
		return formattedMessage;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public String tsFormat() {
		return tsFormat;
	}

	public long getCurrentTs() {
		return timeStamp;
	}
	
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(level).append("] ");
        sb.append(getFormattedMessage());
        return sb.toString();
    }
}
