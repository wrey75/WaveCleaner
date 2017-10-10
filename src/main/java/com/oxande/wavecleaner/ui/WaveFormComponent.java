package com.oxande.wavecleaner.ui;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.MouseInputListener;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.audio.AudioDocument;
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
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class WaveFormComponent extends JPanel
		implements MouseInputListener, MouseWheelListener, AdjustmentListener {
	private static Logger LOG = LogFactory.getLog(WaveFormComponent.class);
	AudioDocument audio = null;
	private JScrollBar scroll = new JScrollBar();
	private WaveComponent wave = new WaveComponent();


//		RIGHT CHANNEL :
//		-       TURQUOISE : #1abc9c -- rgb(26, 188, 156)
//		-       GREEN SEA : #16a085 -- rgb(22, 160, 133)
//		 
//		LEFT CHANNEL :
//		-       EMERALD : #2ecc71  -- rgb(46, 204, 113)
//		-       NEPHRITIS : #27ae60 -- rgb(39, 174, 96)
		

	
	/**
	 * Return the play head expressed in samples.
	 * 
	 * @return the play head sample.
	 */
	public int getPlayHead(){
		return this.wave.playHead;
	}
	
	public void setPlayHead(int pos, boolean sync ){
		this.wave.playHead = pos;
		if( sync && (pos > this.wave.lastVisibleSample) ){
			this.scroll.setValue( this.wave.playHead );
		}
		repaint();
	}

	/** The first visible sample */
	private int firstVisibleSample = 0;

	/** The last visible sample */
	private int lastVisibleSample = Integer.MAX_VALUE;

	private int numberOfSamples = 0;

	/**
	 * Get the scroll position
	 * 
	 * @return
	 */
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
	public void setAudioDocument(AudioDocument doc) {
		this.audio = doc;
		this.wave.setAudioDocument(doc);
		updateAudio();
	}
	
	protected void scrollTo(int first, int last){
		if( first != this.firstVisibleSample || last != this.lastVisibleSample ){
			if( first != -1 ) this.firstVisibleSample = first;
			if( last != -1 ) this.lastVisibleSample = last;
			this.scroll.setValues(this.firstVisibleSample, this.lastVisibleSample - this.firstVisibleSample, 0, this.numberOfSamples);
			this.wave.setVisibleWindow(this.firstVisibleSample, this.lastVisibleSample);
			repaint();
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
			int unit = this.numberOfSamples / audio.getChunkSize();
			this.scroll.setUnitIncrement(unit); // 1 buffer size (about 20 ms)
			this.scroll.setBlockIncrement(unit * 10); // 10 buffers (about 200 ms)
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
		if( newExtent <= 0 ){
			return;
		}
		else if (newExtent >= numberOfSamples) {
			this.firstVisibleSample = 0;
			this.lastVisibleSample = numberOfSamples;
		} 
		else {
			int extend = this.getExtent();
			int diff = (newExtent - extend) / 2;
			this.lastVisibleSample += diff;
			this.firstVisibleSample -= diff;
		}
		this.scrollTo(-1, -1);
		this.repaint();
	}

	public int getExtent() {
		int val = this.lastVisibleSample - this.firstVisibleSample;
		// LOG.debug("getExtend() = {} ({} - {})", val, this.firstVisibleSample, this.lastVisibleSample);
		return val;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.wave.mousePosition = e.getPoint().x + firstVisibleSample;
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
			double rotation = e.getWheelRotation();
			double newExtent = (this.getExtent() + rotation * 10000.0);
			// LOG.debug("Rotation: {}, newExtent: {}", rotation, newExtent);
			this.setExtent((int)newExtent);
		}
	}



	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int diff = e.getValue() - firstVisibleSample;
		lastVisibleSample += diff;
		firstVisibleSample += diff;
		scrollTo(-1,-1);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.wave.playHead = sampleFrom(e.getX());
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



}
