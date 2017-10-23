/*****************************************************************************
*   Gnome Wave Cleaner Version 0.19
*   Copyright (C) 2001 Jeffrey J. Welty
*   
*   This program is free software; you can redistribute it and/or
*   modify it under the terms of the GNU General Public License
*   as published by the Free Software Foundation; either version 2
*   of the License, or (at your option) any later version.
*   
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*   
*   You should have received a copy of the GNU General Public License
*   along with this program; if not, write to the Free Software
*   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*******************************************************************************/
package com.oxande.wavecleaner.filters;

import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;

/**
 * The decrackle filter comes from the <a href=
 * "https://github.com/AlisterH/gwc/blob/150b1f937592e1b6d69601748ac5ab4cd21540b9/decrackle.c">decrackle.c</a>
 * version of 2014, 4th of December.
 * 
 * <p>
 * NOTES:
 * </p>
 * <ul>
 * <li>"fftw_real" in the original file are translated to float (everything is
 * based on floats, 25 bits of precision)</li>
 * <li>The recovery of the crackling seems not very optimized at the first 
 * read. More sophisticated interpolation algorithms seems also interesting.
 * Mainly because the algorithm does not include the sampling rate (from 48k to 192k).</li> 
 * </ul>
 * 
 * @author Jeff Welty (original author)
 *
 */
public class DecrackleFilter extends AudioFilter {
	private static Logger LOG = LogFactory.getLog(DeclickerFilter.class);

	public static final String WINDOW = "window";
	public static final String AVERAGE = "average";
	public static final String FACTOR = "factor";
	
	/**
	 * Create the decrackler.
	 * 
	 * @param iStream
	 *            the input stream.
	 */
	public DecrackleFilter() {
		super();
		this.addParameter(WINDOW, INT_PARAM, 3, 10000, 2000);
		this.addParameter(FACTOR, FLOAT_PARAM, 0.01f, 1.0f, 0.2f).setFactor(10.0f);
		this.addParameter(AVERAGE, INT_PARAM, 1, 10, 3);
	}


	private float[][] y = new float[2][];
	private float[][] y_hat = new float[2][];
	private float[] y_hat_tmp = null;
	
	private int old_width = 1;
	
	/**
	 * Process to the decrackling of one buffer.
	 */
	public float[][] nextSamples() {
		// Load control values
		int width = Math.max(1, (int)this.getControl(AVERAGE));
		int nmax = Math.max(3, (int)this.getControl(WINDOW));
		
		int asize = nmax + old_width + width;
		if( y_hat_tmp == null || y_hat_tmp.length < asize ){
			// Inputs have changed or first round
			y_hat_tmp = newFloatArray(y_hat_tmp, asize);
			for (int ch = 0; ch < 2; ch++) {
				y[ch] = newFloatArray(y[ch], asize);
				y_hat[ch] = newFloatArray(y_hat[ch], asize);
			}
		}
		
		int n = nmax + old_width; 
		int[] cflag = new int[asize];
		
		int fixed = 0;
		float[][] input = loadSamples(nmax, width);
		for (int ch = 0; ch < 2; ch++) {
			Validate.isTrue(input[ch].length == nmax+width);
			System.arraycopy(input[ch], 0, y[ch], old_width, nmax+width);
			
			int scount = 0;
			double absum = 0.0;
			for (int i = old_width + 1; i < n; i++) {
				/* absum += fabs(y[ch][i]); */
				absum += Math.abs(y[ch][i] - y[ch][i-1]);
				scount++;
			}
			double scaled_factor = this.getControl(FACTOR) * absum / scount;

			for (int i = 0; i < n + width; i++) {
				double dy0 = 0;
				double dy1 = 0;
				double dy2dx = 0;

				y_hat[ch][i] = y[ch][i];
				if (i < 2) {
					/* wgt[i] = 1.0 ; */
					cflag[i] = 0;
				} else {
					dy0 = y_hat[ch][i-1] - y_hat[ch][i-2];
					dy1 = y[ch][i-0] - y_hat[ch][i-1];
					dy2dx = dy1 - dy0;

					if (dy2dx > scaled_factor) {
						// We found one!
						cflag[i-1] = 1;
						fixed++;
					} else if (dy2dx < -scaled_factor) {
						// We found one!
						cflag[i-1] = 1;
						fixed++;
					} else {
						cflag[i-1] = 0;
					}
					
				}
			}

			for (int i = old_width; i < n; i++) {
				int first, last;
				int flag = 0;
				double sum = 0.0;
				double sumwgt = 0.0;

				first = i - old_width;
				if (first < 0) {
					first = 0;
					LOG.warn("first < 0");
				}
				last = i + width;
				if (last > asize - 1) {
					last = asize - 1;
					LOG.warn("last > asize");
				}

				for (int j = first; j <= last; j++) {
					sum += y_hat[ch][j];
					sumwgt++;
				}
				flag = cflag[i - 1] | cflag[i] | cflag[i + 1];

				if (cflag[i] != 0) {
					fixed++;
					y_hat_tmp[i] = (float) (sum / sumwgt);
				} else if (flag != 0) {
					y_hat_tmp[i] = (float) ((sum / sumwgt + y_hat[ch][i]) / 2.0);
				} else {
					y_hat_tmp[i] = y_hat[ch][i];
				}
			}

			for (int i = 0; i < n + width; i++) {
				y_hat[ch][i] = y_hat_tmp[i];
			}
		

			// LOG.debug("Flagged {}% ({} times on {}), channel {}", (int)((float)fixed / asize * 100), fixed, asize, (ch == 0 ? "LEFT" : "RIGHT"));
		} // next channel

		// We MUST send back ONLY the first nmax samples!
		float ret[][] = new float[2][nmax];
		System.arraycopy(y_hat[0], old_width, ret[0], 0, nmax);
		System.arraycopy(y_hat[1], old_width, ret[1], 0, nmax);

		/*
		 * Copy the last width points of this window to the "pre-window" of
		 * the next window
		 */
		for (int i = 0; i < width; i++) {
			for (int ch = 0; ch < 2; ch++) {
				y_hat[ch][i] = y_hat[ch][i + n - width];
				y[ch][i] = y[ch][i + n - width];
			}
		}
		
		LOG.debug("Changes applied: {}", fixed);
		old_width = width; // Now transfer the new value.
		return ret;
	}
}
