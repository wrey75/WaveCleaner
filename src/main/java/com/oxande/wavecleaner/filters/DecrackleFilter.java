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

import ddf.minim.MultiChannelBuffer;
import ddf.minim.spi.AudioStream;

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
 * @author AlisterH
 *
 */
public class DecrackleFilter extends AudioFilter {
	private static Logger LOG = LogFactory.getLog(DeclickerFilter.class);

	// Store the one-round samples.
	private float samples[][] = new float[2][];
	
	/**
	 * Create the decrackler.
	 * 
	 * @param iStream
	 *            the input stream.
	 */
	public DecrackleFilter(AudioStream iStream) {
		super(iStream);
	}

	int nmax = 2000; // decrackle_window;
	int width = 3; // decrackle_average;
	// int first_sample = 0; // First sample of the song
	// int last_sample = 5000000; // Last sample of the song!
	// int n_samples = 300; <-- Only for gwc (total number of samples in th original file)
	double factor = 0.2; // decrackle_level

	/*
	 * CALL IN THE gwc.c: do_decrackle(&prefs, first, last,
	 * audio_view.channel_selection_mask, decrackle_level, decrackle_window,
	 * decrackle_average);
	 * 
	 * NOTES: - decrackle_level = 0.2 by default
	 */

	/**
	 * Set the level of decrackling.
	 * 
	 * @param factor
	 *            the factor should be contained between 0.0 and 1.0
	 */
	public synchronized void setLevel(double factor) {
		this.factor = factor;
	}

	/**
	 * Set the average.
	 * 
	 * @param width
	 *            the average to use when processing.
	 */
	public synchronized void setAverage(int width) {
		this.width = width;
	}

	/**
	 * Set the window for decrackling.
	 * 
	 * @param window
	 *            the window for decrackling (3000 is good).
	 */
	public synchronized void setWindow(int window) {
		this.nmax = window;
	}

	private float[][] y = new float[2][];
	private float[][] y_hat = new float[2][];
	private float[] y_hat_tmp = null;
	
	// int do_decrackle(struct sound_prefs *pPrefs, long first_sample,
	// long last_sample, int channel_mask, double factor, gint nmax, gint width)
	/**
	 * Process to the decrackling of one buffer.
	 */
	public float[][] nextSamples() {
		// long fin; // final in the original code but not possible in JAVA!
		// long last_read;
		// int ch; // channel
		// double scaled_factor;
		// double absum;

		// int scount;

		// The decrackling full size? Why the width is multiplied by 2?
		

		int asize = nmax + 2 * width;
		if( y_hat_tmp == null ){
			// First round, initialize the arrays
			y_hat_tmp = new float[asize];
			for (int ch = 0; ch < 2; ch++) {
				y[ch] = new float[asize];
				y_hat[ch] = new float[asize];
			}
		}
		
		int n = nmax + width; // We necessary load the size of the buffer!
		int[] cflag = new int[asize];
		// scaled_factor = factor; // * pPrefs -> max_allowed;

		// push_status_text("Decrackling audio") ;
		// update_progress_bar(0.0,PROGRESS_UPDATE_INTERVAL,TRUE) ;
		
		// int current = 0;
		// WE DO NOT LOOP - ONE ONE DECRACKLING BY CYCLE
		// for (int current = first_sample; current < last_sample; current += nmax) {
			// Forget the progress bar!
			// float p = (float) (current - first_sample) / (float) (last_sample - first_sample);
			// update_progress_bar(p,PROGRESS_UPDATE_INTERVAL,FALSE) ;

			// int fin = /* current + */ nmax - 1; // was "final" but not possible in JAVA
//			if (fin > last_sample){
//				fin = last_sample;
//			}
			// last_read = fin + width;
			// if (last_read >= n_samples) last_read = n_samples - 1; <-- Only
			// for gwc

			// LOG.debug("[{} - {}], start  @ {}, end @ {} ({})\n", first_sample, last_sample, current, fin, last_read);
			// LOG.debug("{} {} {}\n", n_samples, pPrefs->bits, pPrefs->stereo);

			// READ DATA: int read_fft_real_wavefile_data(fftw_real left[], fftw_real right[], long first, long last)
			/*
			 * n = read_fft_real_wavefile_data(y[0]+width, y[1]+width, current, last_read);
			 */
			float[][] input = loadSamples(nmax, width);
			for (int ch = 0; ch < 2; ch++) {
				Validate.isTrue(input[ch].length == n);
				System.arraycopy(input[ch], 0, y[ch], width, n);
				
			// LOG.debug("Got {} samples", n);

//			// Fill anything beyond the end of the file with zeros
//			if (n < nmax + width /* - current */) { 
//				// NEVER HAPPENS!
//				LOG.error("Filling");
//				for (int i = /* current + */ n; i < nmax; i++) {
//					y[0][i] = 0.0f;
//					y[1][i] = 0.0f;
//				}
//			}
			
				int fixed = 0;
				
				// We are ALWAYS in STEREO...
				if (((ch+1) & 0x03) != 0) { // Always true, just keep old code
					int scount = 0;
					double absum = 0.0;
					for (int i = width + 1; i < n; i++) {
						/* absum += fabs(y[ch][i]); */
						absum += Math.abs(y[ch][i] - y[ch][i-1]);
						scount++;
					}
					double scaled_factor = factor * absum / scount;

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
							} else if (dy2dx < -scaled_factor) {
								// We found one!
								cflag[i-1] = 1;
							} else {
								cflag[i-1] = 0;
							}
							
						}
					}

					for (int i = width; i < n; i++) {
						int first, last;
						int w = width;
						int flag = 0;
						double sum = 0.0;
						double sumwgt = 0.0;

						first = i - w;
						if (first < 0) {
							first = 0;
							LOG.warn("first < 0");
						}
						last = i + w;
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
				} else {
					for (int i = 0; i < n + width; i++) {
						y_hat[ch][i] = y[ch][i];
					}
				}
					// LOG.debug("Flagged {}% ({} times on {}), channel {}", (int)((float)fixed / asize * 100), fixed, asize, (ch == 0 ? "LEFT" : "RIGHT"));
			} // next channel

			// WRITE BACK: int write_fft_real_wavefile_data(fftw_real left[], fftw_real right[], long first, long last)
			// write_fft_real_wavefile_data(y_hat[0]+width, y_hat[1]+width, current, final) ;
			
			// We MUST send back ONLY the first "n samples"
			float ret[][] = new float[2][nmax];
			System.arraycopy(y_hat[0], width, ret[0], 0, nmax);
			System.arraycopy(y_hat[1], width, ret[1], 0, nmax);
//			for(int i = 0; i < nmax; i++){
//				if( ret[1][i] != y_hat[1][i+width] ){
//					LOG.debug("DIFF AT {}: {} vs {}", i, y_hat[0][i], input[0][i]);
//				}
//			}
			// Validate.isTrue(ret[0].length == input[0].length);

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
			
		// }  // NEXT SAMPLES

		// update_progress_bar(0.0,PROGRESS_UPDATE_INTERVAL,TRUE) ;

		// pop_status_text() ;

		// main_redraw(FALSE, TRUE) ;

		// for (ch = 0; ch < 2; ch++) {
		// free(y[ch]) ;
		// free(y_hat[ch]);
		// }
		// free(y_hat_tmp);
		// free(cflag);

			
		
		return ret;
	}
}
