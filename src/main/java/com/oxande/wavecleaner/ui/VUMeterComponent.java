package com.oxande.wavecleaner.ui;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * A component for displaying information about the RMS and peak levels
 *  
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class VUMeterComponent extends JComponent {

	int samples = 0;
	float maxPeakLeft = 0;
	float maxPeakRight = 0;
	float peakLeft = 0;
	float peakRight = 0;
	float rmsRight = 0;
	float rmsLeft = 0;
	
	public synchronized void reset(){
		maxPeakLeft = 0;
		maxPeakRight = 0;
		peakLeft = 0;
		peakRight = 0;
		rmsRight = 0;
		rmsLeft = 0;
		samples = 0;
	}

	public synchronized void push(float left, float right){
		samples++;
		
		if( left > peakLeft ){
			peakLeft = left;
			if( peakLeft > maxPeakLeft ){
				maxPeakLeft = peakLeft;
			}
		}
		if( right > peakRight ){
			peakRight = right;
			if( peakRight > maxPeakRight ){
				maxPeakRight = peakRight;
			}
		}

		rmsRight += left;
		rmsLeft += right;
	}

	@Override
	public void paintComponent(Graphics g0){
//		Graphics2D g = (Graphics2D)g0;
//		if( this.audio != null ){
//			this.line = 0;
//			println(g, "Sample rate", "" + audio.getSampleRate());
//			int duration = (int)(audio.getNumberOfSamples() / audio.getSampleRate() / 60.0);
//			println(g, "Duration:", duration + "min." );
//		}
			
	}
}
