package com.oxande.wavecleaner.filters;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import org.apache.logging.log4j.Logger;
import org.tritonus.share.sampled.file.AudioOutputStream;

import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.Recordable;
import ddf.minim.UGen;
import ddf.minim.spi.AudioOut;

public class RecorderOutput extends UGen implements Recordable {
	private static Logger LOG = LogFactory.getLog(RecorderOutput.class);
	public static final int BUFSIZE = 1000;
	
	private MultiChannelBuffer buf = new MultiChannelBuffer(BUFSIZE, Minim.STEREO);
	private int pos = 0;
	
	private List<AudioListener> listeners = new ArrayList<>();
	private AudioFormat format;
	private UGen audio;
	
	public RecorderOutput(AudioFormat format) {
		super();
		this.format = format;
	}
	
	
		@Override
		protected void addInput(UGen input) {
			audio = input;

					LOG.info("Filter {} attached to {}", audio, this);
			//		this.originalStream = ((AudioFilter)input).getOriginalStream();
			//	}
			
		}
	
	@Override
	protected void uGenerate(float[] channels) {
		buf.setSample(0, pos, channels[0]);
		buf.setSample(1, pos, channels[1]);
		
		if( ++pos == BUFSIZE ){
			float[] sampL = new float[BUFSIZE];
			float[] sampR = new float[BUFSIZE];
			System.arraycopy(buf.getChannel(0), 0, sampL, 0, BUFSIZE);
			System.arraycopy(buf.getChannel(1), 0, sampR, 0, BUFSIZE);
			for( AudioListener l : listeners ){
				l.samples(sampL, sampR);
			}
		}
	}

	@Override
	public void addListener(AudioListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(AudioListener listener) {
		listeners.remove(listener);
	}

	@Override
	public AudioFormat getFormat() {
		return this.format;
	}

	@Override
	public int type() {
		return Minim.STEREO;
	}

	@Override
	public int bufferSize() {
		return BUFSIZE;
	}


}
