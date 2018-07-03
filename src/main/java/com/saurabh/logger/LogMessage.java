package com.saurabh.logger;

/**
 * Encapsulate info regarding message to be logged
 * @author Saurabh
 */
public class LogMessage {

	private String content;
	private Level level;
	private String nameSpace;
	
	public LogMessage(String content, Level level, String nameSpace) {
		this.content = content;
		this.level = level;
		this.nameSpace = nameSpace;
	}
	
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * @return the nameSpace
	 */
	public String getNameSpace() {
		return nameSpace;
	}

	/**
	 * @param nameSpace the nameSpace to set
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
}
