package com.oxande.wavecleaner.util;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.SwingUtilities;

import com.oxande.wavecleaner.audio.AudioChangedListener;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

public class ListenerManager<T extends EventListener> {

	private List<ListenerInfo<T>> listenerInfos = new ArrayList<>();
	
	/**
	 * We keep information on the listener
	 *
	 *
	 */
	private static class ListenerInfo<T extends EventListener>  {
		
		AtomicInteger calls = new AtomicInteger(0);
		T listener;
		int skipped = 0;
		
		public ListenerInfo(T listener){
			this.listener = listener;
		}
		
		
		/**
		 * We invoke the listener if possible.
		 * 
		 * @param val the value for invocation.
		 */
		public void invoke(Consumer<T> fnct){
			if( calls.getAndIncrement() < 1 ){
				// We can run the code in the SWING thread...
				// the value is now "1" (means: waiting)
				// we boost once  
				calls.incrementAndGet();
				SwingUtilities.invokeLater( () -> {
					fnct.accept(this.listener);
					calls.decrementAndGet();
				});
			}
			else {
				// for debugging purposes
				skipped++;
			}
			calls.decrementAndGet();
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
