package com.oxande.wavecleaner.filters;

import ddf.minim.UGen;

/**
 * This is the base for audio filters. Note the audio
 * filter is an high-end filter (mainly because it can
 * work on many samples and not only one) but also because
 * it can (and must drive) components to modify its
 * working capabilities. 
 * 
 * @author wrey75
 *
 */
public abstract class AudioFilter extends UGen {

}
