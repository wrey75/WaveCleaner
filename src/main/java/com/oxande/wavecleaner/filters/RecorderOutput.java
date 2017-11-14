package com.oxande.wavecleaner.filters;

import javax.sound.sampled.AudioFormat;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.ListenerManager;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioListener;
import ddf.minim.Minim;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.Recordable;
import ddf.minim.UGen;

public class RecorderOutput extends UGen implements Recordable {
	private static Logger LOG = LogFactory.getLog(RecorderOutput.class);
	public static final int BUFSIZE = 1000;

	private MultiChannelBuffer buf = new MultiChannelBuffer(BUFSIZE, Minim.STEREO);
	private int pos = 0;

	private ListenerManager<AudioListener> listenerManager = new ListenerManager<AudioListener>();
	private AudioFormat format;
//	private UGen audio;

	public RecorderOutput(AudioFormat format) {
		super();
		this.format = format;
		new UGenInput(InputType.AUDIO);
	}

//	@Override
//	protected void addInput(UGen input) {
//		audio = input;
//
//		LOG.info("Filter {} attached to {}", audio, this);
//		// this.originalStream = ((AudioFilter)input).getOriginalStream();
//		// }
//
//	}

	@Override
	protected void uGenerate(float[] channels) {
		buf.setSample(0, pos, channels[0]);
		buf.setSample(1, pos, channels[1]);

		if (++pos == BUFSIZE) {
			final float[] sampL = new float[BUFSIZE];
			final float[] sampR = new float[BUFSIZE];
			System.arraycopy(buf.getChannel(0), 0, sampL, 0, BUFSIZE);
			System.arraycopy(buf.getChannel(1), 0, sampR, 0, BUFSIZE);
			listenerManager.send((listener) -> {
				// Call the listener
				listener.samples(sampL, sampR);
			});
		}
	}

	@Override
	public void addListener(AudioListener listener) {
		listenerManager.add(listener);
	}

	@Override
	public void removeListener(AudioListener listener) {
		listenerManager.remove(listener);
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
