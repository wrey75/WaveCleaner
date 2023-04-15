package com.oxande.wavecleaner.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class WaveUtils {

	public static Icon loadIcon(String fileName, int size){
		Icon icon = null;
		try {
			String name = "images/" + fileName;
			URL url = WaveUtils.class.getClassLoader().getResource(name);
			BufferedImage img = ImageIO.read(url);
			if( size > 0 ){
				// Dimension newSize = new Dimension(size, size);
				Image resized =  img.getScaledInstance( size, size, Image.SCALE_SMOOTH);
				icon = new ImageIcon(resized);
			}
			else {
				icon = new ImageIcon(img);
			}
		} catch (IOException ex) {
			LOG.error("Can not load image: {}", ex.getMessage());
		}
		return icon;
	}
}
