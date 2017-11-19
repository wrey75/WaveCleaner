package com.oxande.wavecleaner.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.Logger;

import com.oxande.swing.JMeter;
import com.oxande.wavecleaner.WaveCleaner;
import com.oxande.wavecleaner.filters.AudioFilter;
import com.oxande.wavecleaner.filters.AudioFilter.Parameter;
import com.oxande.wavecleaner.filters.ClickRemovalFilter;
import com.oxande.wavecleaner.filters.DecrackleFilter;
import com.oxande.wavecleaner.filters.PreamplifierFilter;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.WaveUtils;
import com.oxande.wavecleaner.util.logging.LogFactory;

@SuppressWarnings("serial")
public class ControllerComponent extends AbstractControllerComponent implements ItemListener, ChangeListener, ActionListener {
	private static Logger LOG = LogFactory.getLog(ControllerComponent.class);
	BufferedImage background;
	private DecrackleFilter decrackleFilter;
	private ClickRemovalFilter declickFilter;
	private PreamplifierFilter preamplifierFilter;
	
	public static final String MIXED = "MIXED";
	public static final String ORIGINAL = "ORIGINAL";
	public static final String DIFF = "DIFF";
	public static final String LEFT_RIGHT = "L/R";
	
	public long samplesToMicroseconds(int nbSamples ){
		return (long)(nbSamples * 1000000 / preamplifierFilter.sampleRate());
	}

	public ControllerComponent() {
		super();
		initComponents();

		// The following code should be taken into account by
		// XML4SWING...!
		this.crackle.addItemListener(this);
		this.click.addItemListener(this);
	}
	
	/**
	 * Used to refresh all the values.
	 * 
	 */
	protected final void refreshValues(){
		// setCrackleFactorLabel("Factor: " + decrackleFilter.getControl(DecrackleFilter.FACTOR));
		// setCrackleWindowLabel("Window: " + samplesToMilliseconds(decrackleFilter.getIntControl(DecrackleFilter.WINDOW)) + "ms.");
		// setCrackleAverageLabel("Average: " + decrackleFilter.getIntControl(DecrackleFilter.AVERAGE));
	}
	
	
//	@Override
//	protected void crackleAverageChanged(){
//		decrackleFilter.setControl(DecrackleFilter.AVERAGE, crackle_average.getValue());
//		refreshValues();
//	}
//	
//	@Override
//	protected void clickThresoldChanged(){
//		clickFilter.setControl(ClickRemovalFilter.THRESHOLD, thresold_factor.getValue());
//		refreshValues();
//	}

	@Override
	protected void clickWindowChanged(){
		declickFilter.setControl(ClickRemovalFilter.WIDTH, declick_window.getValue());
		refreshValues();
	}

	
	public void initComponents() {
		URL url = getClass().getClassLoader().getResource("images/sono.png");
		try {
			background = ImageIO.read(url);
			Dimension size = new Dimension(background.getWidth() / 3, background.getHeight() / 3);
			this.setPreferredSize(size);
			this.setMinimumSize(size);
			this.setMaximumSize(size);
			this.setOpaque(true);
			this.setBackground(null);
		} catch (IOException ex) {
			LOG.error("Can not load image: {}", ex.getMessage());
		}
		super.initComponents();
		this.setVisible(true);
	}

