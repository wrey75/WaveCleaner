package com.oxande.wavecleaner.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;

/**
 * A component for displaying information about the RMS and peak levels. The
 * VUMeter stores its status which is not the way to do, but this should be
 * sufficient.
 * 
 * You have to set the sample rate and push each sample. The analysis is as
 * fast as possible. Mainly used by the preamplifier with the output including
 * the gain (which can explain some saturation). Only peaks level are displayed.
 * By construction, the level is calculated for the last 20 milliseconds. When the
 * level is higher than the peak, the peak is stored for 2 seconds. Thoses values could
 * be changed (see code for reference).
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class VUMeterComponent extends JComponent {
	private static Logger LOG = LogFactory.getLog(VUMeterComponent.class);
	
	private static final int NB_CHANNELS = 2; // STEREO
	
	public static final int HORIZONAL = 1;
	public static final int VERTICAL = 2;
	public static final int AUTO = 3;
	
	public static final float LEVEL_6DB = 0.70794576f; // -6dB

	
	int border = 3;
	boolean blocks;
	int orientation = AUTO;
	int bufferSize = 1024;
	int buffCount = 0;
	float sampleRate = 48000;
	
	private int mode = 0;
	private int height;
	private int width;
	private Graphics2D g;
	private ChannelData data[];
	
	public VUMeterComponent(){
		super();
		this.setVisible(true);
		this.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.data = new ChannelData[2];
		for(int ch = 0; ch < NB_CHANNELS; ch++){
			this.data[ch] = new ChannelData();
		}
	}

	private static class ChannelData {
		public static final float DELAY_PEAK = 5.0f;
//		public boolean over = false;
		public float peak = 0;
		public float rms = 0;
		public float buffer = 0;
		public int peakDelay = 0;
		
		public void reset() {
//			over = false;
			peak = 0.0f;
			rms = 0.0f;
			buffer = 0.0f;
			peakDelay = 0;
//			noPeak = 0;
		}
	}
	
	public synchronized void reset() {
		for(int ch = 0; ch < NB_CHANNELS; ch++){
			this.data[ch].reset();
		}
		buffCount = 0;
	}

	/**
	 * Set the sample rate.
	 * 
	 * @param sampleRate
	 */
	public void setSampleRate(float sampleRate){
		this.bufferSize = (int)(sampleRate / 20.0);
		this.sampleRate = sampleRate;
	}

	public synchronized void push(float samples[]) {
		boolean mustResetCounter = false;
		buffCount++;
		for(int ch = 0; ch < samples.length; ch++){
			ChannelData channel = this.data[ch];
			float level = Math.abs(samples[ch]);
			if (level > channel.peak) {
				channel.peak = level;
//				channel.noPeak = 0;
				channel.peakDelay = (int)(channel.DELAY_PEAK * this.sampleRate);
//				if (channel.peak > 0.99f) {
//					channel.over = true;
//				}
			}
			channel.buffer = Math.max(channel.buffer, level);
			if( buffCount >= bufferSize ){
				// LOG.debug("RMS = {} => {}", channel.rms, channel.peak);
				channel.rms = channel.buffer; // (float) channel.buffer / (float)bufferSize;
				channel.buffer = 0.0f;
				mustResetCounter = true;
				if( channel.peakDelay > 0 ){
					channel.peakDelay -= bufferSize;
				}
				else {
					channel.peak = channel.rms;
//					channel.noPeak++;
//					channel.peak -= (channel.noPeak * channel.noPeak * 0.2) / sampleRate; // 200.0 / sampleRate;
				}
			}
		}
		
		if( mustResetCounter ) buffCount = 0;
	}

	public static float dB2volt(float dBvalue) {
		return (float) Math.pow(10.0, (0.05 * dBvalue));
	}

	public static float volt2db(float volt) {
		return (float) (20.0 * Math.log10(volt));
	}
	
	private void drawRectangle( int channel, Color color, double start, double end ){
		int x0, x1, y0, y1;
		int size;

		if( mode == HORIZONAL ){
			size = height / 2;
			y0 = (channel * size) + border;
			y1 = (channel + 1) * size - border;
			x0 = (int)(start * width) + border;
			x1 = (int)(end * width) + border;
			if( Math.abs(x1 - x0) < 1 ) x1 = x0 + 7;
		}
		else {
			size = width / 2;
			x0 = (channel * size) + border;
			x1 = (channel + 1) * size - border;
			y1 = height - (int)(start * height);
			y0 = height - (int)(end * height);
			if( Math.abs(y1 - y0) < 1 ) y1 = y0 + 7;
		}
		g.setColor(color);
		g.fillRect(x0, y0, x1 - x0, y1 - y0);
	}
	
