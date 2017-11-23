package com.oxande.wavecleaner.filters;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.ui.VUMeterComponent;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.ConvertUtils;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.MultiChannelBuffer;

/**
 * The preamplifier filter (aka ControllerFilter but ranamed to match the real functionality). 
 * The output is directly passed to the sound card. The preamplifier has several roles but the 
 * main one is to get the 2 inputs (the "filtered" and the "original" sound) and to be able to play
 * both. 
 * 
 * <p>
 * The filter has a connector to the last filter (through patching) but also to the {@link AudioDocumentPlayer}
 * which is directly connected to the preamplifier. Once the source is selected, the sound can pass in the "gain"
 * filtering to amplify the sound (should not exceed 0dB, to avoid listening issues, but can be useful for a
 * "differential" control).  
 * </p>
 * 
 * @author wrey75
 *
 */
public class PreamplifierFilter extends AudioFilter {
	private Logger LOG = LogFactory.getLog(PreamplifierFilter.class);
	
	public static final String GAIN = "gain";
	public static final String SOURCE = "source";
	public static final String LIMITER = "limiter";
	
	/**
	 * Source for normal sound (through the filters).
	 */
	public static final int NORMAL = 0;
	
	/**
	 * Source for original sound (untouched).
	 */
	public static final int ORIGINAL = 1;
	
	/**
	 * Source minus original sound (what has been removed by the filters).
	 */
	public static final int DIFF = 2;
	
	/**
	 * Original sound on left and filtered one on right. No more stereo has
	 * the music is transferred in MONO.
	 * 
	 */
	public static final int LEFT_RIGHT = 3;
	
	private AudioDocumentPlayer player;
	private VUMeterComponent vumeter = null;
	
	public PreamplifierFilter( AudioDocumentPlayer player){
		super();
		this.addParameter(GAIN, -24.0f, +12.0f, 3.0f, 0.1f, (v) -> {
			NumberFormat formatter = new DecimalFormat("0.0 dB");
			return formatter.format(v);
		});
		this.addSelectorParameter(SOURCE, 3);
		this.addBooleanParameter(LIMITER, true);
		this.setSampleRate(48000f); // Force a default sample rate (overriden by the player)
		if( player != null ){
			this.setPlayer(player);
		}
	}
	
	public void setEnabled(boolean b){
		setControl(SOURCE, ConvertUtils.bool2flt(b) );
	}

	public boolean getEnabled(){
		return ConvertUtils.flt2bool( getControl(SOURCE) );
	}
	
	public void setPlayer(AudioDocumentPlayer player){
		Assert.notNull(player);
		Assert.isTrue( player.sampleRate() > 0 );
		LOG.debug("New player is now {}", player);
		this.player = player;
		this.setSampleRate(player.sampleRate()); // Use the player sample rate
	}

	public void setVUMeter(VUMeterComponent vumeter){
		this.vumeter = vumeter;
		this.vumeter.setSampleRate(this.sampleRate());
	}
	
	@Override
	protected void sampleRateChanged(){
		if( this.vumeter != null ){
			this.vumeter.setSampleRate(this.sampleRate());
		}
	}
	
	protected void process(MultiChannelBuffer buff) {
		if(player == null){
			throw new IllegalAccessError("'player' not set.");
		}
		
		float[] original = new float[2];
		float[] source = new float[2];
		float[] sample = new float[2];
		float dBvalue = this.getControl(GAIN);
		float mValue = (float)Math.pow(10.0, (0.05 * dBvalue));
		int mode = this.getIntControl(SOURCE);

		int len = buff.getBufferSize();
		for(int i = 0; i < len; i++ ){
			source[0] = buff.getSample(0, i);
			source[1] = buff.getSample(1, i);
				
			player.pop(original);
			
//			if( Math.abs(source[0] - original[0]) > 0.1 || Math.abs(source[1] - original[1]) > 0.1) {
//				LOG.debug("SIGNAL at {} HAS CHANGED A LOT: {} => {} (delay = {})", i, original, source, player.getDelay() );
//			}

			switch (mode) {
			case NORMAL:
				sample = source;
				break;
			case ORIGINAL:
				sample = original;
				break;
			case DIFF:
				sample[0] = source[0] - original[0];
				sample[1] = source[1] - original[1];
				break;
			case LEFT_RIGHT:
				// Channels are monaural to avoid a bad interpretation.
				sample[0] = (source[0] + source[1]) / 2.0f;
				sample[1] = (original[0] + original[1]) / 2.0f;
				break;
			}
				
			for( int ch = 0; ch < buff.getChannelCount(); ch++ ){
				sample[ch] = sample[ch] * mValue;
				buff.setSample(ch, i, sample[ch]);
			}
			

			float maxVol = Math.max(Math.abs(sample[0]), Math.abs(sample[1]));
			if( maxVol > 0.90f ){
				if(ConvertUtils.flt2bool(getControl(LIMITER))){
					dBvalue = this.getControl(GAIN) / (maxVol + 0.1f);
					this.setControl(GAIN, dBvalue);
					mValue = (float)Math.pow(10.0, (0.05 * dBvalue));
				}
			}
			if( vumeter != null ){
				vumeter.push(sample);
			}
		}
	}
	
}
