package com.saurabh.logger;

/**
 * Class to be used for logging internal issues.
 *
 * Log entries will be always output into the console.
 */
public final class InternalLog {

	private static final String PREFIX_WARNING = "logger-warn: ";
	private static final String PREFIX_ERROR = "logger-error: ";

	private static volatile String lastLogEntry = null;

	private InternalLog() {
	}

	/**
	 * Log an internal warning.
	 *
	 * @param message
	 *            Text to log
	 */
	public static void warn(final String message) {
		String logEntry = PREFIX_WARNING + message;
		if (!logEntry.equals(lastLogEntry)) {
			System.err.println(logEntry);
			lastLogEntry = logEntry;
		}
	}

	/**
	 * Log an internal warning.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void warn(final Throwable exception) {
		String message = exception.getMessage();
		if (message == null || message.length() == 0) {
			warn(exception.getClass().getName());
		} else {
			warn(message + " (" + exception.getClass().getName() + ")");
		}
	}

	/**
	 * Log an internal warning.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Text to log
	 */
	public static void warn(final Throwable exception, final String message) {
		String messageOfThrowable = exception.getMessage();
		if (messageOfThrowable == null || messageOfThrowable.length() == 0) {
			warn(message + " (" + exception.getClass().getName() + ")");
		} else {
			warn(message + " (" + exception.getClass().getName() + ": " + messageOfThrowable + ")");
		}
	}

	/**
	 * Log an internal error.
	 *
	 * @param message
	 *            Text to log
	 */
	public static void error(final String message) {
		String logEntry = PREFIX_ERROR + message;
		if (!logEntry.equals(lastLogEntry)) {
			System.err.println(logEntry);
			lastLogEntry = logEntry;
		}
	}

	/**
	 * Log an internal error.
	 *
	 * @param exception
	 *            Exception to log
	 */
	public static void error(final Throwable exception) {
		String message = exception.getMessage();
		if (message == null || message.length() == 0) {
			error(exception.getClass().getName());
		} else {
			error(message + " (" + exception.getClass().getName() + ")");
		}
	}

	/**
	 * Log an internal error.
	 *
	 * @param exception
	 *            Exception to log
	 * @param message
	 *            Text to log
	 */
	public static void error(final Throwable exception, final String message) {
		String messageOfThrowable = exception.getMessage();
		if (messageOfThrowable == null || messageOfThrowable.length() == 0) {
			error(message + " (" + exception.getClass().getName() + ")");
		} else {
			error(message + " (" + exception.getClass().getName() + ": " + messageOfThrowable + ")");
		}
	}
}
