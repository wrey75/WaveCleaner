package com.oxande.wavecleaner.filters;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.MultiChannelBuffer;
import ddf.minim.analysis.FFT;

/**
 * This is a brand new version. There is no link with Audacity.
 * The code below is based on a pretty simple way to do:
 * - we find a very important "click" in the record (must be twice
 * the RMS) on a very short period.
 * - we completely remove the "pic".
 * - we get the FFT before the click and FFT after the click.
 * - we mix the 2 FFT found
 * - we put this mix inside the removed sound.
 * 
 * This should be the better removing technique.
 * 
 * @author wrey75
 *
 */
public class ClickRemovalFilter2 extends AudioFilter {
	private static Logger LOG = LogFactory.getLog(ClickRemovalFilter.class);
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
	public ClickRemovalFilter2() {
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
	float y[][] = new float[2][];
	float ret[][] = new float[2][];
	
	public float[][] nextSamples() {
		int sampleWidth = 24000; // (int)this.getControl(SAMPLE_WIDTH) / 2;
		int clickWidth = 5; // (int)this.getControl(CLICK_WIDTH) / 2;
		float factor = 2.5f;
		
		float input[][] = loadSamples(sampleWidth, after_width);
		for(int ch = 0; ch < 2; ch++){
			if(ret[ch] == null || ret[ch].length != sampleWidth){
				// Allocate the correct size!
				ret[ch] = new float[sampleWidth];
			}
			
			if(y[ch] == null || y[ch].length != sampleWidth + before_width + after_width){
				// Allocate the correct size!
				float[] newBuf = new float[sampleWidth + before_width + after_width];
				if( y[ch] != null ){
					// Copy the end of the queue...
					System.arraycopy(y[ch], 0, newBuf, 0, before_width);
				}
				y[ch] = newBuf;
			}
			System.arraycopy(input[ch], 0, y[ch], before_width, input[ch].length);
			
			// Calculate average power for the sample...
			double avg = 0;
			for(int i = before_width; i < sampleWidth + before_width; i++){
				// Calculate average power
				avg += (y[ch][i] * y[ch][i]) / sampleWidth;
			}
			for(int i = before_width; i < sampleWidth + before_width; i++){
				// Calculate average power
				double instant = (y[ch][i] * y[ch][i]);
				if(instant > avg * factor){
					int first;
					int last;
					// found a click candidate...
					// but ensure not a real sound 
					int j = i - clickWidth / 2;
					while( j < i + clickWidth / 2 && (y[ch][j] * y[ch][j]) < avg * 15.0 ){
						j++;
					}
					first = j;
					while( j < i + clickWidth / 2 && (y[ch][j] * y[ch][j]) > avg * 10.0 && Math.abs(y[ch][j]) > 0.1 ){
						j++;
					}
					last = j;
					while( j < i + clickWidth / 2 && (y[ch][j] * y[ch][j]) < avg * 15.0 ){
						j++;
					}
					if( j == i + clickWidth / 2 && last > first ){
						LOG.info("found click at {} on {} samples", i, (last - first));
						// Only one pic found on the full size means not a short frequency
						// like a drum.
						nbFound++;
						
//						for(int k = first; k < last; k++){
//							y[ch][k] = 0;
//						}

						// START OF CLEANING
						
						int fftSize = nextPower2(last - first);
						if( fftSize == 1 ){
							// Very short cut, use average
							y[ch][first] = (y[ch][first - 1] + y[ch][first + 1]) / 2.0f; 
						}
						else {
							// Get the FFT before & after...
							float[] sampleBefore = new float[fftSize];
							System.arraycopy(y[ch], first - fftSize, sampleBefore, 0, fftSize);
							FFT fft_before = new FFT(fftSize, sampleRate());
							fft_before.forward(sampleBefore);
							float[] real1 = fft_before.getSpectrumReal();
							float[] im1 = fft_before.getSpectrumImaginary();
							
							float[] sampleAfter = new float[fftSize];
							System.arraycopy(y[ch], last, sampleAfter, 0, fftSize);
							FFT fft_after = new FFT(fftSize, sampleRate());
							fft_after.forward(sampleAfter);
							float[] real2 = fft_after.getSpectrumReal();
							float[] im2 = fft_after.getSpectrumImaginary();
							
							float[] im = new float[fftSize];
							float[] real = new float[fftSize];
							for (int k = 0; k < fftSize; k++){
								real[k] = (real1[k] + real2[k]) / 2.0f;
								im[k] = (im[k] + im[k]) / 2.0f;
							}
							
							FFT mix = new FFT(fftSize, sampleRate());
							float mixed[] = new float[fftSize];
							mix.inverse(real, im, mixed);
							int size = (int)((last-first) / 2);
							int start = (last+first) /2 - fftSize / 2;
							for(int k = 0; k < fftSize; k++ ){
								if( k + start < first) {
									y[ch][k+start] = (mixed[k] + input[ch][k+start]) / 2.0f;
								} else if( k + start > last) {
									y[ch][k+start] = (mixed[k] + y[ch][k+start]) / 2.0f;
								} else{
									y[ch][k+start] = mixed[k];
								}
							}
						}
						// END OF CLEANING
					}
				}
			}
			
			// Save results
			System.arraycopy(y[ch], before_width, ret[ch], 0, sampleWidth);
			System.arraycopy(y[ch], before_width + sampleWidth, y[ch], 0, after_width);
		}
		
		before_width = after_width;
		return ret;
	}
	
}
