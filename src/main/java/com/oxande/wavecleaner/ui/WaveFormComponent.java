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

import com.oxande.wavecleaner.RMSSample;

@SuppressWarnings("serial")
public class WaveFormComponent extends JPanel
		implements MouseInputListener ,MouseWheelListener, AdjustmentListener, AudioDocumentListener {
	AudioDocument audio = null;
	double zoom = 1.0;
	private JScrollBar scroll = new JScrollBar();
	private WaveComponent wave = new WaveComponent();
	private int mousePosition = -1;
	private int mouseSelected = -1;

	public static final Color peakColor = new Color(52, 152, 219, 100).brighter();
	public static final Color rmsColor = new Color(41, 128, 185, 100);

	private class WaveComponent extends JComponent {

		private void setStroke(Graphics g, double width) {
			if (g instanceof Graphics2D) {
				Stroke stroke = new BasicStroke((float) width );
				((Graphics2D) g).setStroke(stroke);
			}
		}

		private void drawLevels(Graphics g, RMSSample[] samples) {
			Rectangle rect = g.getClipBounds();
			int y = rect.height / 2;
			float h = rect.height / 2 - 10;
			double zoom = getZoomLevel();
			float scrollPos = getScrollPosition() / audio.getSampleSize();

			double strokeWidth = rect.width * zoom / samples.length;
			
			setStroke(g, strokeWidth);

			for (int i = 0; i < samples.length; i++) {
				RMSSample s = samples[i];
				if (s != null) {
					int x = (int) ((i - scrollPos) * (float) rect.width * zoom / samples.length);
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
			
			setStroke(g,1.0);
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
		this.setLayout(new BorderLayout());
		this.add(scroll, BorderLayout.SOUTH);
		this.add(wave, BorderLayout.CENTER);
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
		this.repaint();
	}

	/**
	 * Modify the zoom level for this wave. Note the change in the zoom level
	 * will trigger a repaint of the screen.
	 * 
	 * @param newValue
	 *            the new value
	 */
	public void setZoom(double newValue) {
		this.zoom = (newValue > 1.0 ? newValue : 1.0);
		int max = audio.getNumberOfSamples();
		int current = this.scroll.getValue();
		this.scroll.setValues(current, (int) (max / zoom), 0, max);
		this.repaint();
	}

	public double getZoomLevel() {
		return this.zoom;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = e.getPoint().x;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO: LOAD THE FILE DRAGGED (IF POSSIBLE)
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!e.isShiftDown()) {
			int rotation = e.getWheelRotation();
			this.setZoom(this.getZoomLevel() + rotation / 10.0);
		}
	}

	@Override
	public void audioChanged() {
		System.out.println("Audio " + this.audio + " changed.");
		int nbChunks = audio.getNumberOfChunks();
		int max = audio.getNumberOfSamples();
		int current = this.scroll.getValue();
		this.scroll.setValues(current, (int) (max / zoom), 0, max);
		repaint();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mouseSelected = e.getX();
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
