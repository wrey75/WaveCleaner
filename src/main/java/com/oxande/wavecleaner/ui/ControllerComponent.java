package com.oxande.wavecleaner.ui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.filters.AudioFilter;
import com.oxande.wavecleaner.filters.AudioFilter.Parameter;
import com.oxande.wavecleaner.filters.ClickRemovalFilter;
import com.oxande.wavecleaner.filters.DecrackleFilter;
import com.oxande.wavecleaner.filters.PreamplifierFilter;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.logging.LogFactory;

@SuppressWarnings("serial")
public class ControllerComponent extends AbstractControllerComponent implements ItemListener {
	private static Logger LOG = LogFactory.getLog(ControllerComponent.class);
//	BufferedImage background;
	DecrackleFilter decrackleFilter;
	ClickRemovalFilter clickFilter;
	PreamplifierFilter controlFilter;
	
	public long samplesToMilliseconds(int nbSamples ){
		return (long)(nbSamples * 1000 / 48000.0);
	}

	public ControllerComponent() {
		super();
		initComponents();

		// The following code should be taken into account by
		// XML4SWING...!
		this.crackle.addItemListener(this);
		this.click.addItemListener(this);
	}
	
	protected final void refreshValues(){
		setCrackleFactorLabel("Factor: " + decrackleFilter.getControl(DecrackleFilter.FACTOR));
		setCrackleWindowLabel("Window: " + samplesToMilliseconds(decrackleFilter.getIntControl(DecrackleFilter.WINDOW)) + "ms.");
		setCrackleAverageLabel("Average: " + decrackleFilter.getIntControl(DecrackleFilter.AVERAGE));
		
	}
	
	@Override
	protected void crackleFactorChanged(){
		decrackleFilter.setControl(DecrackleFilter.FACTOR, crackle_factor.getValue() / 10.0f);
		refreshValues();
	}

	@Override
	protected void crackleWindowChanged(){
		decrackleFilter.setControl(DecrackleFilter.WINDOW, crackle_window.getValue());
		refreshValues();
	}
	
	@Override
	protected void crackleAverageChanged(){
		decrackleFilter.setControl(DecrackleFilter.AVERAGE, crackle_average.getValue());
		refreshValues();
	}
	
	@Override
	protected void clickThresoldChanged(){
		clickFilter.setControl(ClickRemovalFilter.THRESHOLD, thresold_factor.getValue());
		refreshValues();
	}

	@Override
	protected void clickWindowChanged(){
		clickFilter.setControl(ClickRemovalFilter.WIDTH, declick_window.getValue());
		refreshValues();
	}

	@Override
	protected void volumeChanged(){
		controlFilter.setControl(PreamplifierFilter.GAIN, volume.getValue());
		refreshValues();
	}
	
	public void initComponents() {
//		URL url = getClass().getClassLoader().getResource("images/sono.png");
//		try {
//			background = ImageIO.read(url);
//			Dimension size = new Dimension(background.getWidth(), background.getHeight());
//			this.setPreferredSize(size);
//			this.setMinimumSize(size);
//			this.setMaximumSize(size);
//			this.setOpaque(false);
//		} catch (IOException ex) {
//			LOG.error("Can not load image: {}", ex.getMessage());
//		}
		super.initComponents();
		this.setVisible(true);
	}

	/**
	 * Set the filter driven by this controller.
	 * 
	 * @param filter1
	 *            the {@link DecrackleFilter} filter.
	 */
	public void setFilters(DecrackleFilter filter1, ClickRemovalFilter filter2, PreamplifierFilter lastFilter) {
		this.decrackleFilter = filter1;
		this.crackle.setSelected(this.decrackleFilter.isEnabled());
		this.clickFilter = filter2;
		this.click.setSelected(this.clickFilter.isEnabled());
		this.initValue(crackle_factor, this.decrackleFilter, DecrackleFilter.FACTOR);
		this.initValue(crackle_window, this.decrackleFilter, DecrackleFilter.WINDOW);
		this.initValue(crackle_average, this.decrackleFilter, DecrackleFilter.AVERAGE);
		
		this.controlFilter = lastFilter;
		this.controlFilter.setControl(PreamplifierFilter.GAIN, +6.0f);
		refreshValues();
	}

	
	/**
	 * Init a {@link JSlider} based on the parameter of the filter.
	 * 
	 * @param slider the slider
	 * @param filter the filter concerned
	 * @param control the name of the controller.
	 */
	protected void initValue(JSlider slider, AudioFilter filter, String control ){
		Parameter p = filter.getParameter(control);
		slider.setMinimum( (int)(p.getMinimum() * p.getFactor()));
		slider.setMaximum( (int)(p.getMaximum() * p.getFactor()));
		slider.setValue((int)(p.getValue() * p.getFactor()));
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (this.crackle == e.getSource()) {
			boolean bSelected = this.crackle.isSelected();
			this.decrackleFilter.setEnable(bSelected);
		} else if (this.click == e.getSource()) {
			boolean bSelected = this.click.isSelected();
			this.clickFilter.setEnable(bSelected);
		} else {
			LOG.warn("Source {} unknown.", e.getSource());
		}
	}
	
	@Override public Dimension getPreferredSize()
	{
	    return new Dimension(120, 120);
	}
	
	// ------------------------------------------
	
	private void setSource( JToggleButton btn, int source ){
		Assert.isTrue( SwingUtilities.isEventDispatchThread() );
		if( btn.isSelected() ){
			controlFilter.setControl(PreamplifierFilter.SOURCE, source);
			if( source != 0 ) finalOutput.setSelected(false);
			if( source != 1 ) originalOutput.setSelected(false);
			if( source != 2 ) diffOutput.setSelected(false);
			if( source != 3 ) leftRightOutput.setSelected(false);
		}
	}
	
	protected void onOriginalOutput(){
		setSource(originalOutput, 1);
	}
	
	protected void onFinalOutput() {
		setSource(finalOutput, 0);
	}
	
	protected void onDiffOutput(){
		setSource(diffOutput, 2);
	}
	
	protected void onLeftRightOutput(){
		setSource(leftRightOutput, 3);
	}
	
//	protected void paintComponent(Graphics g0) {
//		Graphics2D g = (Graphics2D) g0;
//		super.paintComponent(g);
//		if (background != null) {
//			g.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0, background.getWidth(), background.getHeight(),
//					null);
//		}
//
//	}
}
