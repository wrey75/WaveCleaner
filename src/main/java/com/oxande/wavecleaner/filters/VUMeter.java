package com.oxande.wavecleaner.filters;

import com.oxande.wavecleaner.ui.VUMeterComponent;

import ddf.minim.UGen;

public class VUMeter extends UGen {

	private UGen			 audio;
	private VUMeterComponent vu = new VUMeterComponent();
	private float[]			 tickBuffer;
	
	public VUMeterComponent getComponent(){
		return this.vu;
	}
	
	@Override
	protected void addInput(UGen in)
	{
		this.audio = in;
		this.tickBuffer = new float[in.channelCount()];
	}
	
	@Override
	protected void uGenerate(float[] channels) {
		audio.tick(tickBuffer);
		
		System.arraycopy(tickBuffer, 0, channels, 0, tickBuffer.length);
	}

}
