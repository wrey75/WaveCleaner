package com.oxande.wavecleaner.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import com.oxande.wavecleaner.audio.AudioDocument;

/**
 * A component for displaying information
 * @author wrey
 *
 */
@SuppressWarnings("serial")
public class AudioInfoComponent extends JComponent {
	
	private AudioDocument audio;
	
	public void setAudioDocument( AudioDocument doc ){
		this.audio = doc;
		this.repaint();
	}
	
	private int line = 0;
	
	private void println(Graphics g, String label, Object value){
		line += 12;
		if( value != null ){
			
			g.drawString(label + ": " + value.toString(), 10, line );
			
		}
	}
	
	@Override
	public void paintComponent(Graphics g0){
		Graphics2D g = (Graphics2D)g0;
		if( this.audio != null ){
			this.line = 0;
			println(g, "Sample rate", "" + audio.getSampleRate());
			int duration = (int)(audio.getNumberOfSamples() / audio.getSampleRate() / 60.0);
			println(g, "Duration:", duration + "min." );
		}
			
	}
}
