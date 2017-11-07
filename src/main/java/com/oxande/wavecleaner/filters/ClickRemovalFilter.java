package com.oxande.wavecleaner.filters;

import ddf.minim.MultiChannelBuffer;

public class ClickRemovalFilter extends AudioFilter {

	public static final String THRESHOLD = "thresold";
	public static final String WIDTH = "width";

	int windowSize;
	int sep = 2049;

	/**
	 * Create the decrackler.
	 * 
	 * @param iStream
	 *            the input stream.
	 */
	public ClickRemovalFilter() {
		super();
		this.addParameter(THRESHOLD, INT_PARAM, 0, 900, 200);
		this.addParameter(WIDTH, INT_PARAM, 0, 40, 20);

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

	protected void process(MultiChannelBuffer buff) {
		int len = buff.getBufferSize();
		int mClickWidth = getIntControl(WIDTH);
		int mThresholdLevel = getIntControl(THRESHOLD);
		for (int ch = 0; ch < 2; ch++) {
			boolean bResult = false;
			float[] buffer = buff.getChannel(ch);
			int i;
			int j;
			int left = 0;

			float msw;
			int ww;
			int s2 = sep / 2;
			float[] ms_seq = new float[len];
			float[] b2 = new float[len];

			for (i = 0; i < len; i++) {
				b2[i] = buffer[i] * buffer[i];
			}

			/*
			 * Shortcut for rms - multiple passes through b2, accumulating as we
			 * go.
			 */
			for (i = 0; i < len; i++) {
				ms_seq[i] = b2[i];
			}

			for (i = 1; i < sep; i *= 2) {
				for (j = 0; j < len - i; j++) {
					ms_seq[j] += ms_seq[j + i];
				}
			}

			/* Cheat by truncating sep to next-lower power of two... */
			sep = i;

			for (i = 0; i < len - sep; i++) {
				ms_seq[i] /= sep;
			}

			/*
			 * ww runs from about 4 to mClickWidth. wrc is the reciprocal;
			 * chosen so that integer roundoff doesn't clobber us.
			 */
			int wrc;
			for (wrc = mClickWidth / 4; wrc >= 1; wrc /= 2) {
				ww = mClickWidth / wrc;

				for (i = 0; i < len - sep; i++) {
					msw = 0;
					for (j = 0; j < ww; j++) {
						msw += b2[i + s2 + j];
					}
					msw /= ww;

					if (msw >= mThresholdLevel * ms_seq[i] / 10) {
						if (left == 0) {
							left = i + s2;
						}
					} else {
						if (left != 0 && i - left + s2 <= ww * 2) {
							float lv = buffer[left];
							float rv = buffer[i + ww + s2];
							for (j = left; j < i + ww + s2; j++) {
								bResult = true;
								buffer[j] = (rv * (j - left) + lv * (i + ww + s2 - j)) / (float) (i + ww + s2 - left);
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

	// private void toto() {

	// int len = buff.getBufferSize();
	// Assert.isTrue(len == this.windowSize);
	//
	// boolean mDidSomething = false;
	// for(int ch = 0; ch < 2; ch++){
	// boolean bResult = true;
	// int s = 0;
	// float[] buffer = new float[ len ];
	// float[] datawindow = new float[ len ];
	// while ((len - s) > windowSize / 2)
	// {
	// int block = limitSampleBufferSize( idealBlockLen, len - s );
	//
	// track->Get((samplePtr) buffer.get(), floatSample, start + s, block);

	// for (decltype(block) i = 0; i + windowSize / 2 < block; i +=
	// windowSize / 2)
	// {
	// auto wcopy = std::min( windowSize, block - i );
	//
	// for(decltype(wcopy) j = 0; j < wcopy; j++)
	// datawindow[j] = buffer[i+j];
	// for(auto j = wcopy; j < windowSize; j++)
	// datawindow[j] = 0;
	//
	// mbDidSomething |= RemoveClicks(windowSize, datawindow.get());
	//
	// for(decltype(wcopy) j = 0; j < wcopy; j++)
	// buffer[i+j] = datawindow[j];
	// }
	//
	// if (mbDidSomething) // RemoveClicks() actually did something.
	// track->Set((samplePtr) buffer.get(), floatSample, start + s, block);
	//
	// s += block;
	//
	// if (TrackProgress(count, s.as_double() /
	// len.as_double())) {
	// bResult = false;
	// break;
	// }
	// }
	//
	// return bResult;
	// }
}
