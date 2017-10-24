package com.oxande.wavecleaner.filters;

import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.FilePlayer;

public class AudioDocumentPlayer extends FilePlayer {
	
	float[] rightQueue;
	float[] leftQueue;
	int position = 0;
	int buffered = 0;

	/**
	 * Return the delay in samples. The delay is the exact number of
	 * samples in the queue. Not very tricky.
	 * 
	 * @return the delay in samples
	 */
	public int getDelay(){
		return buffered;
	}

	public AudioDocumentPlayer(AudioRecordingStream iFileStream) {
		super(iFileStream);
		this.leftQueue = new float[100];
		this.rightQueue = new float[100];
	}
	

	public void cue(int millis){
		// We should know this... Because we are not at the same point anymore
		super.cue(millis);
	}
	
	public float[] newQueue(float[] src, int inc ){
		int newSize = src.length + inc;
		float[] newQueue = new float[newSize];
		System.arraycopy(src, 0, newQueue, 0, src.length);
		return newQueue;
	}

	public void pop( float[] channels){
		if( buffered < 1 ){
			throw new IllegalAccessError("The queue is empty!");
		}
		int pos = position % rightQueue.length;
		channels[0] = leftQueue[pos];
		channels[1] = rightQueue[pos];
		position++;
		buffered--;
		if( position > leftQueue.length ){
			// Not fully neceessary because we do a modulo when pushing
			position -= leftQueue.length;
		}
	}
	
	void push( float left, float right){
		if( buffered + 1 > rightQueue.length ){
			rightQueue = newQueue(rightQueue, 100);
			leftQueue = newQueue(leftQueue, 100);
		}
				
		int pos = (position + buffered) % rightQueue.length;
		leftQueue[ pos ] = right; 
		rightQueue[ pos ] = right; 
		buffered++;
	}

	protected void uGenerate(float[] channels) 
	{
		push(channels[0], channels[1]); // Store in the original buffer 
		super.uGenerate(channels);
	}
}
