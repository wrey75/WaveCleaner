package com.oxande.wavecleaner.filters;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.MultiChannelBuffer;
import ddf.minim.analysis.FFT;

/**
 * This filter is inspired by Craig Deforest and comes from 
 * <a href="https://github.com/audacity/audacity/blob/master/src/effects/ClickRemoval.cpp">Audacity 
 * code</a>.
 * 
 * <p>
 * Clicks are identified as small regions of high amplitude compared
 * to the surrounding chunk of sound. Anything sufficiently tall compared
 * to a large (2048 sample) window around it, and sufficiently narrow,
 * is considered to be a click.</p>
 * 
 * <p>The structure was largely stolen from Domonic Mazzoni's NoiseRemoval
 * module, and reworked for the NEW effect.</p>
 * 
 *
 */
public class ClickRemovalFilter extends AudioFilter {
	private static Logger LOG = LogFactory.getLog(ClickRemovalFilter.class);
	private final static int SEP = 2049;
	
	public static final String THRESHOLD = "thresold";
	public static final String SAMPLE_WIDTH = "sampleWidth";
	public static final String CLICK_WIDTH ="clickWidth";
	public static final String REMOVED = "removed";

	/**
	 * Create the decrackler.
	 * 
	 * @param iStream
	 *            the input stream.
	 */
	public ClickRemovalFilter() {
		super();
		this.addParameter(THRESHOLD, 1, 5, 0.25f, 2, v -> {
			NumberFormat formatter = new DecimalFormat("0");
			return formatter.format(v);
		});
		this.addParameter(SAMPLE_WIDTH, 0.5f, 5.0f, 1.0f, 0.1f, v -> {
			NumberFormat formatter = new DecimalFormat("0 ms");
			return formatter.format(v);
		});
		this.addParameter(CLICK_WIDTH, 0.0f, 0.25f, 0.01f, 0.01f, v -> {
			NumberFormat formatter = new DecimalFormat("0 ms");
			return formatter.format(v);
		});
		this.addParameter(REMOVED, 0, Float.MAX_VALUE, 0, 1, v -> {
			NumberFormat formatter = new DecimalFormat("0");
			return formatter.format(v);
		});
	}
	
	private int before_width = 0;
	private int after_width = 200;
	private int nbFound = 0;
	private int sep = SEP;
	float y[][] = new float[2][];
	float ret[][] = new float[2][];
	
	public float[][] nextSamples() {
		int sampleWidth = 8192; // (int)this.getControl(SAMPLE_WIDTH) / 2;
		int clickWidth = 5; // (int)this.getControl(CLICK_WIDTH) / 2;
		float factor = 2.5f;
		after_width = sampleWidth;
		int windowSize = sampleWidth * 2;
		
		float input[][] = loadSamples(sampleWidth, sampleWidth);
		for(int ch = 0; ch < 2; ch++){
			if(ret[ch] == null || ret[ch].length != sampleWidth){
				// Allocate the correct size!
				ret[ch] = new float[sampleWidth];
			}
			
			if(y[ch] == null || y[ch].length != sampleWidth * 2){
				// Allocate the correct size!
				float[] newBuf = new float[sampleWidth * 2];
				if( y[ch] != null ){
					// Copy the end of the queue...
					System.arraycopy(y[ch], 0, newBuf, 0, y[ch].length);
				}
				y[ch] = newBuf;
			}
			System.arraycopy(input[ch], 0, y[ch], 0, input[ch].length);
			
			
//			// Calculate average power for the sample...
//			double avg = 0;
//			for(int i = before_width; i < sampleWidth + before_width; i++){
//				// Calculate average power
//				avg += (y[ch][i] * y[ch][i]) / sampleWidth;
//			}
//			for(int i = before_width; i < sampleWidth + before_width; i++){
//				// Calculate average power
//				double instant = (y[ch][i] * y[ch][i]);
//				if(instant > avg * factor){
//					int first;
//					int last;
//					// found a click candidate...
//					// but ensure not a real sound 
//					int j = i - clickWidth / 2;
//					while( j < i + clickWidth / 2 && (y[ch][j] * y[ch][j]) < avg * 15.0 ){
//						j++;
//					}
//					first = j;
//					while( j < i + clickWidth / 2 && (y[ch][j] * y[ch][j]) > avg * 10.0 && Math.abs(y[ch][j]) > 0.1 ){
//						j++;
//					}
//					last = j;
//					while( j < i + clickWidth / 2 && (y[ch][j] * y[ch][j]) < avg * 15.0 ){
//						j++;
//					}
//					if( j == i + clickWidth / 2 && last > first ){
//						LOG.info("found click at {} on {} samples", i, (last - first));
//						// Only one pic found on the full size means not a short frequency
//						// like a drum.
//						nbFound++;
//						
////						for(int k = first; k < last; k++){
////							y[ch][k] = 0;
////						}
//
//						// START OF CLEANING
//						
//						int fftSize = nextPower2(last - first);
//						if( fftSize == 1 ){
//							// Very short cut, use average
//							y[ch][first] = (y[ch][first - 1] + y[ch][first + 1]) / 2.0f; 
//						}
//						else {
//							// Get the FFT before & after...
//							float[] sampleBefore = new float[fftSize];
//							System.arraycopy(y[ch], first - fftSize, sampleBefore, 0, fftSize);
//							FFT fft_before = new FFT(fftSize, sampleRate());
//							fft_before.forward(sampleBefore);
//							float[] real1 = fft_before.getSpectrumReal();
//							float[] im1 = fft_before.getSpectrumImaginary();
//							
//							float[] sampleAfter = new float[fftSize];
//							System.arraycopy(y[ch], last, sampleAfter, 0, fftSize);
//							FFT fft_after = new FFT(fftSize, sampleRate());
//							fft_after.forward(sampleAfter);
//							float[] real2 = fft_after.getSpectrumReal();
//							float[] im2 = fft_after.getSpectrumImaginary();
//							
//							float[] im = new float[fftSize];
//							float[] real = new float[fftSize];
//							for (int k = 0; k < fftSize; k++){
//								real[k] = (real1[k] + real2[k]) / 2.0f;
//								im[k] = (im[k] + im[k]) / 2.0f;
//							}
//							
//							FFT mix = new FFT(fftSize, sampleRate());
//							float mixed[] = new float[fftSize];
//							mix.inverse(real, im, mixed);
//							int size = (int)((last-first) / 2);
//							int start = (last+first) /2 - fftSize / 2;
//							for(int k = 0; k < fftSize; k++ ){
//								if( k + start < first) {
//									y[ch][k+start] = (mixed[k] + input[ch][k+start]) / 2.0f;
//								} else if( k + start > last) {
//									y[ch][k+start] = (mixed[k] + y[ch][k+start]) / 2.0f;
//								} else{
//									y[ch][k+start] = mixed[k];
//								}
//							}
//						}
//						// END OF CLEANING
//					}
//				}
//			}
			
			// Save results
			removeClicks(y[ch], sampleWidth * 2);
			System.arraycopy(y[ch], 0, ret[ch], 0, sampleWidth);
			System.arraycopy(y[ch], sampleWidth, y[ch], 0, y[ch].length - sampleWidth );
		}
		
		before_width = after_width;
		return ret;
	}
	
