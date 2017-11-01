package com.oxande.wavecleaner.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;

/**
 * This is to display a "real time" waveform. Mainly used for recording purposes.
 * 
 * @author wrey75
 *
 */
@SuppressWarnings("serial")
public class RealtimeWaveComponent  extends JComponent {
	private static Logger LOG = LogFactory.getLog(RealtimeWaveComponent.class);
	
	protected Color rightColor = WaveComponent.RIGHT_COLOR;
	protected Color leftColor = WaveComponent.LEFT_COLOR;
	
	float samples[][] = new float[2][0];
	
	public RealtimeWaveComponent() {
		super();
		this.setBackground(Color.BLACK);
	}
	
	/**
	 * Update the wave and repaint. We expect to have the same
	 * number of samples on the right and on the left.
	 * 
	 * @param left the left samples
	 * @param right the right samples
	 */
	void update(float[] left, float[] right){
		SwingUtilities.invokeLater( () -> {
			samples[0] = left; // input.left.toArray();
			samples[1] = right; // input.right.toArray();
			repaint();
		});
	}
	
	@Override
	public Dimension getMinimumSize(){
		return new Dimension(24,24);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(200,500);
	}

	@Override
	public Dimension getMaximumSize(){
		return new Dimension(2000,2000);
	}
	
	protected void drawWave(Graphics g){
		int height = getHeight();
		int width = getWidth();
		
		float h = (height / 2) - 10;
		for( int j = 0; j < 2; j++){
			int y_middle = (int)(height / 2 + (height * 0.15) * (j == 0 ? -1.0 : +1.0));
			g.setColor(j == 0 ? leftColor : rightColor);
			int len = samples[j].length;
			double x_old = -100;
			double y_old = 0;
			for(int k = 0; k < len; k++){
				double x = ((double)k * width / len) ;
				int y = (int)(samples[j][k] * h);
				// Optimization: draw as less as possible 
				g.drawLine((int)x_old, (int)y_old + y_middle, (int)x, y + y_middle);
				x_old = x;
				y_old = y;
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g0) {
		Graphics2D g = ((Graphics2D) g0);
		super.paintComponent(g);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		drawWave(g);
	}

}
