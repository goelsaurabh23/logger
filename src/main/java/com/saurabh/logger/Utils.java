package com.saurabh.logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import com.saurabh.logger.sinks.SinkType;

/**
 * Basic utility class for helper methods
 * @author Saurabh
 */
public class Utils {
	
    private static Class<?>[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<?> classes = new ArrayList();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return (Class[]) classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

	/**
	 * Helper method to find default library sink implementation for the passed sinkType field
	 * @param sinkType
	 * @return
	 * 		Fully qualified name for the sink based on the passed sink type
	 */
	public static String getSinkImplementation(String sinkType) {
		Class<?>[] classes;
		try {
			classes = getClasses(GlobalConstants.SINKS_PACKAGE);
			for ( Class<?> clas : classes ) {
				if ( clas.isAnnotationPresent(SinkType.class) ) {
	  				SinkType sinkTypeAnno = (SinkType)clas.getAnnotation(SinkType.class);
	  				if ( sinkTypeAnno.type().equalsIgnoreCase(sinkType) ) {
	  					return clas.getName();
	  				}
				}
			}
		} catch (Exception e) {
			InternalLog.error(e, "Exception while getting sinks pre defined implementations");
		}
		return null;
	}

	public static String formatTimeStamp(long timeStamp, String tsFormat) {
    	DateFormat formatter = new SimpleDateFormat(tsFormat);
		//Reduce IST -5.30 UTC difference
		Date date = new Date((timeStamp));
		return formatter.format(date);
	}
}
