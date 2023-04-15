package com.oxande.wavecleaner.audio;

import ddf.minim.Minim;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * The audio cache is a very simple class to store the audio sample in
 * an uncompressed form on disk. The main advantage of this class is
 * to use the "memory-mapping". We use a file but the kernel is in
 * charge to map the data from the file to
 *
 */
@Slf4j
public class AudioCache implements AutoCloseable {

	public static final String PREFIX = "wcleaner-";
	public static final String SUFFIX = ".dat";
	
    private int lastSample = 0;
    private int bufferSize = 1024;
    private MappedByteBuffer buffer;
    private FileChannel fileChannel;
    private RandomAccessFile randomAccessFile;
    private File file;

    public AudioCache( int sampleSize, int numberOfChunks ) throws IOException {
        this.bufferSize = sampleSize;
        this.lastSample = (numberOfChunks+1) * sampleSize;

        // Create file object
        file = File.createTempFile(PREFIX, SUFFIX);
        LOG.info("Temporary file '{}' created.", file.getAbsoluteFile());

        //Delete the file; we will create a new file
        file.deleteOnExit();

        // Get file channel in read-only mode
        randomAccessFile = new RandomAccessFile(file, "rw");
        fileChannel = randomAccessFile.getChannel();

        // Get direct byte buffer access using channel.map() operation
        buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, lastSample * 2 * Float.BYTES);
    }

    /**
     * Returns the samples for the requested part of the file.
     *
     * @param block the block.
     * @return an array that contains the left channel in index 0 and right
     * channel at index 1. Always {@link Minim#STEREO}.
     *
     */
    float[][] getSamples(int block) {
        float[][] array = new float[2][];
        array[0] = new float[bufferSize];
        array[1] = new float[bufferSize];
        
        if( block * bufferSize > lastSample ){
        	LOG.warn("Trying to read block {} after the end of the record.", block );
        	return array;
        }

        try {
	        FloatBuffer fBuf = buffer.asFloatBuffer();
	        fBuf.position( block * 2  * bufferSize );
	        fBuf.get(array[0]); // left channel
	        fBuf.position( (block * 2 + 1) *bufferSize );
	        fBuf.get(array[1]); // right channel
        }
        catch( BufferUnderflowException ex){
        	LOG.error("Buffer Underflow: block {} not available, requested up to position {} but the capacity is {}.",
        			block,  (block+1) * 2 * bufferSize * Float.BYTES, buffer.capacity() );
        	
        }

        return array;
    }

    public void saveSamples(int block, float[] left, float[] right ) throws IOException {
        FloatBuffer fBuf = buffer.asFloatBuffer();
        fBuf.position( block * 2 * this.bufferSize );
        fBuf.put(left); // left channel
        fBuf.position( (block * 2 + 1) * this.bufferSize );
        fBuf.put(right); // right channel
    }

    @Override
    public void close() throws IOException {
    	try {
	        fileChannel.close();
	        // randomAccessFile.close();
	        // file.delete();
	        LOG.info("Temporary file '{}' deleted.", file.getAbsolutePath());
    	}
    	catch(IOException ex ){
    		LOG.error("I/O Error: {}", ex.getMessage());
    	}
    }
}
