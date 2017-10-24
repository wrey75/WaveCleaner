package com.oxande.wavecleaner.filters;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.ConvertUtils;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.MultiChannelBuffer;

/**
 * The controller filter. The output is passed to the sound card.
 * 
 * @author wrey75
 *
 */
public class ControllerFilter extends AudioFilter {
	private Logger LOG = LogFactory.getLog(ControllerFilter.class);
	
	public static final String GAIN = "gain";
	public static final String SOURCE = "source";

	public ControllerFilter(){
		super();
		this.addParameter(GAIN, FLOAT_PARAM, -24.0f, 0.0f, +24.0f);
		this.addBooleanParameter(SOURCE, true);
	}
	
	public void setEnabled(boolean b){
		setControl(SOURCE, ConvertUtils.bool2flt(b) );
	}

	public boolean getEnabled(){
		return ConvertUtils.flt2bool( getControl(SOURCE) );
	}
	
	protected void process(MultiChannelBuffer buff) {
		float dBvalue = this.getControl(GAIN);
		float mValue = (float)Math.pow(10.0, (0.05 * dBvalue));
		int len = buff.getBufferSize();
		for( int ch = 0; ch < buff.getChannelCount(); ch++ ){
			for(int i = 0; i < len; i++ ){
				float sample = buff.getSample(ch, i);
				sample = sample * mValue;
				buff.setSample(ch,  i, sample);
			}
		}
	}
	
}
