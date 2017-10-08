package com.oxande.wavecleaner.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.MouseInputListener;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.RMSSample;
import com.oxande.wavecleaner.util.logging.LogFactory;

/**
 * The waveform component is the central part of the software in terms of
 * graphics.
 * 
 * The curve is displayed in ONLY one bar: the upper part is the left channel
 * and the bottom is the right channel. I found it simpler and clearer than
 * having two separate graphics. As for Audacity, I kept the RMS level in a
 * color and the peaks in the same but brighter (I used some tables for the
 * colors).
 * 
 * <p>
 * The scrollbar is directly attached at the bottom of the window and always
 * visible (as far as I can). To zoom inside the sound, you can use the wheel of
 * the mouse. For Mac users (also having an horizontal wheel), I just used a
 * trick found on StackOverflow.
 * </p>
 * <p>
 * The main problem is to synchronize the scrollbar to the wave form.
 * </p>
 * 
 * TODO: next paragraph not implemented.
 * <p>
 * If the zoom is very a big one, you are not interested in the RMS curves but
 * in the waveform. This component will display the waveform when you scroll at
 * the maximum.
 * </p>
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class WaveFormComponent extends JPanel
		implements MouseInputListener, MouseWheelListener, AdjustmentListener, AudioDocumentListener {
	private static Logger LOG = LogFactory.getLog(WaveComponent.class);
	AudioDocument audio = null;
	private JScrollBar scroll = new JScrollBar();
	private WaveComponent wave = new WaveComponent();

	/** mousePosition in samples */
	private int mousePosition = -1;

	/** player head in samples */
	private int playHead = -1;
	
	/**
	 * Return the play head expressed in samples.
	 * 
	 * @return the play head sample.
	 */
	public int getPlayHead(){
		return this.playHead;
	}

	/** The first visible sample */
	private int firstVisibleSample = 0;

	/** The last visible sample */
	private int lastVisibleSample = Integer.MAX_VALUE;

	private int numberOfSamples = 0;

	// Colors to be used. Note alpha must NOT be set
	// for Windows (because the platform is too lengthly
	// for repainting).
	public static final Color peakColor = new Color(52, 152, 219).brighter();
	public static final Color rmsColor = new Color(41, 128, 185);

	/**
	 * The component which is in charge of the display.
	 * 
	 * @author wrey75
	 *
	 */
	private class WaveComponent extends JComponent {

		private void setStroke(Graphics g, double width) {
			if (g instanceof Graphics2D) {
				Stroke stroke = new BasicStroke((float) width);
				((Graphics2D) g).setStroke(stroke);
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

			int ratio = audio.getSampleSize();
			// float scrollPos = getScrollPosition() / audio.getSampleSize();

			double strokeWidth = 1.0; // rect.width * zoom / samples.length;
			setStroke(g, strokeWidth);

			// double zoom = (double)audio.getNumberOfSamples() / getExtent();
			int first = firstVisibleSample / ratio; // first visible
			int last = lastVisibleSample / ratio; // last visible
			if (last >= samples.length) {
				LOG.warn("Last is {} but a maximum of {} was expected. Fixing the issue.", last, samples.length);
				last = samples.length - 1;
				if (lastVisibleSample > numberOfSamples) {
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
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Rectangle rect = g.getClipBounds();
			if (audio != null) {
				RMSSample[] samples = audio.getLevels();

				g.setColor(new Color(44, 62, 80).darker());
				g.fillRect(rect.x, rect.y, rect.width, rect.height);
				if (samples != null) {
					drawLevels(g, samples);
					// int imgWidth = this.wave.getWidth();
					// int imgHeight = this.wave.getHeight();
					// g.drawImage(this.wave, rect.x, rect.y, rect.x +
					// rect.width, rect.y + rect.height,
					// 0, 0, imgWidth, imgHeight, null);
					g.setColor(Color.WHITE);
					g.drawString("Number of samples: " + samples.length, 10, 20);
					g.drawString("Farme rate: " + audio.getFormat().getFrameRate(), 10, 40);
				}
			}

			setStroke(g, 1.0);
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(mousePosition - firstVisibleSample, 0, mousePosition - firstVisibleSample, rect.height);

			g.setColor(Color.GREEN.brighter());
			g.drawLine(sampleToX(playHead), 0, sampleToX(playHead), rect.height);
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

	int getScrollPosition() {
		return scroll.getValue();
	}

	public WaveFormComponent() {
		this.audio = null;
		this.scroll.setOrientation(JScrollBar.HORIZONTAL);
		this.scroll.addAdjustmentListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		// this.wave.addKeyListener(this);
		this.setLayout(new BorderLayout());
		this.add(scroll, BorderLayout.SOUTH);
		this.add(wave, BorderLayout.CENTER);
		// this.wave.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
		// 0), "play");
		// this.wave.getActionMap().put("play", new Action (){
	}

	/**
	 * Set the document for drawing the wave. The audio document is in charge of
	 * everything!
	 * 
	 * @param doc
	 *            the audio document.
	 */
	public void setDocument(AudioDocument doc) {
		this.audio = doc;
		this.audio.register(this);
		updateAudio();
	}
	
	protected void scrollTo(int first, int last){
		if( first != this.firstVisibleSample || last != this.lastVisibleSample ){
			if( first != -1 ) this.firstVisibleSample = first;
			if( last != -1 ) this.lastVisibleSample = last;
			this.scroll.setValues(this.firstVisibleSample, this.lastVisibleSample - this.firstVisibleSample, 0, this.numberOfSamples);
			this.wave.invalidate();
		}
	}

	/**
	 * If the audio changed, update the values linked to it.
	 */
	protected void updateAudio() {
		if (numberOfSamples != audio.getNumberOfSamples()) {
			this.numberOfSamples = audio.getNumberOfSamples();
			this.lastVisibleSample = Math.min(this.lastVisibleSample, this.numberOfSamples);
			if (this.lastVisibleSample < this.firstVisibleSample) {
				this.firstVisibleSample = 0;
			}
			scrollTo(-1, -1);
		}
		repaint();
	}

	// protected void updateScrollBar(){
	//
	// }

	/**
	 * Modify the zoom level for this wave. Note the change in the zoom level
	 * will trigger a repaint of the screen.
	 * 
	 * @param newValue
	 *            the new value
	 */
	public void setExtent(int newExtent) {
		int first, last;
		int max = numberOfSamples;
		if (newExtent >= max) {
			first = 0;
			last = numberOfSamples;
		} else {
			int extend = this.getExtent();
			int diff = (newExtent - extend) / 2;
			last = this.lastVisibleSample + diff;
			first = this.firstVisibleSample - diff;
		}
		int unit = this.numberOfSamples / audio.getSampleSize();
		this.scroll.setUnitIncrement(unit);
		this.scroll.setBlockIncrement(unit * 10);
		this.scrollTo(first, last);
		this.repaint();
	}

	public int getExtent() {
		return this.lastVisibleSample + this.firstVisibleSample;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = e.getPoint().x + firstVisibleSample;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO: LOAD THE FILE DRAGGED (IF POSSIBLE)
		System.out.println("Dragging..");
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!e.isShiftDown()) {
			int rotation = e.getWheelRotation();
			this.setExtent(this.getExtent() + rotation * 10000);
		}
	}

	@Override
	public void audioChanged() {
		// int max = audio.getNumberOfSamples();
		// if( max != numberOfSamples ){
		this.updateAudio();
		// }
		// repaint();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int diff = (e.getValue() - firstVisibleSample);
		lastVisibleSample += diff;
		firstVisibleSample += diff;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		playHead = sampleFrom(e.getX());
		repaint();
	}

	/**
	 * Get the sample position from the x position from the visible window.
	 * 
	 * @param x
	 *            the x position
	 * @return
	 */
	protected int sampleFrom(int x) {
		int visibleSamples = (lastVisibleSample - firstVisibleSample);
		double samplePos = ((double) x * visibleSamples / this.wave.getWidth());
		int pos = (int) samplePos + firstVisibleSample;
		LOG.debug("sampleFrom({})={}", x, pos);
		return pos;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Store the position for selection...
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void audioPlayed(int sample) {
		playHead = sample;
		if( playHead > this.lastVisibleSample ){
			scroll.setValue( playHead );
		}
		repaint();
	}

	@Override
	public void audioPaused() {
		// TODO - Update the "PLAY/RECORD BUTTONS"
	}

}
