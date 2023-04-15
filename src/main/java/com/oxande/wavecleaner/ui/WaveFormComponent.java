package com.oxande.wavecleaner.ui;

import com.oxande.wavecleaner.audio.AudioDocument;
import ddf.minim.AudioListener;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;

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
@Slf4j
@SuppressWarnings("serial")
public class WaveFormComponent extends JPanel
		implements MouseInputListener, MouseWheelListener, AdjustmentListener {
	AudioDocument audio = null;
	private JScrollBar scroll = new JScrollBar();
	private WaveComponent wave = new WaveComponent();
	private RegionSelected selection = null;
	
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
	
	/**
	 * Move the play head. If the synchronization flag, we follow the play head
	 * (this is cool during playback). We follow the play head if and only if the
	 * play head was visible and goes to the next page. This method will avoid issues
	 * when you scroll the window or other operations.
	 * 
	 * @param pos the new position (in samples)
	 * @param sync if we have to sync the play head (set to true when the message comes
	 * from the {@link AudioListener}).
	 */
	public void setPlayHead(int pos, boolean sync ){
		if( pos != this.wave.playHead ){
			// Only if the play head has moved!
			if( sync && (this.wave.playHead < this.wave.lastVisibleSample) && (pos >= this.wave.lastVisibleSample) ){
				this.scroll.setValue( this.wave.playHead );
			}
			this.wave.playHead = pos;
			repaint();
		}
	}

	/** The first visible sample */
	private int firstVisibleSample = 0;

	/** The last visible sample */
	private int lastVisibleSample = Integer.MAX_VALUE;

	private int numberOfSamples = 0;

	/**
	 * Get the scroll position.
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
		if( this.audio != null ){
			this.audio.stop();
			this.audio.dispose();
		}
		this.audio = doc;
		this.wave.setAudioDocument(doc);
		updateAudio();
	}
	
	private void scrollTo(/* int first, int last */) {
//		if (first != this.firstVisibleSample || last != this.lastVisibleSample) {
//			if (first != -1){
//				this.firstVisibleSample = Math.max(0, first);
//			}
//			if (last != -1){
//				this.lastVisibleSample = last;
//			}
		if( this.firstVisibleSample < 0 ){
			this.firstVisibleSample = 0;
			LOG.error("firstVisibleSample = {}", this.firstVisibleSample );
			return;
		}
		if( this.lastVisibleSample > this.numberOfSamples ){
			this.lastVisibleSample = this.numberOfSamples;
			LOG.error("lastVisibleSample = {} (first = {})", this.lastVisibleSample, this.firstVisibleSample );
			return;
		}
			SwingUtilities.invokeLater(() -> {
				this.scroll.setValues(this.firstVisibleSample, this.lastVisibleSample - this.firstVisibleSample, 0,
						this.numberOfSamples);
				this.wave.setVisibleWindow(this.firstVisibleSample, this.lastVisibleSample);
				repaint();
			});
//		}
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
			scrollTo(/* -1, -1 */);
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
		this.scrollTo(/*-1, -1*/ );
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
	public void mousePressed(MouseEvent e) {
		this.selection = new RegionSelected("loop", sampleFrom(e.getX()));
		this.audio.endLoop();
		this.wave.addSelection(this.selection);
		LOG.debug("Pressed at: {}", this.selection.begin );
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if( this.selection == null ){
			LOG.error("Mouse dragging while no selection!?!");
			this.mousePressed(e);
		}
		LOG.debug("Dragging..");
		this.selection.end = sampleFrom(e.getX());
		this.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		this.selection.validate();
		if( !this.selection.isEmpty() ){
			this.audio.startLoop(this.selection.begin, this.selection.end);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!e.isShiftDown()) {
			double rotation = e.getPreciseWheelRotation();
			double newExtent = (this.getExtent() + rotation * 10000.0);
			// LOG.debug("Rotation: {}, newExtent: {}", rotation, newExtent);
			this.setExtent((int)newExtent);
		}
		else {
            LOG.debug("mouseWheelMoved() dispatched.");
            e.getComponent().getParent().dispatchEvent(e);
		}
	}


	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int diff = e.getValue() - firstVisibleSample;
		lastVisibleSample += diff;
		firstVisibleSample = Math.max(0, firstVisibleSample + diff);
		scrollTo(/* -1,-1 */);
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		if( audio == null ) return;
		if( e.getClickCount() > 1) {
			LOG.info("Double-clicked at x={}", e.getX());
			int sample = sampleFrom(e.getX());
			audio.play(sample);
		}
		else if( audio.isPlaying() ){
			LOG.info("Click ignored when playing.");
		}
		else {
			this.wave.playHead = sampleFrom(e.getX());
		}
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
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
}
