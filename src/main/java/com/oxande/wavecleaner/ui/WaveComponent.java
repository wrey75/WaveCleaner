package com.oxande.wavecleaner.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.RMSSample;
import com.oxande.wavecleaner.audio.AudioDocument;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.analysis.FFT;

/**
 * The component which is in charge of the display.
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class WaveComponent extends JComponent {
	static public final int AUTO_MODE = 0;
	static public final int WAVE_MODE = 1;
	static public final int CHUNK_MODE = 2;
	static public final int FFT_MODE = 3;
	
	private static Logger LOG = LogFactory.getLog(WaveComponent.class);
	private AudioDocument audio = null;
	
	/** mousePosition in samples */
	public int mousePosition = -1;

	/** player head in samples */
	public int playHead = -1;
	
	public int firstVisibleSample;
	public int lastVisibleSample;
	public int numberOfSamples;
	public int mode = AUTO_MODE;

	// Colors to be used. Note alpha must NOT be set
	// for Windows (because the platform is too lengthly
	// for repainting).
	private Color rightColor = new Color(26, 188, 156);
	private Color leftColor = new Color(46, 204, 113);
	private Color peakColor = new Color(52, 152, 219).brighter();
	private Color rmsColor = new Color(41, 128, 185);
	private Color backgroundColor = new Color(44, 62, 80).darker();
	
	private List<RegionSelected> regions = new ArrayList<RegionSelected>();
	
	public WaveComponent(){
		this.audio = null;
		this.setBackground( backgroundColor );
		this.setMinimumSize(new Dimension(50, 100));
		this.setPreferredSize(new Dimension(100, 200));
	}
	
	/**
	 * Clear the current selection.
	 * 
	 */
	public synchronized void addSelection(RegionSelected region){
		this.removeSelection(region.name); // avoid duplicates!
		this.regions.add(region);
		this.repaint();
	}
	
	/**
	 * Remove a region based on its name.
	 * 
	 * @param name name of the region (case-insensitive).
	 */
	public void removeSelection(String name){
		synchronized( regions ){
			for(int i = 0; i < regions.size(); i++ ){
				RegionSelected r = regions.get(i);
				if( r.name.equalsIgnoreCase(name) ){
					regions.remove(i);
					repaint();
				}
			}
		}
	}
	
	
	public void setAudioDocument(AudioDocument doc){
		this.audio = doc;
		this.numberOfSamples = audio.getNumberOfSamples();
		this.firstVisibleSample = 0;
		this.lastVisibleSample = this.numberOfSamples;
	}
	
	/**
	 * Set the visible part of the wave.
	 * 
	 * @param first first sample
	 * @param last last sample
	 */
	public void setVisibleWindow(int first, int last){
		if( first < 0 || first >= last ){
			throw new IllegalArgumentException("'first' must be positive.");
		}
		this.firstVisibleSample = first;
		this.lastVisibleSample = last;
	}
	
	private void setStroke(Graphics g, double width) {
		if (g instanceof Graphics2D) {
			Stroke stroke = new BasicStroke((float) width);
			((Graphics2D) g).setStroke(stroke);
		}
	}
	
	private static Color[] colors = {
			new Color(120,  66, 18  ),
			new Color(156, 100, 12  ),
			new Color(185, 119, 14  ),
			new Color(214, 137, 16  ),
			new Color(243, 156, 18  ),
			new Color(245, 176, 65  ),
			new Color(248, 196, 113 ),
			new Color(250, 215, 160 ),
			new Color(253, 235, 208 ),
			new Color(254, 245, 231 ),
	}; 
	
	private int calculateX(int k, double width){
		double logFreq = Math.log(k+1);
		int x = (int)((logFreq - 0.6) / 5.6 * width);
		return x;
	}
	
	private void drawFFT(Graphics g, int first, int last){
		int chunkSize = audio.getChunkSize();
		FFT fft = new FFT(chunkSize, audio.getSampleRate());
		int fftSize = fft.specSize(); // The size of the buffer diveded by 2!
		Rectangle rect = g.getClipBounds();
		// double factor = rect.getWidth() / fftSize;

		int firstChunk = first / chunkSize;
		int y_middle = (int)(rect.height / 2.0);
		// int lastChunk = (last / chunkSize) + 1;
		setStroke(g, 1.0);
		for( int j = 0; j < 2; j++){
			float h = (float) ((rect.height * 0.01) * (j == 0 ? -1 : +1));
//			int x_old = -100;
//			int y_old = y_middle;
			// g.setColor(j == 0 ? leftColor : rightColor);
			
			// g.setFillColor(j == 0 ? leftColor : rightColor);
			g.setColor( colors[ 5 ]);
			g.setColor(new Color(187, 143, 206));
			for(int i = firstChunk; i < firstChunk + 1; i++){
				float[][] samples = audio.getAudioSamples(i);
 				fft.forward(samples[j]);
				for(int k = 0; k < fftSize; k++){
					// float freq = fft.indexToFreq(k);
//					double logFreq = Math.log(k+1);
//					int x = (int)((logFreq - 1.0) / 5.0 * rect.getWidth());
					int x = calculateX(k, rect.getWidth());
					int y = (int)(fft.getBand(k) * h ) + y_middle;
					
					// double lineWidth = Math.max((x - x_old) / 1, 1);
					// g.drawLine(x_old, y_old, x, y);
					// g.setColor( colors[ Math.min(colors.length - 1, (int)(fft.getBand(k) / 5.0) )]);
					for(int w = 0 ; w <= (k > 8 ? 1 : (10 - k)); w++){
						g.drawLine(x + w, y_middle, x + w, y);
					}
					// g.drawRect(x_old, (j == 0 ? y_middle : y_middle - (int)(fft.getBand(k) * h )), x - x_old - 2, (j == 0 ? (int)(fft.getBand(k) * h ) : y_middle) );
//					x_old = x;
//					y_old = y;
				}
			}
		}
		
		if( rect.getWidth() > 250){
			AffineTransform at = new AffineTransform();
	        AffineTransform saveAT = ((Graphics2D)g).getTransform();
			int bands[] = new int[] { 30, 60, 125, 250, 500, 1000, 2000, 4000, 8000, 16000 };
			for(int i = 0; i < bands.length; i++){
				g.setColor(Color.GRAY);
				int k = fft.freqToIndex((float)bands[i]);
				int x = calculateX(k, rect.getWidth());
				g.drawLine(x, y_middle, x, 0);
				
				g.setColor(Color.GRAY.brighter());
				String str = (bands[i] < 1000 ? bands[i] + "Hz" : (bands[i] / 1000) + "KHz");
				at.setToRotation(Math.toRadians(90), x, 0);
				((Graphics2D)g).setTransform(at);
				g.drawString(str, x + 15, -10);
				((Graphics2D)g).setTransform(saveAT);
			}
		}
	}

	private void drawWave(Graphics g, int first, int last){
		int chunkSize = audio.getChunkSize();
		Rectangle rect = g.getClipBounds();
		double factor = rect.getWidth() / (last - first);

		float h = (rect.height / 2) - 10;
		int firstChunk = first / chunkSize;
		int lastChunk = (last / chunkSize) + 1;
		
		setStroke(g, (factor > 0.15 ? 2.0 : 1.0));
		// LOG.debug("FACTOR = {}",  factor);
		for( int j = 0; j < 2; j++){
			int y_middle = (int)(rect.height / 2 + (rect.height * 0.15) * (j == 0 ? -1.0 : +1.0));
			g.setColor(j == 0 ? leftColor : rightColor);
			if( factor < 0.02 ){
				LOG.debug("FACTOR = {}", factor);
				int x_prev = -100;
				int y = 0;
				for(int i = firstChunk; i < lastChunk; i++){
					long begin_x = (i * chunkSize) - first;
					float[][] samples = audio.getAudioSamples(i);
					for(int k = 0; k < samples[j].length; k++){
						int x = (int)((begin_x + k) * factor);
						if( x > x_prev ){
							g.drawLine((int)x, y_middle - y, (int)x, y_middle + y);
							y = 0; // Rest the value
							x_prev = x;
						}
						else {
							// Keep samples
							y = Math.max(y, Math.abs((int)(samples[j][k] * h)));
						}
					}
				}
			}
			else {
				double x_old = -100;
				int y_old = y_middle;
				for(int i = firstChunk; i < lastChunk; i++){
					long begin_x = (i * chunkSize) - first;
					float[][] samples = audio.getAudioSamples(i);
					for(int k = 0; k < samples[j].length; k++){
						double x = ((begin_x + k) * factor);
						int y = (int)(samples[j][k] * h);
						// Optimization: draw as less as possible 
						g.drawLine((int)x_old, y_old + y_middle, (int)x, y + y_middle);
						x_old = x;
						y_old = y;
					}
				}
			}
		}
	}
	
	/**
	 * Draw the levels for the sond.
	 * 
	 * @param g
	 *            the graphics where to render the wave
	 * @param samples
	 *            the samples of the song.
	 */
	private void drawLevels(Graphics g, RMSSample[] samples) {
		Rectangle rect = g.getClipBounds();
		int y = rect.height / 2;
		float h = rect.height / 2 - 10;

		int ratio = audio.getChunkSize();
		// float scrollPos = getScrollPosition() / audio.getSampleSize();

		double strokeWidth = 1.0; // rect.width * zoom / samples.length;
		setStroke(g, strokeWidth);

		// double zoom = (double)audio.getNumberOfSamples() / getExtent();
		int first = this.firstVisibleSample / ratio; // first visible
		int last = this.lastVisibleSample / ratio; // last visible
		if (last >= samples.length) {
			LOG.warn("Last is {} but a maximum of {} was expected. Fixing the issue.", last, samples.length);
			last = samples.length - 1;
			if (this.lastVisibleSample > numberOfSamples) {
				LOG.error("BAD LAST VISIBLE: {} but maximum is {}", lastVisibleSample, numberOfSamples);
			}
		}
		double width = (0.0 + rect.width) / (double) (last - first);
		for (int i = first; i < last; i++) {
			RMSSample s = samples[i];
			if (s != null) {
				int x = (int) ((i - first) * width);
				g.setColor(peakColor);
				g.drawLine(x, (int) (y - s.peakL * h), x, (int) (y - s.levelL * h));
				g.setColor(rmsColor);
				g.drawLine(x, (int) (y - s.levelL * h), x, (int) (y + s.levelR * h));
				g.setColor(peakColor);
				g.drawLine(x, (int) (y + s.levelR * h), x, (int) (y + s.peakR * h));
			}
		}
	}

	@Override
	public void paintComponent(Graphics g0) {
		Graphics2D g = ((Graphics2D) g0);
		super.paintComponent(g);
		
		int height = getHeight();
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (audio != null) {
			RMSSample[] samples = audio.getLevels();
	
			if (samples != null) {
				switch( mode ){
					case WAVE_MODE:
						drawWave(g, firstVisibleSample, lastVisibleSample);
						break;
						
					case CHUNK_MODE:
						drawWave(g, firstVisibleSample, lastVisibleSample);
						break;

					case FFT_MODE:
						drawFFT(g, firstVisibleSample, lastVisibleSample);
						break;
						
					default :
						if( getWidth() * audio.getChunkSize() < (lastVisibleSample - firstVisibleSample) ){
							drawLevels(g, samples);
						}
						else {
							drawWave(g, firstVisibleSample, lastVisibleSample);
						}
						break;
				}


//				g.setColor(Color.WHITE);
//				g.drawString("Number of samples: " + samples.length, 10, 20);
//				g.drawString("Farme rate: " + audio.getFormat().getFrameRate(), 10, 40);
			}
		
			synchronized(regions){
				for( RegionSelected r : regions ){
					if( r.active ){
						int x1 = this.sampleToX(r.begin);
						int x2 = this.sampleToX(r.end);
						Color c = new Color( r.color.getRed(), r.color.getGreen(), r.color.getBlue(), 150 ); 
						g.setColor(c);
						g.fillRect(Math.min(x1, x2), 0, Math.abs(x2 - x1), getHeight());
					}
				}
			}
		}

		setStroke(g, 1.0);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(sampleToX(mousePosition), 0, sampleToX(mousePosition), height);

		g.setColor(Color.GREEN.brighter());
		g.drawLine(sampleToX(playHead), 0, sampleToX(playHead), height);
		

	}

	/**
	 * Return the position x in the visible window. Returns -1 in case the
	 * position is outside.
	 * 
	 * @param pos
	 *            the sample
	 * @return the horizontal position in pixels.
	 */
	protected int sampleToX(int pos) {
		int winWidth = this.getWidth();
		if (pos < firstVisibleSample || winWidth < 1) {
			return -1;
		}
		if (pos > lastVisibleSample) {
			return winWidth + 1;
		}
		int visiblePos = pos - firstVisibleSample;
		double ratio = winWidth / (double) (lastVisibleSample - firstVisibleSample);
		int x = (int) (visiblePos * ratio);
		// LOG.debug("sampleToX( {} ) = {}", pos, x);
		return x;
	}
}