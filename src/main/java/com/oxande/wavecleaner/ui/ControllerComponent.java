package com.oxande.wavecleaner.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;

public class ControllerComponent extends JPanel {
	private static Logger LOG = LogFactory.getLog(ControllerComponent.class);
	BufferedImage background;

	public ControllerComponent() {
		URL url = getClass().getClassLoader().getResource("images/sono.png");
		try {
			background = ImageIO.read(url);
		} catch (IOException ex) {
			LOG.error("Can not load image: {}", ex.getMessage());
		}
	}

	protected void paintComponent(Graphics g0) {
		Graphics2D g = (Graphics2D) g0;
		if (background != null) {
			g.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0, background.getWidth(), background.getHeight(),
					null);
		}
	}
}
