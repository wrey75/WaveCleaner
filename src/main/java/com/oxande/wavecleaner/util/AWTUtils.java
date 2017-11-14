package com.oxande.wavecleaner.util;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.oxande.wavecleaner.WaveCleaner;

public class AWTUtils {
	
	/**
	 * Execute some code outside the AWT thread. If the current thread is the
	 * AWT one, we create a Thread and we run run the code immediately. If we are
	 * not in a AWT thread, we execute the code inside the current thread.
	 * 
	 * <p>This code does NOT wait the end of the processing, you must put ALL the
	 * processing in it.</p>
	 * 
	 * @param r
	 *            the {@link Runnable} code.
	 * @return true if executed without interruption.
	 */
	public static final void runFreely(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			Thread t = new Thread(r);
			t.start();
		} else {
			// Just run the code in the current thread.
			r.run();
		}
	}
	
	public static final void showMessage(String message){
		WaveCleaner app = WaveCleaner.getApplication();
		Component frame = app.getMainScreen();
		JOptionPane.showMessageDialog(frame, message, "Information",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	public static final void showErrorMessage(String message){
		WaveCleaner app = WaveCleaner.getApplication();
		Component frame = app.getMainScreen();
		JOptionPane.showMessageDialog(frame, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
}
