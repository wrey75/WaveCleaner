package com.oxande.wavecleaner.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JComponent;

import ddf.minim.AudioSample;
import ddf.minim.AudioSource;

@SuppressWarnings("serial")
public class WaveForm extends JComponent {
	
	AudioSample sample = null;
	BufferedImage wave = null;
	
	public void loadSound( AudioSample sample ){
		this.sample = sample;
		this.wave = null;
		Thread thread = new Thread( new CreateBitmap() );
		thread.start();
	}
	
	class CreateBitmap implements Runnable {
		
		public CreateBitmap(){
			
		}
		
		public void run() {
			int width = 1024;
			int height = 512;
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			g.setColor(Color.red);
	        float[] left = sample.getChannel(AudioSample.LEFT);
	        int y = height / 2;
	        float h = height / 4.0f;
	        for(int i = 0; i < left.length; i++ ){
	        	int x = (int)(( (float)width / (float)left.length) * i);
        		g.drawLine(x, y, x, (int)(y + left[i] * h));
	        }
			wave = image;
			repaint();
		}
	}
	
	@Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Rectangle rect = g.getClipBounds();
        if( rect != null && sample != null ){
        	float[] left = sample.getChannel(AudioSample.LEFT);
        	if( wave != null ){
        		int imgWidth = this.wave.getWidth();
        		int imgHeight = this.wave.getHeight();
        		g.drawImage(this.wave, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
        			0, 0, imgWidth, imgHeight, null);
        	}
        	g.setColor(Color.WHITE);
	        g.drawString("Number of samples: " + left.length, 10, 20);
	        g.drawString("Farme rate: " + sample.getFormat().getFrameRate(), 10, 40);
        }
    }
}
