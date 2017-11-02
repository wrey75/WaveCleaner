package com.oxande.wavecleaner.util;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;


public class ListenerManager<T extends EventListener> {
	private static Logger LOG = LogFactory.getLog(ListenerManager.class);
	private List<ListenerInfo<T>> listenerInfos = new ArrayList<>();
	
	/**
	 * We keep information on the listener
	 *
	 *
	 */
	private static class ListenerInfo<T extends EventListener>  {
		
		AtomicInteger mutex = new AtomicInteger(0);
		T listener;
		int skipped = 0;
		int calls = 0;
		
		public ListenerInfo(T listener){
			this.listener = listener;
		}
		
		
		/**
		 * We invoke the listener but only if a call is not already
		 * in the queue. This avoid multiple calls. Note only the
		 * first call is, in this case, used. The other calls are
		 * dimissed.
		 * 
		 * @param val the value for invocation.
		 */
		public void invoke(Consumer<T> fnct){
			calls++;
			if( mutex.getAndIncrement() < 1 ){
				// We can run the code in the SWING thread...
				// the value is now "1" (means: waiting)
				// we boost once  
				mutex.incrementAndGet();
				SwingUtilities.invokeLater( () -> {
					// LOG.debug("invoked.");
					fnct.accept(this.listener);
					mutex.decrementAndGet();
				});
			}
			else {
				// for debugging purposes
				skipped++;
				if( skipped % 1000 == 0 ){
					int ratio = (int)(100.0 * (calls - skipped) / calls);
					LOG.debug("Listener {} called {} times ({}%).", this.listener.getClass().getSimpleName(), (calls - skipped), ratio);
				}
			}
			mutex.decrementAndGet();
		}
		
		T getListener(){
			return listener;
		}
	}
	
	/**
	 * Very basic constructor
	 */
	public ListenerManager() {
	}
	
	
	/**
	 * Notify all the listeners. We use a {@link Function}
	 * to keep the code simple. Note the listener  
	 * 
	 */
	public void publishOnce(Consumer<T> function){
		for(ListenerInfo<T> infos : this.listenerInfos){
			infos.invoke(function);
		}
	}
	
	/**
	 * Force to publish.
	 * 
	 * @param function the code to call.
	 */
	public void publish(final Consumer<T> function){
		for(ListenerInfo<T> infos : this.listenerInfos){
			SwingUtilities.invokeLater( () -> {
				function.accept(infos.getListener());
			});
		}
	}

	public void add(T listener ) {
		synchronized(this.listenerInfos){
			ListenerInfo<T> infos = new ListenerInfo<T>(listener);
			this.listenerInfos.add(infos);
		}
	}
	
	public void remove(T listener ) {
		synchronized(this.listenerInfos){
			this.listenerInfos.remove(listener);
		}
	}

}
