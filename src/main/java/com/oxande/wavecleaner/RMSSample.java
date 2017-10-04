package com.oxande.wavecleaner;

import com.oxande.wavecleaner.util.Assert;

/**
 * A basic sample which contains the level and the peak for left and right.
 * 
 * @author wrey
 *
 */
public class RMSSample {
	public float levelL;
	public float levelR;
	public float peakL;
	public float peakR;

	public RMSSample(float levL, float levR, float peakL, float peakR) {
		this.peakR = peakR;
		this.peakL = peakL;
		this.levelR = levR;
		this.levelL = levL;
	}

	/**
	 * Create a wave sample from samples.
	 * 
	 * @param sampL
	 *            the left samples
	 * @param sampR
	 *            the right samples
	 */
	public static RMSSample create(float[] sampL, float[] sampR) {
		Assert.isTrue(sampL != null);
		Assert.isTrue(sampR != null);
		Assert.isTrue(sampR.length == sampL.length);
		
		float levL = 0.0f;
		float levR = 0.0f;
		float peakL = 0.0f;
		float peakR = 0.0f;

		for (int i = 0; i < sampL.length; i++) {
			levR += (sampR[i] * sampR[i]);
			levL += (sampL[i] * sampL[i]);
			if (sampR[i] > peakR) peakR = sampR[i];
			if (sampL[i] > peakL) peakL = sampL[i];
		}
		levR = (float) Math.sqrt(levR / sampR.length);
		levL = (float) Math.sqrt(levL / sampL.length);

		return new RMSSample(levL, levR, peakL, peakR);
	}

}
