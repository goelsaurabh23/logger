package com.saurabh.logger.sinks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for providing property based meta info for setter methods
 * Useful for loading classes dynamically
 * @author Saurabh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodParam {

	/**
	 * Name of the property as per configuration.
	 *
	 * @return Property name
	 */
	String name();
  
	/**
	 * Type of the property. Currently supporting
	 *
	 * <ul>
	 * <li><code>boolean.class</code></li>
	 * <li><code>int.class</code></li>
	 * <li><code>String.class</code></li>
	 * <li><code>String[].class</code></li>
	 * </ul>
	 * @return Property type
	 */
	Class<?> type();
}
