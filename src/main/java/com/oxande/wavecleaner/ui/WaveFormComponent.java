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

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputListener;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.RMSSample;

/**
 * The waveform component is the central part of the software in terms of graphics.
 * 
 * The curve is displayed in ONLY one bar: the upper part is the left channel and the
 * bottom is the right channel. I found it simpler and clearer than having two
 * separate graphics. As for Audacity, I kept the RMS level in a color and the peaks
 * in the same but brighter (I used some tables for the colors).
 * 
 * <p>
 * The scrollbar is directly attached at the bottom of the window and always visible
 * (as far as I can). To zoom inside the sound, you can use the wheel of the mouse. For
 * Mac users (also having an horizontal wheel), I just used a trick found on StackOverflow.
 * </p>
 * <p>
 * The main problem is to synchronize the scrollbar to the wave form.
 * </p>
 * 
 * TODO: next paragraph not implemented.
 * <p>
 *  If the zoom is very a big one, you are not interested in the RMS curves but in the
 *  waveform. This component will display the waveform when you scroll at the maximum. 
 * </p>
 * 
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class WaveFormComponent extends JPanel
		implements MouseInputListener, MouseWheelListener, AdjustmentListener, AudioDocumentListener {
	private static Logger LOG = LogManager.getLogger();
	AudioDocument audio = null;
	private JScrollBar scroll = new JScrollBar();
	private WaveComponent wave = new WaveComponent();
	private int mousePosition = -1;
	private int mouseSelected = -1;

	public static final Color peakColor = new Color(52, 152, 219, 100).brighter();
	public static final Color rmsColor = new Color(41, 128, 185, 100);

	private class WaveComponent extends JComponent {

		private void setStroke(Graphics g, double width) {
			if (g instanceof Graphics2D) {
				Stroke stroke = new BasicStroke((float) width);
				((Graphics2D) g).setStroke(stroke);
			}
		}

		private void drawLevels(Graphics g, RMSSample[] samples) {
			Rectangle rect = g.getClipBounds();
			int y = rect.height / 2;
			float h = rect.height / 2 - 10;
			// double zoom = getExtent() / audio.getSampleSize();
			float scrollPos = getScrollPosition() / audio.getSampleSize();

			double strokeWidth = 1.0; // rect.width * zoom / samples.length;
			setStroke(g, strokeWidth);

			double zoom = (double)audio.getNumberOfSamples() / getExtent();
			LOG.debug("ZOOM: {}, ext = {}", zoom, getExtent() );
			for (int i = 0; i < samples.length; i++) {
				RMSSample s = samples[i];
				if (s != null) {
					int x = (int) ((i - scrollPos) * (float) rect.width *zoom / samples.length);
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
			g.drawLine(mousePosition, 0, mousePosition, rect.height);

			g.setColor(Color.GREEN.brighter());
			g.drawLine(mouseSelected, 0, mouseSelected, rect.height);
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
//		this.wave.addKeyListener(this);
		this.setLayout(new BorderLayout());
		this.add(scroll, BorderLayout.SOUTH);
		this.add(wave, BorderLayout.CENTER);
//		this.wave.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "play");
//		this.wave.getActionMap().put("play", new Action (){
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
		setExtent(this.audio.getNumberOfSamples());
		this.repaint();
	}

	/**
	 * Modify the zoom level for this wave. Note the change in the zoom level
	 * will trigger a repaint of the screen.
	 * 
	 * @param newValue
	 *            the new value
	 */
	public void setExtent(int extent) {
		int max = audio.getNumberOfSamples();
		if( extent > max ){
			extent = max;
		}
		int current = this.scroll.getValue();
		this.scroll.setValues(current, extent, 0, max);
		this.scrollExtend = extent;
		this.repaint();
	}

	private int scrollExtend;
	
	public int getExtent() {
		return this.scrollExtend;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = e.getPoint().x;
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
		int max = audio.getNumberOfSamples();
		int current = this.scroll.getValue();
		this.scroll.setValues(current, this.getExtent(), 0, max);
		repaint();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mouseSelected = e.getX();
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

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


}
