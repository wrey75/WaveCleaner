package com.oxande.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;

@SuppressWarnings("serial")
public class JFlashLabel extends JLabel implements ActionListener {
	private static Logger LOG = LogFactory.getLog(JFlashLabel.class);
	
	long flashTime = 0;
	
	public JFlashLabel(){
		super();
		startTimer();
	}
		
	public JFlashLabel(String label){
		super(label);
		startTimer(); 
	}
	
	private void startTimer(){
		Timer timer = new Timer(100, this);
		timer.setInitialDelay(100);
		timer.start();
		this.setOpaque(true);
	}
	
	public void fireFlash() {
		this.flashTime = System.currentTimeMillis();
		if( SwingUtilities.isEventDispatchThread() ){
			actionPerformed(null);
		}
	}
	
	public Dimension getMinimumSize(){
		return new Dimension(10, 10);
	}

	public Dimension getPreferredSize(){
		return super.getPreferredSize();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		long value = (e == null ? 1000 : 1000 - Math.min(1000, System.currentTimeMillis() - flashTime));
		Color color = new Color((int)(value * 255.0 / 1000.0), 0, 0);
		this.setBackground(color);
	}
}
