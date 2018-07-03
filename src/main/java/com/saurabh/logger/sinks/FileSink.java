package com.saurabh.logger.sinks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;

import com.saurabh.logger.ILoggingEvent;
import com.saurabh.logger.InternalLog;

/**
 * FileSink logs message to a file
 * @author Saurabh
 */
@SinkType(type = "file")
public class FileSink implements Sink {

    public static final int DEFAULT_BUFFER_SIZE = 64* 1024;
    public static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name(); 
    public static final String NEWLINE_CHARACTER = "\n";

    /**
     * Append to or truncate the file? The default value for this variable is
     * <code>true</code>, meaning that by default a <code>FileSink</code> will
     * append to an existing file and not truncate it.
     */
    private boolean append = true;
	private boolean buffered = false;
	private OutputStream stream;
    private String fileName = null;
    private String name;
    private boolean started;	//Will be used for lazy initialization

    /**
     * All synchronization in this class is done via the lock object.
     */
    protected final ReentrantLock lock = new ReentrantLock(false);

    /**
     * Returns the value of the <b>File</b> property.
     */
    public String getFile() {
        return fileName;
    }
    
    /**
     * The <b>File</b> property takes a string value which should be the name of
     * the file to append to.
     */
    @MethodParam(name="file_location", type=String.class)
    public void setFile(String file) {
        if (file == null) {
            fileName = file;
        } else {
            // Trim spaces from both ends. The users probably does not want
            // trailing spaces in file names.
            fileName = file.trim();
        }
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    /**
     * Returns the value of the <b>Append</b> property.
     */
    public boolean isAppending() {
        return append;
    }

    /**
     * Returns currently opened stream
     * @return
     */
    public OutputStream getOutputStream() {
    	return stream;
    }
    /**
     * Returns the value of the <b>Append</b> property.
     */
    public boolean isBuffered() {
        return buffered;
    }
    
    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
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
		if ( fileName != null ) {
	        lock.lock();
	        try {
	            File file = new File(fileName);
	            File parent = file.getParentFile();
	            if (parent != null) {
	                // File.mkdirs() creates the parent directories only if they don't
	                // already exist; and it's okay if they do.
	                parent.mkdirs();
	            }
	            try {
		    		if (buffered) {
		    			stream = new BufferedOutputStream(new FileOutputStream(file, append), DEFAULT_BUFFER_SIZE);
		    		} else {
		    			stream = new FileOutputStream(file, append);
		    		}
	            } catch ( Exception ex) {
	            	InternalLog.error("Exception while opening output stream for file : " + file);
	            }
	            started = true;
	        } finally {
	            lock.unlock();
	        }

		}
	}

	public void write(ILoggingEvent loggingEvent) {
		// TODO Auto-generated method stub
		if (stream == null) {
	        InternalLog.error("OutputStream is not opened, won't be able to write");
	        return;
	    }
		byte[] byteArray = null;
		try {
	        byteArray = loggingEvent.getFormattedMessage().getBytes(DEFAULT_CHARSET);
		} catch ( UnsupportedEncodingException ex) {
			InternalLog.error(ex, "Exception while generating byte array for message :" + loggingEvent);
		}
        if ( byteArray == null || byteArray.length == 0 )
            return;
        
        lock.lock();
        try {
            this.stream.write(byteArray);
            stream.write(NEWLINE_CHARACTER.getBytes());
        } catch (Exception ex) {
			InternalLog.error(ex, "Exception while writing byte array for message :" + loggingEvent);
        } finally {
            lock.unlock();
        }
	}

	public void flush() {
        lock.lock();
        try {
            this.stream.flush();
        } catch (Exception ex) {
			InternalLog.error(ex, "Exception while flusing outputStream");
        } finally {
            lock.unlock();
        }
	}

	public void close() {
		// TODO Auto-generated method stub
        lock.lock();
        try {
            if (this.stream != null) {
                this.stream.close();
                this.stream = null;
            }
        } catch (Exception ex) {
			InternalLog.error(ex, "Exception while closing outputStream");
        } finally {
            lock.unlock();
            started = false;
        }
	}

	public boolean isStarted() {
		return started;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if ( getClass() != obj.getClass()) { 
			return false;
	    }
	    FileSink fileSink = (FileSink)obj;
	    return getFile().equals(fileSink.getFile());
	}
}
