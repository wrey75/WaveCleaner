package com.oxande.wavecleaner.filters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;


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
 * module, and reworked for the new effect. Especially, the click
 * detection has been kept but removal use a parametric reconstruction.</p>
 * 
 *
 */
public class ClickRemovalFilter extends AudioFilter {
	private static Logger LOG = LogFactory.getLog(ClickRemovalFilter.class);
	private final static int SEP = 2049;
	
	public static final String THRESHOLD = "thresold";
//	public static final String SAMPLE_WIDTH = "sampleWidth";
	public static final String CLICK_WIDTH ="clickWidth";
	public static final String REMOVED = "removed";

	/**
	 * Performs spline interpolation given a set of control points.
	 * Code from <a href="https://gist.github.com/lecho/7627739">https://gist.github.com/</a>.
	 * 
	 */
	static class SplineInterpolator {

		private final List<Float> mX;
		private final List<Float> mY;
		private final float[] mM;

		private SplineInterpolator(List<Float> x, List<Float> y, float[] m) {
			mX = x;
			mY = y;
			mM = m;
		}

		/**
		 * Creates a monotone cubic spline from a given set of control points.
		 * 
		 * The spline is guaranteed to pass through each control point exactly. Moreover, assuming the control points are
		 * monotonic (Y is non-decreasing or non-increasing) then the interpolated values will also be monotonic.
		 * 
		 * This function uses the Fritsch-Carlson method for computing the spline parameters.
		 * http://en.wikipedia.org/wiki/Monotone_cubic_interpolation
		 * 
		 * @param x
		 *            The X component of the control points, strictly increasing.
		 * @param y
		 *            The Y component of the control points
		 * @return
		 * 
		 * @throws IllegalArgumentException
		 *             if the X or Y arrays are null, have different lengths or have fewer than 2 values.
		 */
		public static SplineInterpolator createMonotoneCubicSpline(List<Float> x, List<Float> y) {
			if (x == null || y == null || x.size() != y.size() || x.size() < 2) {
				throw new IllegalArgumentException("There must be at least two control "
						+ "points and the arrays must be of equal length.");
			}

			final int n = x.size();
			float[] d = new float[n - 1]; // could optimize this out
			float[] m = new float[n];

			// Compute slopes of secant lines between successive points.
			for (int i = 0; i < n - 1; i++) {
				float h = x.get(i + 1) - x.get(i);
				if (h <= 0f) {
					throw new IllegalArgumentException("The control points must all "
							+ "have strictly increasing X values.");
				}
				d[i] = (y.get(i + 1) - y.get(i)) / h;
			}

			// Initialize the tangents as the average of the secants.
			m[0] = d[0];
			for (int i = 1; i < n - 1; i++) {
				m[i] = (d[i - 1] + d[i]) * 0.5f;
			}
			m[n - 1] = d[n - 2];

			// Update the tangents to preserve monotonicity.
			for (int i = 0; i < n - 1; i++) {
				if (d[i] == 0f) { // successive Y values are equal
					m[i] = 0f;
					m[i + 1] = 0f;
				} else {
					float a = m[i] / d[i];
					float b = m[i + 1] / d[i];
					float h = (float) Math.hypot(a, b);
					if (h > 9f) {
						float t = 3f / h;
						m[i] = t * a * d[i];
						m[i + 1] = t * b * d[i];
					}
				}
			}
			return new SplineInterpolator(x, y, m);
		}

		/**
		 * Interpolates the value of Y = f(X) for given X. Clamps X to the domain of the spline.
		 * 
		 * @param x
		 *            The X value.
		 * @return The interpolated Y = f(X) value.
		 */
		public float interpolate(float x) {
			// Handle the boundary cases.
			final int n = mX.size();
			if (Float.isNaN(x)) {
				return x;
			}
			if (x <= mX.get(0)) {
				return mY.get(0);
			}
			if (x >= mX.get(n - 1)) {
				return mY.get(n - 1);
			}

			// Find the index 'i' of the last point with smaller X.
			// We know this will be within the spline due to the boundary tests.
			int i = 0;
			while (x >= mX.get(i + 1)) {
				i += 1;
				if (x == mX.get(i)) {
					return mY.get(i);
				}
			}

			// Perform cubic Hermite spline interpolation.
			float h = mX.get(i + 1) - mX.get(i);
			float t = (x - mX.get(i)) / h;
			return (mY.get(i) * (1 + 2 * t) + h * mM[i] * t) * (1 - t) * (1 - t)
					+ (mY.get(i + 1) * (3 - 2 * t) + h * mM[i + 1] * (t - 1)) * t * t;
		}

