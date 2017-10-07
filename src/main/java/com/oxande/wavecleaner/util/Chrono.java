package com.oxande.wavecleaner.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

public class Chrono {
	private static Log LOG = LogFactory.getLog(Chrono.class);
	private static AtomicInteger count = new AtomicInteger(0);
	
	private long started;
	private long stopped;
	private String name;
	
	Chrono(){
	}
	
	/**
	 * Starts a chronograph.
	 * 
	 * @return the chronosgraph started.
	 */
	public static Chrono go( Object obj ){
		Chrono chrono = new Chrono();
		chrono.setName( obj == null ? "count-" + count.incrementAndGet() : obj.toString() );
		chrono.start();
		return chrono;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void start(){
		started = System.currentTimeMillis();
	}
	
	/**
	 * Stop the chronometer is running.
	 * 
	 * @return 
	 */
	public long stop(){
		if( started > 0 && stopped == 0 ){
			this.stopped = System.currentTimeMillis();
		}
		return stopped - started;
	}
	
	public long getDuration(){
		return stopped - started;
	}
	
	public void logWork(String message, int tooLong){
		long duration = this.stop();
		if(duration > tooLong){
			LOG.warn(this.name + " executed in " + duration + " msec.");
		}
		else {
			LOG.info(this.name + " executed in " + duration + " msec.");
		}
	}
}