	private void setFrom(Parameter p, JMeter i){
		i.setMinimumValue( p.getMinimum() );
		i.setMaximumValue( p.getMaximum() );
		i.setStepValue(p.getStep());
		i.setValue(p.getValue());
		i.addChangeListener(this);
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
		this.declickFilter = filter2;
		this.click.setSelected(this.declickFilter.isEnabled());

		this.preamplifierFilter = lastFilter;
		this.preamplifierFilter.setControl(PreamplifierFilter.GAIN, +6.0f);
		
		crackleFactor.setTitle("Factor");
		Parameter p0 = decrackleFilter.getParameter(DecrackleFilter.FACTOR);
		setFrom(p0, crackleFactor);
		crackleFactor.setFormatter((e) -> {
			DecimalFormat formatter = new DecimalFormat("0.0");
			return formatter.format(e);			
		});
		// this.initValue(crackleFactor, this.decrackleFilter, DecrackleFilter.FACTOR);
		
		
		// this.initValue(crackle_window, this.decrackleFilter, DecrackleFilter.WINDOW);
		Parameter p1 = decrackleFilter.getParameter(DecrackleFilter.WINDOW);
		crackle_window.setTitle("Window");
		crackle_window.setMinimumValue((int)(p1.getMinimum() * p1.getFactor()));
		crackle_window.addChangeListener(this);
		crackle_window.setFormatter((e) -> {
			return samplesToMicroseconds(decrackleFilter.getIntControl(DecrackleFilter.WINDOW)) + " \u00B5s";			
		});

		volume.setTitle("Volume");
		setFrom(preamplifierFilter.getParameter(PreamplifierFilter.GAIN), volume);
		volume.setFormatter((e) -> {
			DecimalFormat formatter = new DecimalFormat("0.0");
			return formatter.format(e) + " dB";			
		});
		
		Parameter p3 = decrackleFilter.getParameter(DecrackleFilter.AVERAGE);
		setFrom(p3, crackle_average);
		crackle_average.setTitle("Average");
		crackle_average.setFormatter((e) -> {
			//DecimalFormat formatter = new DecimalFormat("0.0");
			//return formatter.format(e);			
			return samplesToMicroseconds(decrackleFilter.getIntControl(DecrackleFilter.AVERAGE)) + " \u00B5s";			
		});
		// this.initValue(crackle_average, this.decrackleFilter, DecrackleFilter.AVERAGE);
		
		Parameter p2 = declickFilter.getParameter(declickFilter.THRESHOLD);
		setFrom(p2, declickThresold);
		declickThresold.setTitle("Thresold");
		declickThresold.setFormatter((e) -> {
			DecimalFormat formatter = new DecimalFormat("0.0");
			return formatter.format(e);			
			// return samplesToMicroseconds(declickFilter.getControl(declickFilter.THRESHOLD));			
		});
		
		output.setButtons(MIXED, ORIGINAL, DIFF, LEFT_RIGHT);
		output.addActionListener(this);
		refreshValues();
		
		TitledBorder titleBorder;
		titleBorder = BorderFactory.createTitledBorder("Decrakling");
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12 );
		this.panelCrackle.setBorder(titleBorder);
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
			this.declickFilter.setEnable(bSelected);
		} else {
			LOG.warn("Source {} unknown.", e.getSource());
		}
	}
	
	@Override public Dimension getPreferredSize()
	{
	    return new Dimension(120, 120);
	}
	
	// ------------------------------------------
	

	@Override
	public void actionPerformed(ActionEvent evt) {
		Assert.isEventDispatchThread();
		Component btn = (Component)evt.getSource();
		switch(btn.getName()){
			case MIXED:
				preamplifierFilter.setControl(PreamplifierFilter.SOURCE, PreamplifierFilter.NORMAL);
				break;
			case ORIGINAL:
				preamplifierFilter.setControl(PreamplifierFilter.SOURCE, PreamplifierFilter.ORIGINAL);
				break;
			case DIFF:
				preamplifierFilter.setControl(PreamplifierFilter.SOURCE, PreamplifierFilter.DIFF);
				break;	
			case LEFT_RIGHT:
				preamplifierFilter.setControl(PreamplifierFilter.SOURCE, PreamplifierFilter.LEFT_RIGHT);
				break;	
			default:
				throw new IllegalArgumentException("Unexpected value '" + btn.getName() + "'.");
		}
	}
	
	

	@Override
	public void stateChanged(ChangeEvent e) {
		Assert.notNull(e);
		if( e.getSource() == crackle_window ){
			decrackleFilter.setControl(DecrackleFilter.WINDOW, crackle_window.getValue());
		} else if( e.getSource() == volume ){
			preamplifierFilter.setControl(PreamplifierFilter.GAIN, volume.getValue());
		} else if(e.getSource() == crackleFactor ){
			decrackleFilter.setControl(DecrackleFilter.FACTOR, crackleFactor.getValue() / 10.0f);
		} else if(e.getSource() == crackle_average ){
			decrackleFilter.setControl(DecrackleFilter.AVERAGE, crackle_average.getValue());
		} else if(e.getSource() == declickThresold ){
			declickFilter.setControl(declickFilter.THRESHOLD, declickThresold.getValue());
		} else {
			LOG.error("Source {} not found?!?", e.getSource());
		}
		refreshValues();		
	}


	
	protected void paintComponent(Graphics g0) {
		Graphics2D g = (Graphics2D) g0;
		super.paintComponent(g);
		if (background != null) {
			g.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0, background.getWidth(), background.getHeight(),
					null);
		}

	}
}
