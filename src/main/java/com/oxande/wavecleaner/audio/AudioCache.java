package com.oxande.wavecleaner.audio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.util.logging.LogFactory;


/**
 * The audio cache is a very simple clas sto store the audio sample in
 * an uncompressed form on disk. The main advantage of this class is
 * to use the "memory-mapping". We use a file but the kernel is in
 * charge to map the data from the file to
 *
 */
public class AudioCache implements AutoCloseable {
	private static Logger LOG = LogFactory.getLog(AudioCache.class);
    private int lastSample = 0;
    private int bufferSize = 1024;
    private int blockSize;
    private MappedByteBuffer buffer;
    private FileChannel fileChannel;
    private RandomAccessFile randomAccessFile;
    private File file;
    private BigInteger loaded = BigInteger.ZERO;

    public AudioCache( int sampleSize, int numberOfChunks ) throws IOException {
        this.bufferSize = sampleSize;
        this.lastSample = numberOfChunks * sampleSize;
        this.blockSize = this.bufferSize * Float.BYTES * 2;

        // Create file object
        file = File.createTempFile("wave", ".dat");
        LOG.info("Temporary file '{}' created.", file.getAbsoluteFile());

        //Delete the file; we will create a new file
        file.deleteOnExit();

        // Get file channel in readonly mode
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
     * channel at index 1.
     *
     */
    float[][] getSamples(int block) throws IOException {
        float[][] array = new float[2][];
        array[0] = new float[bufferSize];
        array[1] = new float[bufferSize];

        FloatBuffer fBuf = buffer.asFloatBuffer();
        fBuf.position( block * 2  * bufferSize );
        fBuf.get(array[0]); // left channel
        fBuf.position( (block * 2 + 1) *bufferSize );
        fBuf.get(array[1]); // right channel

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
