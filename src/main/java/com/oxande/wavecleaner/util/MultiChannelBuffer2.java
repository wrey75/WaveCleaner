package com.oxande.wavecleaner.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import ddf.minim.MultiChannelBuffer;
import ddf.minim.javasound.FloatSampleBuffer;

public class MultiChannelBuffer2 extends MultiChannelBuffer {

	public static final int BUFFER_SIZE = 64 * 1024; // 64 KB
	
	private static class BufferCache {
		File tempFile;
		RandomAccessFile channel;
		FileChannel fChannel;
		int ptrCache;
		int bufferSize;
		float buf[];
		
		public BufferCache(int bufferSize) {
			try {
				fChannel = open(bufferSize);
				ptrCache = 0;
				buf = new float[BUFFER_SIZE];
				this.bufferSize = bufferSize;
			} catch(IOException ex){
				throw new IllegalStateException(ex);
			}
		}
		
		public int getSize(){
			return this.bufferSize;
		}
		
		private FileChannel open( int size ) throws IOException{
			tempFile = File.createTempFile("sound", ".data");
			tempFile.deleteOnExit();
			channel = new RandomAccessFile(tempFile, "rw");
			channel.setLength(size * 4); // float is 4 bytes 
			return channel.getChannel();
		}
		
		public void close() {
			try {
				channel.close();
				tempFile.delete();
				tempFile.delete();
			} catch (IOException e) {
				// Do not try to hard...
				e.printStackTrace();
			}

		}
		
		/**
		 * Save the current part of the buffer (if necessary).
		 * 
		 * @throws IOException
		 */
		private void savePart() throws IOException{
			ByteBuffer writeBuf = ByteBuffer.allocate(BUFFER_SIZE * 4);
			writeBuf.clear();
			writeBuf.asFloatBuffer().put(buf);
			channel.seek(this.ptrCache);
			fChannel.write(writeBuf);
		}
		
		/**
		 * Load the part containing the element i.
		 * 
		 * @param i the element position.
		 * @throws IOException
		 */
		private void loadPart(int i) throws IOException{
			ByteBuffer readBuf = ByteBuffer.allocate(BUFFER_SIZE * 4);
			ptrCache = Math.max(0, i - 1024);
			
			readBuf.clear();
			channel.seek(this.ptrCache);
			fChannel.read(readBuf);
			readBuf.asFloatBuffer().get(buf);
		}

		/**
		 * Check if the requested position is currently in
		 * memory.
		 * 
		 * @param i the position
		 * @throws IOException
		 */
		private void check(int i) throws IOException{
			if( i < ptrCache || i > ptrCache + BUFFER_SIZE ){
				savePart();
				loadPart(i);
			}
		}
		
