package com.oxande.wavecleaner.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.Logger;

import com.oxande.swing.JToggleSelect;
import com.oxande.swing.JVerticalSeparator;
import com.oxande.wavecleaner.filters.AudioFilter;
import com.oxande.wavecleaner.filters.AudioFilter.Parameter;
import com.oxande.wavecleaner.filters.ClickRemovalFilter;
import com.oxande.wavecleaner.filters.DecrackleFilter;
import com.oxande.wavecleaner.filters.PreamplifierFilter;
import com.oxande.wavecleaner.ui.JFilterMeter.ValueListener;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.logging.LogFactory;

/**
 * The controller is quite complex in term of creating subcomponents. That's why we construct it
 * manually rather than relying on Xml4Swing.
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class ControllerComponent extends JPanel implements ActionListener {
	private static Logger LOG = LogFactory.getLog(ControllerComponent.class);
	private BufferedImage sono_up;
	private BufferedImage sono_middle;
	private BufferedImage sono_down;
	private static final int SCALE = 2;
	private static final int REPEAT = 10;
	private int finalWidth;
	
//	private DecrackleFilter decrackleFilter;
//	private ClickRemovalFilter declickFilter;
	private PreamplifierFilter preamplifierFilter;
	private List<JFilterMeter> meterList = new ArrayList<>();
	private JToggleSelect output = new JToggleSelect();
	
	public static final String MIXED = "MIXED";
	public static final String ORIGINAL = "ORIGINAL";
	public static final String DIFF = "DIFF";
	public static final String LEFT_RIGHT = "L/R";
	
	public long samplesToMicroseconds(int nbSamples ){
		return (long)(nbSamples * 1000000 / preamplifierFilter.sampleRate());
	}

	private void addToPanel(JPanel panel, JComponent comp){
		if( panel.getComponents().length > 0){
			JComponent sep = new JVerticalSeparator();
			sep.setMinimumSize(new Dimension( 10, 15));
			sep.setPreferredSize(new Dimension( 10, 15));
			panel.add(sep);
		}
		panel.add(comp);
		// panel.add(Box.createRigidArea(new Dimension(10, 0)));

	}

	private void addFiller(JPanel panel){
		Dimension minSize = new Dimension(5, 100);
		Dimension prefSize = new Dimension(5, 100);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
//		panel.add(new Box.Filler(minSize, prefSize, maxSize));
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
		addToPanel(panel, comp);
	}
	
	private JPanel createPanel(String title){
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		TitledBorder titleBorder;
		titleBorder = BorderFactory.createTitledBorder(title);
		Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12 );
		titleBorder.setTitleFont(font);
		panel.setBorder(titleBorder);
		BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(boxLayout);
		// panel.add(Box.createHorizontalGlue());
		this.add(panel); // Add to the current panel
		return panel;
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
		addToPanel(panel, m);
		meterList.add(m);
	}
	

	public ControllerComponent() {
		super();
		URL url1 = getClass().getClassLoader().getResource("images/preamp/up.png");
		URL url2 = getClass().getClassLoader().getResource("images/preamp/middle.png");
		URL url3 = getClass().getClassLoader().getResource("images/preamp/down.png");
		try {
			sono_up = ImageIO.read(url1);
			sono_middle = ImageIO.read(url2);
			sono_down = ImageIO.read(url3);
			finalWidth = sono_middle.getWidth() / SCALE;
			Dimension size = new Dimension(finalWidth, (sono_middle.getHeight() * REPEAT + sono_down.getHeight() + sono_middle.getHeight()) / SCALE);
			this.setPreferredSize(size);

			this.setMinimumSize(size);
			this.setMaximumSize(size);
		} catch (IOException ex) {
			LOG.error("Can not load image: {}", ex.getMessage());
		}

		this.setLayout(new BoxLayout( this, BoxLayout.PAGE_AXIS));
		this.setLayout(new GridLayout(3,1));
		this.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
		revalidate();
	}


	/**
	 * Set the filter driven by this controller.
	 * 
	 * @param filter1
	 *            the {@link DecrackleFilter} filter.
	 */
	public void setFilters(DecrackleFilter filter1, ClickRemovalFilter filter2, PreamplifierFilter lastFilter) {
		JPanel preampPanel = createPanel("Preamplifier" );
		this.preamplifierFilter = lastFilter;
		this.preamplifierFilter.setControl(PreamplifierFilter.GAIN, +2.0f);
		output.setButtons(MIXED, ORIGINAL, DIFF, LEFT_RIGHT);
		output.addActionListener(this);
		preampPanel.add(output);
		addMeter(preampPanel, lastFilter, PreamplifierFilter.GAIN, "Volume" );
		addFiller(preampPanel);
		
//		this.decrackleFilter = filter1;
		JPanel panelCrackle = createPanel("Decrackling");
		addSwith(panelCrackle, filter1);
		addMeter(panelCrackle, filter1, DecrackleFilter.FACTOR, "Factor" );
		addMeter(panelCrackle, filter1, DecrackleFilter.AVERAGE, "Average" );
		addFiller(panelCrackle);
		
//		this.declickFilter = filter2;
		JPanel panelDeclick = createPanel("Click Removal" );
		addSwith(panelDeclick, filter2);
		addMeter(panelDeclick, filter2, ClickRemovalFilter.THRESHOLD, "Thresold" );
		addMeter(panelDeclick, filter2, ClickRemovalFilter.WIDTH, "Width" );
		addFiller(panelDeclick);
		
		this.invalidate();
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
	
	
	protected void paintComponent(Graphics g0) {
		Graphics2D g = (Graphics2D) g0;
		super.paintComponent(g);
		if (sono_up != null) {
			int y = 0;
			int w = sono_up.getWidth();
			int h = sono_up.getHeight();
//			g.drawImage(sono_up, 0, y, w / SCALE, y + h / SCALE, 0, 0, w, h, null);
//			y += h / SCALE;
//					
//			for(int i = 0; i < REPEAT; i++){
//				g.drawImage(sono_middle, 0, y, w / SCALE, y + (sono_middle.getHeight() / SCALE), 0, 0, w, h, null);
//				y += (sono_middle.getHeight() / SCALE);
//			}
//			
//			h = sono_down.getHeight();
//			g.drawImage(sono_down, 0, y, w / SCALE, y + (sono_down.getHeight() / SCALE), 0, 0, w, h, null);
			
			g.drawImage(sono_up, 0, 0, getWidth(), h, 0, 0, w, h, null);
			y += h;
			
			h = sono_middle.getHeight();
			while(y < getHeight()){
				g.drawImage(sono_middle, 0, y - 1, getWidth(), y + h, 0, 0, w, h, null);
				y += h;
			}
			
			h = sono_down.getHeight();
			g.drawImage(sono_down, 0, getHeight() - h, getWidth(), getHeight(), 0, 0, w, h, null);
		}

	}
}