		// For debugging.
		@Override
		public String toString() {
			StringBuilder str = new StringBuilder();
			final int n = mX.size();
			str.append("[");
			for (int i = 0; i < n; i++) {
				if (i != 0) {
					str.append(", ");
				}
				str.append("(").append(mX.get(i));
				str.append(", ").append(mY.get(i));
				str.append(": ").append(mM[i]).append(")");
			}
			str.append("]");
			return str.toString();
		}
	}
	
	/**
	 * Create the decrackler.
	 * 
	 * @param iStream
	 *            the input stream.
	 */
	public ClickRemovalFilter() {
		super();
		this.addParameter(THRESHOLD, 0, 900, 200, 5, v -> {
			NumberFormat formatter = new DecimalFormat("0");
			return formatter.format(v);
		});
//		this.addParameter(SAMPLE_WIDTH, 0.5f, 5.0f, 1.0f, 0.1f, v -> {
//			NumberFormat formatter = new DecimalFormat("0 ms");
//			return formatter.format(v);
//		});
		this.addParameter(CLICK_WIDTH, 0.0f, 0.001f, 0.0005f, 0.00001f, v -> {
			NumberFormat formatter = new DecimalFormat("0.00 ms");
			return formatter.format(v * 1000);
		});
		this.addParameter(REMOVED, 0, Float.MAX_VALUE, 0, 1, v -> {
			NumberFormat formatter = new DecimalFormat("0");
			return formatter.format(v);
		});
	}
	
//	private int before_width = 0;
	private int after_width = 200;
	private int nbFound = 0;
//	private int sep = SEP;
	float y[][] = new float[2][];
	float ret[][] = new float[2][];
	
	public float[][] nextSamples() {
		int sampleWidth = 8192; // (int)this.getControl(SAMPLE_WIDTH) / 2;
		// int clickWidth = 5; // (int)this.getControl(CLICK_WIDTH) / 2;
		// float factor = 2.5f;
		after_width = sampleWidth;
		// int windowSize = sampleWidth * 2;
		
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
		
//		before_width = after_width;
		return ret;
	}
	
	/**
	 * Remove the clicks found in the buffer.
	 * 
	 * @param buffer the buffer
	 * @param len the length of the buffer
	 */
	protected void removeClicks(float[] buffer, int len ) {
		int mClickWidth = this.getSampleControl(CLICK_WIDTH);
		int mThresholdLevel = this.getIntControl(THRESHOLD);
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
		int found = 0;
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
						LOG.info("Remove click...");
						if( left < 20 || (i + ww + s2 - left) < 1 ){
							// AUDACITY_METHOD:
							for (int j = left; j < i + ww + s2; j++) {
								float old = buffer[j];
								buffer[j] = (rv * (j - left) + lv * (i + ww + s2 - j)) / (float) (i + ww + s2 - left);
								LOG.info("AUDACITY: buff[{}] from {} to {}.", j, old, buffer[j]);
								b2[j] = buffer[j] * buffer[j];
							}	
						}
						else {
							int nb = Math.min(left, (int)(sampleRate() * 0.001f)); // use about 50 samples at 48000kHz 
							List<Float> xArray = new ArrayList<Float>();
							List<Float> yArray = new ArrayList<Float>();
							for (int j = left - nb; j < left; j++) {
								xArray.add((float) j);
								yArray.add(buffer[j]);
							}	
							for (int j = 0; j < nb; j++){
								int x = i + ww + s2 + j;
								xArray.add((float)x);
								yArray.add(buffer[x]);
							}
							SplineInterpolator interpolator = SplineInterpolator.createMonotoneCubicSpline(xArray, yArray);
							
							// Interpolate
							float power_0 = 0;
							float power_1 = 0;
							float power_2 = 0;
							for (int j = left; j < i + ww + s2; j++) {
								float old = buffer[j];
								float v1 = (rv * (j - left) + lv * (i + ww + s2 - j)) / (float) (i + ww + s2 - left);
								float v2 = interpolator.interpolate((float)j);
								power_0 += buffer[j] * buffer[j];
								power_1 += (v1 * v1);
								power_2 += (v2 * v2);
								// LOG.info("buff[{}] from {} to v1={} and v2={}.", j, old, v1, v2);
								buffer[j] = v2;
								b2[j] = buffer[j] * buffer[j];
							}	
							LOG.info("Removed click at {} (len={}) removed from POWER {} to {} versus {}.", left,  i + ww + s2 - left, power_0, power_1, power_2);
						}
						
						found++;
						left = 0;
					} else if (left != 0) {
						left = 0;
					}
				}
			}
		}
		this.addToControl(REMOVED, found);
	}

}
