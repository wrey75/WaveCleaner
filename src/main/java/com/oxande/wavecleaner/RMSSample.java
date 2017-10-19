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