	/*
	 * 
	 	public static final String THRESHOLD = "thresold";
	public static final String WIDTH = "width";

	int windowSize;
	int sep = 2049;

	
	public ClickRemovalFilter() {
		super();
		this.addParameter(THRESHOLD, 0, 900, 200, 5, v -> {
			NumberFormat formatter = new DecimalFormat("0");
			return formatter.format(v);
		});
		this.addParameter(WIDTH, 0, 40, 20, 1, v -> {
			NumberFormat formatter = new DecimalFormat("0");
			return formatter.format(v);
		});

		windowSize = 8192;
		sep = 2049;
	}

	protected MultiChannelBuffer processNext(MultiChannelBuffer buf) {
		buf.setBufferSize(windowSize);
		float[][] samples = loadSamples(windowSize);
		buf.setChannel(0, samples[0]);
		buf.setChannel(1, samples[1]);
		process(buf);
		return buf;
	}
    */
	
	protected void removeClicks(float[] buffer, int len ) {
		int mClickWidth = 30; // (int)(this.getControl(CLICK_WIDTH) / sampleRate());
		int mThresholdLevel = 400;
		int left = 0;
		float msw;
		int ww;
		int sep = SEP;
		int s2 = sep / 2;
		float[] ms_seq = new float[len];
		float[] b2 = new float[len];

		for( int i = 0; i < len; i++){
			b2[i] = buffer[i]*buffer[i];
		}
		
	
		//
		// Shortcut for rms - multiple passes through b2,
		// accumulating as we go.
		//
		for (int i = 0; i < len; i++) {
			ms_seq[i] = b2[i];
		}

		int i;
		for (i = 1; i < sep; i *= 2) {
			for (int j = 0; j < len - i; j++) {
				ms_seq[j] += ms_seq[j + i];
			}
		}

		// Cheat by truncating sep to next-lower power of two... 
		sep = i;

		for (i = 0; i < len - sep; i++) {
			ms_seq[i] /= sep;
		}

		
		//
		// ww runs from about 4 to mClickWidth. wrc is the reciprocal;
		// chosen so that integer roundoff doesn't clobber us.
		//
		int wrc;
		for (wrc = mClickWidth / 4; wrc >= 1; wrc /= 2) {
			ww = mClickWidth / wrc;

			for (i = 0; i < len - sep; i++) {
				msw = 0;
				for (int j = 0; j < ww; j++) {
					msw += b2[i + s2 + j];
				}
				msw /= ww;

				if (msw >= mThresholdLevel * ms_seq[i] / 10.0) {
					if (left == 0) {
						LOG.info("left is zero?");
						left = i + s2;
					}
				} else {
					if (left != 0 && (int)(i - left + s2) <= ww * 2) {
						float lv = buffer[left];
						float rv = buffer[i + ww + s2];
						LOG.info("Found click");
						for (int j = left; j < i + ww + s2; j++) {
							float old = buffer[j];
							buffer[j] = (rv * (j - left) + lv * (i + ww + s2 - j)) / (float) (i + ww + s2 - left);
							LOG.info("buff[{}] from {} to {}.", j, old, buffer[j]);
							b2[j] = buffer[j] * buffer[j];
						}
						left = 0;
					} else if (left != 0) {
						left = 0;
					}
				}
			}
		}
	}

}
