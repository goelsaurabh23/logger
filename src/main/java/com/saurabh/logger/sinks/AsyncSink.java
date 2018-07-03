package com.saurabh.logger.sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.saurabh.logger.GlobalConstants;
import com.saurabh.logger.ILoggingEvent;
import com.saurabh.logger.InternalLog;

/**
 * AsyncSink log events asynchronously while wrapping the actual sink. In order to avoid loss of logging events, this
 * sink should be closed accurately. It is the user's  responsibility to close this sink when work is done.
 * <p>
 * This sink buffers events in a {@link BlockingQueue}. {@link Worker} threads created by this sink takes
 * events from the head of the queue, and dispatches them to the wrapped sink object.
 * <p>
 * 
 * @author Saurabh
 */
public class AsyncSink implements Sink {

    private String name;
    
    /**
     * The default buffer size.
     */
    public static final int DEFAULT_QUEUE_SIZE = 256;
    int queueSize = DEFAULT_QUEUE_SIZE;

    //Sink getting wrapped
    private Sink wrappedSink;
    private boolean started = false;
    private BlockingQueue<ILoggingEvent> blockingQueue;

    /**
     * The default maximum queue flush time allowed during appender stop. If the 
     * worker takes longer than this time it will exit, discarding any remaining 
     * items in the queue
     */
    public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
    int maxFlushTime = DEFAULT_MAX_FLUSH_TIME;
    
    List<Worker> workers = new ArrayList<AsyncSink.Worker>();    
    private int workerThreads = 1;
    
    public boolean isStarted() {
    	return started;
    }
    
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }
    
    public void setWrappedSink(Sink sink) {
    	this.wrappedSink = sink;
    }
    
    public void setThreadModel(String threadModel) {
    	if ( threadModel.equalsIgnoreCase(GlobalConstants.THREAD_MODEL_MULTI ) ) {
    		workerThreads = GlobalConstants.DEFAULT_ASYNC_WORKERS;
    	} else {
    		workerThreads = 1;
    	}
    }

    public Sink getWrappedSink() {
    	return wrappedSink;
    }
    
    public int getWorkerThreadsCount() {
    	return workerThreads;
    }
    
	public void init() {
		// TODO Auto-generated method stub
        if (queueSize < 1) {
        	InternalLog.warn("Invalid queue size :" + queueSize);
            return;
        }
        blockingQueue = new ArrayBlockingQueue<ILoggingEvent>(queueSize);
        
        //Initialize the wrapped sink
        wrappedSink.init();
        
        //Update the started flag
        started = true;
        
        //Start the worker threads
        startWorkers();
        
        
	}
	
	public void startWorkers() {
		for ( int i=0; i < workerThreads; i++ ) {
			Worker worker = new Worker(i);
			worker.setDaemon(true);
			worker.setName(getName() + " -Worker-" + i);
			worker.start();
			workers.add(worker);
		}
	}

	public void write(ILoggingEvent loggingEvent) {
		// TODO Auto-generated method stub
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    blockingQueue.put(loggingEvent);
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }

	}

	public void flush() {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		started = false;

		interruptWorkers();
	}
	
	private void interruptWorkers() {
		for ( Worker worker : workers ) {
			worker.interrupt();
			if ( worker.getWorkerOrder() != 0 ) {
				continue;
			}
			try {
	            worker.join(maxFlushTime);

	            // check to see if the thread ended and if not add a warning message
	            if (worker.isAlive()) {
	            	InternalLog.warn("Max queue flush timeout (" + maxFlushTime + " ms) exceeded. Approximately " + blockingQueue.size()
	                                + " queued events will be discarded.");
	            } else {
	            	InternalLog.warn("Queue flush finished successfully within timeout");
	            }
			} catch (InterruptedException e) {
	            int remaining = blockingQueue.size();
	            InternalLog.warn("Failed to join worker thread. " + remaining + " queued events may be discarded.");
	        }
		}
	}

    public class Worker extends Thread {
    	int order;
    	
    	public Worker(int order) {
    		this.order = order;
    	}
    	
    	public int getWorkerOrder() {
    		return order;
    	}
    	
        public void run() {
            AsyncSink parent = AsyncSink.this;
            Sink sink = parent.wrappedSink;

            // loop while the parent is started
            while ( parent.isStarted() ) {
                try {
                    ILoggingEvent loggingEvent = parent.blockingQueue.take();
                    sink.write(loggingEvent);
                } catch (InterruptedException ie) {
                    break;
                } catch (Exception e) {
				}
            }

            //Worker thread with lowest order will flush remaining events before exiting
            if ( order == 0 ) {
                for ( ILoggingEvent loggingEvent : parent.blockingQueue) {
                    sink.write(loggingEvent);
                    parent.blockingQueue.remove(loggingEvent);
                }
                //Close the current sink 
                sink.close();
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null) return false;
    	if (this == obj) return true;
    	if (getClass() != obj.getClass()) { 
    		return false;
    	}
    	AsyncSink asyncSink = (AsyncSink)obj;
    	return this.wrappedSink.equals(asyncSink.wrappedSink);
    }
}