		public float get(int i){
			try {
				check(i);
				return buf[i-ptrCache];
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		}
		
		public void set(int i, float v){
			try {
				check(i);
				buf[i-ptrCache] = v;
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		}

		/**
		 * Copy the array. This is very dangerous because
		 * an {@link OutOfMemoryError} can be thrown in
		 * case of long file or simply of a high sampling
		 * rate.
		 * 
		 * @param data the data of the original channel
		 */
		public void copy(float[] data){
			for(int i = 0; i < data.length; i++ ){
				this.set(i, data[i]);
			}
		}
		
		public float[] toFloatArray(){
			float[] array = new float[this.getSize()];
			for(int i = 0; i < array.length; i++){
				array[i] = this.get(i);
			}
			return array;
		}
	}
	
	private BufferCache cachedChannels[] = new BufferCache[0];				
	private int bufferSize;
	
	/**
	 * Construct a MultiChannelBuffer, providing a size and number of channels.
	 * 
	 * @param bufferSize
	 * 			int: The length of the buffer in sample frames.
	 * @param numChannels
	 * 			int: The number of channels the buffer should contain.
	 * @throws IOException 
	 */
	public MultiChannelBuffer2(int bufferSize, int numChannels) throws IOException
	{
		super(0,0); // To avoid compilation errors
		openChannels(numChannels, bufferSize);
		this.bufferSize = bufferSize;
	}
	
	/**
	 * Copy the data in the provided MultiChannelBuffer to this MultiChannelBuffer.
	 * Doing so will change both the buffer size and channel count of this
	 * MultiChannelBuffer to be the same as the copied buffer.
	 * 
	 * @shortdesc Copy the data in the provided MultiChannelBuffer to this MultiChannelBuffer.
	 * 
	 * @param otherBuffer
	 * 			the MultiChannelBuffer to copy
	 */
	public void set( MultiChannelBuffer otherBuffer )
	{
		try {
			close();
			bufferSize = otherBuffer.getBufferSize();
			openChannels(otherBuffer.getChannelCount(), bufferSize);
			for(int i = 0; i < otherBuffer.getChannelCount(); i++){
				cachedChannels[i].copy(otherBuffer.getChannel(i));
			}
		} catch (IOException ex) {
			// It is not possible to copy the buffer
			throw new IllegalStateException(ex);
		}

	}
	
	/**
	 * Returns the length of this buffer in samples.
	 * 
	 * @return the length of this buffer in samples
	 */
	public int getBufferSize()
	{
		return bufferSize;
	}
	
	/**
	 * Returns the number of channels in this buffer.
	 * 
	 * @return the number of channels in this buffer
	 */
	public int getChannelCount()
	{
		return cachedChannels.length;
	}
	
	/**
	 * Returns the value of a sample in the given channel,
	 * at the given offset from the beginning of the buffer.
	 * When sampleIndex is a float, this returns an interpolated
	 * sample value. For instance, getSample( 0, 30.5f ) will 
	 * return an interpolated sample value in channel 0 that is 
	 * between the value at 30 and the value at 31. 
	 * 
	 * @shortdesc Returns the value of a sample in the given channel,
	 * at the given offset from the beginning of the buffer.
	 * 
	 * @param channelNumber
	 * 			int: the channel to get the sample value from
	 * @param sampleIndex
	 * 			int: the offset from the beginning of the buffer, in samples.
	 * @return
	 * 			float: the value of the sample
	 */
	public float getSample( int channelNumber, int sampleIndex )
	{
		return cachedChannels[channelNumber].get(sampleIndex);
	}
	
	/**
	 * Returns the interpolated value of a sample in the given channel,
	 * at the given offset from the beginning of the buffer, 
	 * For instance, getSample( 0, 30.5f ) will 
	 * return an interpolated sample value in channel 0 that is 
	 * between the value at 30 and the value at 31. 
	 * 
	 * @param channelNumber
	 * 			int: the channel to get the sample value from
	 * @param sampleIndex
	 * 			float: the offset from the beginning of the buffer, in samples.
	 * @return
	 * 			float: the value of the sample
	 */
	public float getSample( int channelNumber, float sampleIndex )
	{
		  int lowSamp = (int)sampleIndex;
		  int hiSamp = lowSamp + 1;
		  if ( hiSamp == bufferSize )
		  {
			  return getSample(channelNumber, lowSamp);
		  }
		  float lerp = sampleIndex - lowSamp;
		  return getSample(channelNumber, lowSamp) + lerp*(getSample(channelNumber, hiSamp) - getSample(channelNumber, lowSamp));
	}
	
	/**
	 * Sets the value of a sample in the given channel at the given
	 * offset from the beginning of the buffer.
	 * 
	 * @param channelNumber
	 * 			int: the channel of the buffer
	 * @param sampleIndex
	 * 			int: the sample offset from the beginning of the buffer
	 * @param value
	 * 			float: the sample value to set
	 */
	public void setSample( int channelNumber, int sampleIndex, float value )
	{
		cachedChannels[channelNumber].set(sampleIndex, value);
	}
	
	/**
	 * Calculates the RMS amplitude of one of the buffer's channels.
	 * 
	 * @example Advanced/OfflineRendering
	 * 
	 * @param channelNumber
	 * 			int: the channel to use
	 * @return
	 * 			float: the RMS amplitude of the channel
	 */
	public float getLevel( int channelNumber )
	{
		BufferCache cache = cachedChannels[channelNumber];
		float level = 0;
	    for (int i = 0; i < cache.getSize(); i++)
	    {
	    	float v = cachedChannels[channelNumber].get(i);
	    	level += (v * v);
	    }
	    level /= cache.getSize();
	    level = (float) Math.sqrt(level);
	    return level;
	}
	
	/**
	 * Returns the requested channel as a float array.
	 * You should not necessarily assume that the 
	 * modifying the returned array will modify 
	 * the values in this buffer.
	 * 
	 * @shortdesc Returns the requested channel as a float array.
	 * 
	 * @param channelNumber
	 * 			int: the channel to return
	 * @return
	 * 			float[]: the channel represented as a float array
	 */
	public float[] getChannel(int channelNumber)
	{
		return cachedChannels[channelNumber].toFloatArray();
	}
	
	/**
	 * Sets all of the values in a particular channel using 
	 * the values of the provided float array. The array
	 * should be at least as long as the current buffer size
	 * of this buffer and this will only copy as many samples
	 * as fit into its current buffer size.
	 * 
	 * @shortdesc Sets all of the values in a particular channel using 
	 * the values of the provided float array.
	 * 
	 * @param channelNumber
	 * 			int: the channel to set
	 * @param samples
	 * 			float[]: the array of values to copy into the channel
	 */
	public void setChannel(int channelNumber, float[] samples)
	{
		BufferCache cache = cachedChannels[channelNumber];
		for(int i = 0; i< samples.length; i++){
			cache.set(i, samples[i]);
		}
	}
	
	/**
	 * Set the number of channels this buffer contains.
	 * Doing this will retain any existing channels 
	 * under the new channel count.
	 * 
	 * @shortdesc Set the number of channels this buffer contains.
	 * 
	 * @param numChannels
	 * 			int: the number of channels this buffer should contain
	 */
	public void setChannelCount(int numChannels)
	{
		if ( cachedChannels.length != numChannels )
		{
			int size = cachedChannels[0].getSize(); 
			BufferCache[] newChannels = new BufferCache[numChannels];
			for( int i = numChannels; i < cachedChannels.length; i++ ){
				cachedChannels[i].close();
			}
			for( int c = 0; c < numChannels; c++ )
			{
				if( c < cachedChannels.length ){
					newChannels[c] = cachedChannels[c];
				}
				else {
					newChannels[c] = new BufferCache(size);
				}
			}
			cachedChannels = newChannels;
		}
	}
	
	/**
	 * Set the length of this buffer in sample frames.
	 * Doing this will retain all of the sample data 
	 * that can fit into the new buffer size.
	 * 
	 * @shortdesc Set the length of this buffer in sample frames.
	 * 
	 * @param bufferSize
	 * 			int: the new length of this buffer in sample frames
	 */
	public void setBufferSize(int bufferSize)
	{
		if ( this.bufferSize != bufferSize )
		{
			this.bufferSize = bufferSize;
			for( int i = 0; i < cachedChannels.length; ++i )
			{
				BufferCache newChannel = new BufferCache(bufferSize);;
				// copy existing data into the new channel array
				int len = (bufferSize < cachedChannels[i].getSize() ? bufferSize : cachedChannels[i].getSize());
				for(int j = 0; j < len; j++){
					newChannel.set(i,  cachedChannels[i].get(i));
				}
				cachedChannels[i].close();
				cachedChannels[i] = newChannel;
			}
		}
	} 

	/**
	 * Close the channels.
	 * 
	 * @throws IOException 
	 */
	public void close() throws IOException{
		for(int i = 0; i < cachedChannels.length; i++ ){
			cachedChannels[i].close();
		}
		cachedChannels = new BufferCache[0];
	}

	private void openChannels(int num, int size ) throws IOException{
		cachedChannels = new BufferCache[num];
		for(int i = 0; i < num; i++){
			cachedChannels[i] = new BufferCache(size);
		}
	}
}
