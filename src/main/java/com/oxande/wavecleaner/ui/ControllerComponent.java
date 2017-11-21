package com.oxande.wavecleaner.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.Logger;

import com.oxande.swing.JToggleSelect;
import com.oxande.wavecleaner.filters.AudioFilter;
import com.oxande.wavecleaner.filters.AudioFilter.Parameter;
import com.oxande.wavecleaner.filters.ClickRemovalFilter;
import com.oxande.wavecleaner.filters.DecrackleFilter;
import com.oxande.wavecleaner.filters.PreamplifierFilter;
import com.oxande.wavecleaner.ui.JFilterMeter.ValueListener;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.logging.LogFactory;

@SuppressWarnings("serial")
public class ControllerComponent extends AbstractControllerComponent implements /* ItemListener,  ChangeListener, */ ActionListener {
	private static Logger LOG = LogFactory.getLog(ControllerComponent.class);
	BufferedImage background;
	private DecrackleFilter decrackleFilter;
	private ClickRemovalFilter declickFilter;
	private PreamplifierFilter preamplifierFilter;
	private List<JFilterMeter> meterList = new ArrayList<>();
	
	public static final String MIXED = "MIXED";
	public static final String ORIGINAL = "ORIGINAL";
	public static final String DIFF = "DIFF";
	public static final String LEFT_RIGHT = "L/R";
	
	public long samplesToMicroseconds(int nbSamples ){
		return (long)(nbSamples * 1000000 / preamplifierFilter.sampleRate());
	}

	private void addSwith(JPanel panel, AudioFilter filter){
		JToggleSelect comp = new JToggleSelect();
		comp.setButtons( "SWITCH" );
		comp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean active = e.getActionCommand().equals("ON");
				filter.setEnable(active);
			}
		});
		panel.add(comp);
	}
	
	private void setPanelTitle(String title, JPanel panel){
		// Add title
		TitledBorder titleBorder;
		titleBorder = BorderFactory.createTitledBorder(title);
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12 );
		titleBorder.setTitleFont(font);
		panel.setBorder(titleBorder);
	}
	
	private void addMeter( JPanel panel, AudioFilter filter, String name, String label ){
		JFilterMeter m = new JFilterMeter(filter, name, label);
		m.addValueListener(new ValueListener() {
			
			public String formattedValue(){
				Parameter p = filter.getParameter(name);
				return p.getFormattedValue();
			}
			
			private void updateValue(JFilterMeter e, int direction) {
				float value = filter.getControl(name);
				Parameter p = filter.getParameter(name);
				float newValue = Math.min(Math.max( p.getMinimum(), value + p.getTick() * direction), p.getMaximum());
				filter.setControl(name, newValue);
			}
			
			@Override
			public void valueIncremented(JFilterMeter e) {
				updateValue(e, +1);
			}
			
			@Override
			public void valueDecremented(JFilterMeter e) {
				updateValue(e, -1);
			}
		});
		panel.add(m);
		meterList.add(m);
	}
	

	public ControllerComponent() {
		super();
		initComponents();
	}
	
	public void initComponents() {
		super.initComponents();
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

		this.invalidate();
		this.setVisible(true);
	}


	/**
	 * Set the filter driven by this controller.
	 * 
	 * @param filter1
	 *            the {@link DecrackleFilter} filter.
	 */
	public void setFilters(DecrackleFilter filter1, ClickRemovalFilter filter2, PreamplifierFilter lastFilter) {
	
		setPanelTitle("Preamplifier", preampPanel );
		this.preamplifierFilter = lastFilter;
		this.preamplifierFilter.setControl(PreamplifierFilter.GAIN, +6.0f);
		output.setButtons(MIXED, ORIGINAL, DIFF, LEFT_RIGHT);
		output.addActionListener(this);
		addMeter(output, lastFilter, PreamplifierFilter.GAIN, "Volume" );
		
		this.decrackleFilter = filter1;
		setPanelTitle("Decrackling", panelCrackle);
		addSwith(panelCrackle, filter1);
		addMeter(panelCrackle, filter1, DecrackleFilter.FACTOR, "Factor" );
		addMeter(panelCrackle, filter1, DecrackleFilter.AVERAGE, "Average" );
		
		this.declickFilter = filter2;
		setPanelTitle("Click Removal", panelDeclick);
		addSwith(panelDeclick, filter2);
		addMeter(panelDeclick, filter2, ClickRemovalFilter.THRESHOLD, "Thresold" );
		addMeter(panelDeclick, filter2, ClickRemovalFilter.WIDTH, "Width" );
		
//		crackleFactor.setTitle("Factor");
//		Parameter p0 = decrackleFilter.getParameter(DecrackleFilter.FACTOR);
//		setFrom(p0, crackleFactor);
//		crackleFactor.setFormatter((e) -> {
//			DecimalFormat formatter = new DecimalFormat("0.0");
//			return formatter.format(e);			
//		});
//		// this.initValue(crackleFactor, this.decrackleFilter, DecrackleFilter.FACTOR);
//		
//		
//		// this.initValue(crackle_window, this.decrackleFilter, DecrackleFilter.WINDOW);
//		Parameter p1 = decrackleFilter.getParameter(DecrackleFilter.WINDOW);
//		crackle_window.setTitle("Window");
//		crackle_window.setMinimumValue((int)(p1.getMinimum() * p1.getFactor()));
//		crackle_window.addChangeListener(this);
//		crackle_window.setFormatter((e) -> {
//			return samplesToMicroseconds(decrackleFilter.getIntControl(DecrackleFilter.WINDOW)) + " \u00B5s";			
//		});
//
//		volume.setTitle("Volume");
//		setFrom(preamplifierFilter.getParameter(PreamplifierFilter.GAIN), volume);
//		volume.setFormatter((e) -> {
//			DecimalFormat formatter = new DecimalFormat("0.0");
//			return formatter.format(e) + " dB";			
//		});
//		
//		Parameter p3 = decrackleFilter.getParameter(DecrackleFilter.AVERAGE);
//		setFrom(p3, crackle_average);
//		crackle_average.setTitle("Average");
//		crackle_average.setFormatter((e) -> {
//			//DecimalFormat formatter = new DecimalFormat("0.0");
//			//return formatter.format(e);			
//			return samplesToMicroseconds(decrackleFilter.getIntControl(DecrackleFilter.AVERAGE)) + " \u00B5s";			
//		});
//		// this.initValue(crackle_average, this.decrackleFilter, DecrackleFilter.AVERAGE);
//		
//		Parameter p2 = declickFilter.getParameter(declickFilter.THRESHOLD);
//		setFrom(p2, declickThresold);
//		declickThresold.setTitle("Thresold");
//		declickThresold.setFormatter((e) -> {
//			DecimalFormat formatter = new DecimalFormat("0.0");
//			return formatter.format(e);			
//			// return samplesToMicroseconds(declickFilter.getControl(declickFilter.THRESHOLD));			
//		});
//		
//		Parameter p4 = declickFilter.getParameter(declickFilter.WIDTH);
//		setFrom(p2, declickWindow);
//		declickWindow.setTitle("Width");
//		declickWindow.setFormatter((e) -> {
//			// DecimalFormat formatter = new DecimalFormat("0.0");
//			// return formatter.format(e);			
//			return samplesToMicroseconds(declickFilter.getIntControl(ClickRemovalFilter.WIDTH)) + "";			
//		});

		this.invalidate();
	}

	
