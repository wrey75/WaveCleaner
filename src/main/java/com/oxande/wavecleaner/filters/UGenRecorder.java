package com.oxande.wavecleaner.filters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.tritonus.share.sampled.AudioFileTypes;

import ddf.minim.UGen;


public class UGenRecorder extends UGen {

	AudioInputStream inputStream;
	UGenInputStream in;
	
	private class UGenInputStream extends InputStream {
		
		// No more than 5 seconds in the queue.
		BlockingDeque<Byte> queue = new LinkedBlockingDeque<>(48000 * 5 * Float.BYTES * 2);

		@Override
		public int read() throws IOException {
			try {
				return (int)queue.takeFirst();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return -1;
		}
		
		public void push( float sampL, float sampR ) throws InterruptedException {
			byte[] buf = ByteBuffer.allocate(Float.BYTES * 2)
					.putFloat(sampL)
					.putFloat(sampR).array();
			for(byte b: buf){
				queue.putLast(b);
			}
		}
		
	}

	public UGenRecorder() {
		new UGenInput(InputType.AUDIO);
	}
	
	@Override
	protected void uGenerate(float[] channels) {
		try {
			in.push(channels[0], channels[1]);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void save(String fileName) throws IOException {
		float sampleRate = sampleRate();
		long nbSamples = (long)(sampleRate * 60.0);
		in = new UGenInputStream();

		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, sampleRate, Float.SIZE, 2, Float.BYTES * 2, sampleRate(), false);
		inputStream = new AudioInputStream(in, format, nbSamples );
		OutputStream out = new FileOutputStream(fileName);

		new Thread( () -> {
			float[] samples = new float[2];
			for(long i = 0; i < nbSamples; i++){
				this.tick(samples);
			}
		});
		AudioFileFormat.Type fileType = AudioFileTypes.getType("MP3");
		if( fileType == null ){
			fileType = AudioFileFormat.Type.WAVE;
		}
		AudioSystem.write(inputStream, fileType, out);
	}

}
