package com.oxande.swing;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
@Slf4j
public class JFlashLabel extends JLabel implements ActionListener {
	long flashTime = 0;
	private int flashDuration = 750; // in milliseconds
	private int fullPower = 5; // keep in full power
	
	public JFlashLabel(){
		this(' ');
	}
		
	public JFlashLabel(char c){
		super(" " + c + " ");
		initComponent();
	}
	
	public void setFlashDuration(int duration){
		this.flashDuration = duration;
	}
	
	public int getFlashDuration(){
		return this.flashDuration;
	}
	
	private synchronized void initComponent(){
		Timer timer = new Timer(50, this);
		timer.setInitialDelay(100);
		timer.start();

		setFont(new Font("monospaced", Font.PLAIN, 12));
		setForeground(Color.LIGHT_GRAY);
		this.setOpaque(true);
	}
	
	public void fireFlash() {
		this.flashTime = System.currentTimeMillis() + fullPower;
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
		Color color = Color.BLACK;
		long sinceFlash = Math.max(0, System.currentTimeMillis() - flashTime);
		if( sinceFlash < flashDuration ){
			int intensity = (int)((flashDuration - sinceFlash) * 255.0 / flashDuration );
			//LOG.info("intensity = {}, since = {}", intensity, sinceFlash);
			color = new Color(intensity, 0, 0);	
		} 
		this.setBackground(color);
	}
}
