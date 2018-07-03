package com.saurabh.logger.sinks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation provides meta info for different {@link Sink} 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SinkType {

	/**
	 * Sink type value.
	 *
	 * @return Name of the sink type
	 */
	String type();
}