//	private void drawLeft( Color color, double start, double end ){
//		drawRectangle(0, color, start, end);
//	}
//
//	private void drawRight( Color color, double start, double end ){
//		drawRectangle(1, color, start, end);
//	}
//	
//	
//	private void paintVersion1(Graphics2D g) {
//		mode = (orientation == AUTO ? (width > height ? HORIZONAL : VERTICAL) : orientation);
//		width = getWidth() - 10;
//		height = getHeight() - 10;
//		
//		for(int ch = 0; ch < NB_CHANNELS; ch++ ){
//			ChannelData channel = this.data[ch];
//			if( channel.rms > 0 ){
//				drawRectangle(ch, Color.YELLOW, -0.0, channel.rms);
//				drawRectangle(ch, Color.GREEN, -0.0, Math.min(channel.rms, LEVEL_6DB));
//			}
//			if( channel.peak > 0.0 ){
//				drawRectangle(ch, Color.ORANGE, channel.peak, channel.peak);
//			}
//			if( channel.over ) drawRectangle(ch, Color.RED, 1.0f, 1.0f);
//		}	
//	}
	
	private static class ColorRMS {
		public Color color;
		public float power;
		public ColorRMS(Color c, float rms){
			this.color = c;
			this.power = rms;
		}
	}
	
	static final private ColorRMS[] COLOR_VALUES = {
		new ColorRMS(Color.GREEN, 0.0f),
		new ColorRMS(Color.YELLOW, 0.5f),
		new ColorRMS(Color.ORANGE, 0.7f),
		new ColorRMS(Color.RED, 0.95f),
	};
	
	private void paintVersion2(Graphics2D g) {
		int MARGIN = 2;
		int w = getWidth() / NB_CHANNELS;
		
		for(int ch = 0; ch < NB_CHANNELS; ch++ ){
			ChannelData channel = this.data[ch];
			
			g.setColor(Color.DARK_GRAY);
			g.fillRect(MARGIN + ch * w, MARGIN, w - MARGIN*2, getHeight() - MARGIN * 2 );
			
			Color peakColor = null;
			for( int i = 0; i < COLOR_VALUES.length; i++ ){
				if( channel.rms > COLOR_VALUES[i].power ){
					g.setColor(COLOR_VALUES[i].color);
					int height = (int)((channel.rms - COLOR_VALUES[i].power) * getHeight());
					g.fillRect(MARGIN + ch * w, (int)(MARGIN + (1.0 - COLOR_VALUES[i].power) * getHeight()) - height, w - MARGIN*2, height );
				}
				if( channel.peak >= COLOR_VALUES[i].power ){
					peakColor = COLOR_VALUES[i].color;
				}
			}
			g.setColor( peakColor );
			g.fillRect(MARGIN + ch * w, (int)((1.0 - Math.min(1.0, channel.peak)) * getHeight()), w - MARGIN*2, MARGIN * 2 );

//			if( channel.over ) drawRectangle(ch, Color.RED, 1.0f, 1.0f);
		}	
		for( int db = 0; db > -90; db -= 3 ){
			double v = Math.pow(10.0, (0.05 * db));
			int y = (int)((1.0 - v) * getHeight()) + MARGIN;
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(MARGIN /* + ch * w */, y, getWidth() - MARGIN*2, y);
		}
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g);
		paintVersion2(g2);
	}
	
}