//	@Override
//	public void itemStateChanged(ItemEvent e) {
//		if (this.crackle == e.getSource()) {
//			boolean bSelected = this.crackle.isSelected();
//			this.decrackleFilter.setEnable(bSelected);
//		} else if (this.click == e.getSource()) {
//			boolean bSelected = this.click.isSelected();
//			this.declickFilter.setEnable(bSelected);
//		} else {
//			LOG.warn("Source {} unknown.", e.getSource());
//		}
//	}
	
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
	
	

//	@Override
//	public void stateChanged(ChangeEvent e) {
//		Assert.notNull(e);
//		if( e.getSource() == crackle_window ){
//			decrackleFilter.setControl(DecrackleFilter.WINDOW, crackle_window.getValue());
//		} else if( e.getSource() == volume ){
//			preamplifierFilter.setControl(PreamplifierFilter.GAIN, volume.getValue());
//		} else if(e.getSource() == crackleFactor ){
//			decrackleFilter.setControl(DecrackleFilter.FACTOR, crackleFactor.getValue() / 10.0f);
//		} else if(e.getSource() == crackle_average ){
//			decrackleFilter.setControl(DecrackleFilter.AVERAGE, crackle_average.getValue());
//		} else if(e.getSource() == declickThresold ){
//			declickFilter.setControl(declickFilter.THRESHOLD, declickThresold.getValue());
//		} else if(e.getSource() == declickWindow ){
//			declickFilter.setControl(declickFilter.WIDTH, declickWindow.getValue());
//		}else {
//			LOG.error("Source {} not found?!?", e.getSource());
//		}
//		refreshValues();		
//	}


	
	protected void paintComponent(Graphics g0) {
		Graphics2D g = (Graphics2D) g0;
		super.paintComponent(g);
		if (background != null) {
			g.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0, background.getWidth(), background.getHeight(),
					null);
		}

	}
}
