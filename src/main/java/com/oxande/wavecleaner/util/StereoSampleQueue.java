/*****************************************************************************
*   Java Record Cleaner
*   Copyright (C) 2017 William Rey
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

package com.oxande.wavecleaner.util;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.MultiChannelBuffer;
import ddf.minim.UGen;

/**
 * This class is a queue for an audio stream.
 *
 */
public class StereoSampleQueue {
	private static Logger LOG = LogFactory.getLog(StereoSampleQueue.class);
	
    public static int BUFFER_LEN = 1024;
    public static int MIN_BUFF = 64;

    private float buf[][]; // left on index 0 and right on index 1.
    private int size;
    private int position;
    // private AudioStream stream;
    private UGen ugen;
    // private MultiChannelBuffer tmpBuffer;

    public StereoSampleQueue( UGen ugen ){
    	// tmpBuffer = new MultiChannelBuffer(BUFFER_LEN, 2);
    	this.buf = new float[2][BUFFER_LEN];
        this.position = 0;
        this.size = 0;
        // this.stream = stream;
        this.ugen = ugen;
    }

    /**
     * Get the samples for the buffer.
     * 
     * @param len the number of samples to consume.
     * @param extra the extra number of samples to get (but kept in the buffer)
     * 
     * @return the array of samples requested
     */
    public float[][] getSamples(int len, int extra){
        float[][] out = new float[2][len+extra];
        peek(out, 0, len);
        this.position += len; // Move the position in the buffer
        peek(out, len, extra);
        return out;
    }


    /**
     * Read from the {@link UGen}.
     * 
     * @param buf the buffer to read (we read everything).
     */
//    static long nb = 0;
//    static long nb_left = 0;
//    static long nb_right = 0;
    protected void readFromInput(MultiChannelBuffer buf){
    	int size = buf.getBufferSize();
    	int channelCount = buf.getChannelCount();
    	float[] channels = new float[channelCount];
    	for(int i = 0; i < size; i++ ){
    		ugen.tick(channels);
//    		nb += 2;
//    		if( Math.abs(channels[0]) == 0.0f ){
//    			nb_left++;
//    			LOG.debug("CHANNEL LEFT NEAR zero {} with i = {}/{} ({}%)", channels, i, size, String.format("%1.5f", 100 * (double)nb_left / nb));
//    		}
//    		if( Math.abs(channels[1]) == 0.0f ){
//    			nb_right++;
//    			LOG.debug("CHANNEL RIGHT NEAR zero {} with i = {}/{} ({}%)", channels, i, size, String.format("%1.5f", 100 * (double)nb_right / nb));
//    		}
   			buf.setSample(0, i, channels[0]);
   			buf.setSample(1, i, channels[1]);
    	}
    }

    
    /**
     * Get the samples found in the buffer but without consuming them. This
     * method will also read the extra bytes in the buffer.
     *
     * @param out the output buffer (STEREO)
     * @param offset the offset from the beginning of the buffer
     * @param len the length to read
     */
    protected void peek(float[][] out, int offset, int len){
        // Complete the buffer if necessary
        while( position + len > size ){ // In case one buffer is not sufficient to store the stuff
            // We have to look forward...
        	MultiChannelBuffer tmpBuffer = new MultiChannelBuffer(Math.max(MIN_BUFF, len), 2);
            readFromInput(tmpBuffer);
            int remaining = size - position;
            if(tmpBuffer.getBufferSize() + remaining > buf[0].length) {
            	int newLength = this.buf[0].length + tmpBuffer.getBufferSize();
            	LOG.debug("Increased the queue to {} samples", newLength);
            	for( int ch = 0; ch < 2; ch ++){
	                float[] newBlock = new float[newLength];
	                System.arraycopy(this.buf[ch], position, newBlock, 0, remaining); // The remaining current buffer
	                System.arraycopy(tmpBuffer.getChannel(ch), 0, newBlock, remaining, tmpBuffer.getBufferSize()); // The added buffer
	                this.buf[ch] = newBlock;
            	}
            }
            else {
                // We just move back...
            	for( int ch = 0; ch < 2; ch ++){
	                if( position > 0 && remaining > 0) {
	                    // Do not copy if we already are at the start, just add the new block
	                    System.arraycopy(this.buf[ch], position, this.buf[ch], 0, remaining); // The remaining current buffer
	                }
	                System.arraycopy(tmpBuffer.getChannel(ch), 0, this.buf[ch], remaining, tmpBuffer.getBufferSize()); // The added buffer
            	}
            }
            
            // Update size and position
            this.size = remaining + tmpBuffer.getBufferSize();
            this.position = 0; // Reset the position to zero
        }

        // Copy to the output buffer
        for( int ch = 0; ch < 2; ch ++){
        	System.arraycopy(this.buf[ch], this.position, out[ch], offset, len);
        }
    }
}
