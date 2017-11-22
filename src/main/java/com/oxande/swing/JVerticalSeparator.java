package com.oxande.swing;

import java.awt.Graphics;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class JVerticalSeparator extends JComponent {

	protected void paintComponent( Graphics g){
		g.setColor(this.getForeground());
		g.drawLine(getWidth() / 2, 5, getWidth() / 2, getHeight() - 5);
	}
}
