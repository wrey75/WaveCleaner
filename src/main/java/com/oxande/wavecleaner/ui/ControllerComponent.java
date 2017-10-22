package com.oxande.wavecleaner.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.audio.AudioDocument;
import com.oxande.wavecleaner.filters.DecrackleFilter;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.logging.LogFactory;

@SuppressWarnings("serial")
public class ControllerComponent extends AbstractControllerComponent implements ItemListener {
	private static Logger LOG = LogFactory.getLog(ControllerComponent.class);
	BufferedImage background;
	DecrackleFilter decrackleFilter;

	public ControllerComponent() {
		super();
		initComponents();

		// The following code should be taken into account by
		// XML4SWING...!
		this.crackle.addItemListener(this);
		this.crackle_average.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				Assert.equals(crackle_average, evt.getSource());
				decrackleFilter.average.setLastValue(crackle_average.getValue());
			}
		});
		this.crackle_window.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				Assert.equals(crackle_window, evt.getSource());
				decrackleFilter.window.setLastValue(crackle_window.getValue());
			}
		});
		this.crackle_factor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				Assert.equals(crackle_factor, evt.getSource());
				decrackleFilter.window.setLastValue(crackle_factor.getValue());
			}
		});
	}

	public void initComponents() {
		super.initComponents();
		URL url = getClass().getClassLoader().getResource("images/sono.png");
		try {
			background = ImageIO.read(url);
		} catch (IOException ex) {
			LOG.error("Can not load image: {}", ex.getMessage());
		}
		this.setVisible(true);
	}

	/**
	 * Set the filter driven by this controller.
	 * 
	 * @param filter1
	 *            the {@link DecrackleFilter} filter.
	 */
	public void setFilters(DecrackleFilter filter1) {
		this.decrackleFilter = filter1;
		this.crackle.setSelected(this.decrackleFilter.isEnabled());
		this.crackle_factor.setValue((int) (this.decrackleFilter.factor.getLastValue() * 10));
		this.crackle_window.setValue((int) (this.decrackleFilter.window.getLastValue()));
		this.crackle_average.setValue((int) (this.decrackleFilter.average.getLastValue()));
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (this.crackle == e.getSource()) {
			boolean bSelected = this.crackle.isSelected();
			this.decrackleFilter.setEnable(bSelected);
		} else {
			LOG.warn("Source {} unknown.", e.getSource());
		}
	}

	// protected void paintComponent(Graphics g0) {
	// Graphics2D g = (Graphics2D) g0;
	// if (background != null) {
	// g.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0,
	// background.getWidth(), background.getHeight(),
	// null);
	// }
	//
	// super.paintComponent(g);
	// }
}
