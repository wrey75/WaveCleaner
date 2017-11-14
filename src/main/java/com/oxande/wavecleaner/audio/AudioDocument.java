package com.oxande.wavecleaner.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import org.apache.logging.log4j.Logger;

import com.oxande.wavecleaner.RMSSample;
import com.oxande.wavecleaner.WaveCleaner;
import com.oxande.wavecleaner.filters.AudioDocumentPlayer;
import com.oxande.wavecleaner.filters.AudioPlayerListener;
import com.oxande.wavecleaner.filters.ClickRemovalFilter;
import com.oxande.wavecleaner.filters.DecrackleFilter;
import com.oxande.wavecleaner.filters.PreamplifierFilter;
import com.oxande.wavecleaner.util.Assert;
import com.oxande.wavecleaner.util.ListenerManager;
import com.oxande.wavecleaner.util.logging.LogFactory;

import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.MultiChannelBuffer;
import ddf.minim.SignalSplitter;
import ddf.minim.spi.AudioRecordingStream;
import ddf.minim.ugens.Summer;

/**
 * Management of an audio file. The audio document can be seen as the "driver"
 * for its display. I created a listener for the document as it is the case for
 * mouse events and so on. It is based on Microsoft MFC management with a
 * <code>CDocument</code> and a <code>CView</code>.
 * 
 * <p>
 * When the file is linked to this audio document class, the levels are read
 * from the disk at full speed (works also on MP3!) and visually updated in the
 * display. We use a thread to read the file then the update can be done behind
 * the scenes.
 * </p>
 * 
 * 
 * 
 * @author wrey75
 *
 */
public class AudioDocument /* implements AudioListener */ {
	private Logger LOG = LogFactory.getLog(AudioDocument.class);

	String fileName;
	AudioRecordingStream stream;
	AudioCache cache;
	AudioDocumentPlayer documentPlayer = null;
	AudioOutput lineOut = null;
	int bufferSize;
	int sampleRate;
	int leftChannel = 0;
	int rightChannel = 1;
	int nbChannels = 2;
	private int totalSamples = 0;
	public DecrackleFilter decrackFilter = new DecrackleFilter();
	public ClickRemovalFilter clickFilter = new ClickRemovalFilter();
	public PreamplifierFilter preamplifer = new PreamplifierFilter(null);

	private RMSSample[] samples = null;

	/**
	 * Returns the level samples.
	 * 
	 * @return the array of samples
	 */
	public RMSSample[] getLevels() {
		return this.samples;
	}

	public float getSampleRate() {
		return this.stream.getFormat().getSampleRate();
	}

	/**
	 * Return the size of the buffer.
	 * 
	 */
	public int getChunkSize() {
		return this.bufferSize;
	}

	/**
	 * Get the file player. If there was no file player, a new one is created.
	 * Note also the filters are patched at this time up to the {@link PreamplifierFilter}.
	 * The preamlifier will be patched to the line output or the recorder.
	 * 
	 * @return the file player.
	 */
	private AudioDocumentPlayer getDocumentPlayer() {
		if (this.documentPlayer == null) {
			this.documentPlayer = new AudioDocumentPlayer(stream);
			preamplifer.setPlayer(this.documentPlayer);
			this.documentPlayer.patch(decrackFilter).patch(clickFilter).patch(preamplifer);
		}
		return this.documentPlayer;
	}

	/**
	 * Attach a line out for playing directly.
	 * 
	 * @param out
	 *            the line out (usually speakers)
	 */
	public void attachLineOut(AudioOutput out) {
		this.lineOut = out;
	}

	public boolean isPlaying() {
		return getDocumentPlayer().isPlaying();
	}
	
	/**
	 * The FFT size (always a power of 2) depending of the sample rate.
	 * The FFT size is about 20 milliseconds of music.
	 * 
	 * @return the FFT size.
	 */
	public int fftSize(){
		if( sampleRate < 10000 ){
			// 8 kHz and below?!?
			return 256;
		} else if( sampleRate < 23000 ){
			// 22 kHz
			return 512;
		} else if( sampleRate < 50000 ){
			// 44.1KHz and 48 kHz
			return 1024;
		} else if( sampleRate < 100000 ){
			// 96 kHz
			return 2028;
		} else if( sampleRate < 200000 ){
			// 192 kHz
			return 4096;
		}
		// Should never be the case!
		return 8192;
	}
	
	/**
	 * Create an audio document.
	 * 
	 * @param minim
	 *            the minim object.
	 * @param f
	 *            the file
	 * @throws IOException
	 */
	public AudioDocument(WaveCleaner app, File f) throws IOException {
		this.fileName = f.getAbsolutePath();
		// construct a new MultiChannelBuffer with 2 channels and 1024 sample
		// frames.
		// in our particular case, it doesn't really matter what we choose for
		// these
		// two values because loadFileIntoBuffer will reconfigure the buffer
		// to match the channel count and length of the file.
		// MultiChannelBuffer sampleBuffer = new MultiChannelBuffer(1, 1024);

		// we pass the buffer to the method and Minim will reconfigure it to
		// match
		// the file. if the file doesn't exist, or there is some other problen
		// with
		// loading it, the function will return 0 as the sample rate.
		this.stream = app.loadFileStream(f);
		// this.getDocumentPlayer(); // = new AudioDocumentPlayer(stream);
		this.sampleRate = (int) this.stream.getFormat().getSampleRate();
		this.bufferSize = fftSize(); // There is no relationship between the size of the buffer and the FFT size

		nbChannels = stream.getFormat().getChannels();
		if (nbChannels < 2) {
			rightChannel = leftChannel = 0;
		}

		int streamDuration = stream.getMillisecondLength();
		totalSamples = (int) ((streamDuration * 0.001 * sampleRate)) + 1;
		int nbChunks = (int) (totalSamples / bufferSize) + 1;
		this.cache = new AudioCache(bufferSize, nbChunks);
		new Thread(() -> loadWaveSamples()).start();
	}

	/**
	 * Returns the audio file (a reference to the).
	 * 
	 * @return the file
	 */
	public File getFile() {
		File f = new File(this.fileName);
		return f;
	}

	public AudioFormat getFormat() {
		return stream.getFormat();
	}

	/**
	 * Returns the number of chunks. A chunk contains the audio levels for about
	 * 0.2 seconds of music.
	 * 
	 * @return the number of chunks.
	 */
	protected int getNumberOfChunks() {
		int totalChunks = (totalSamples / bufferSize) + 1;
		return totalChunks;
	}

	public int getNumberOfSamples() {
		return totalSamples;
	}

	/**
	 * Get the audio samples in a chunk. Use a mapped memory to load the samples
	 * inside the chunk.
	 * 
	 * @param chunk
	 *            the chunk. The size of a chunk is depending of the window size
	 *            (basically a chunk is about 20 milliseconds).
	 * @return the array of samples (always stereo expected).
	 */
	public float[][] getAudioSamples(int chunk) {
		float[][] samples = null;
		samples = this.cache.getSamples(chunk);
		return samples;
	}

	ListenerManager<AudioChangedListener> listenerManager = new ListenerManager<AudioChangedListener>();

	public void addChangedAudioListener(AudioChangedListener listener) {
		listenerManager.add(listener);
	}

	public void removeChangedAudioListener(AudioChangedListener listener) {
		listenerManager.remove(listener);
	}

	void addAudioChangedListener(AudioChangedListener listener) {

	}

	/**
	 * Load the samples of the audio file. This method should be called in a
	 * different thread.
	 * 
	 * @throws IOException
	 * 
	 */
	public synchronized void loadWaveSamples() {
		int nbChunks = this.getNumberOfChunks();
		stream.play();
		MultiChannelBuffer buf = new MultiChannelBuffer(bufferSize, stream.getFormat().getChannels());
		this.samples = new RMSSample[nbChunks];
		for (int i = 0; i < nbChunks; i++) {
			stream.read(buf);
			float left[] = buf.getChannel(leftChannel);
			float right[] = buf.getChannel(rightChannel);
			samples[i] = RMSSample.create(left, right);
			try {
				this.cache.saveSamples(i, left, right);
			} catch (IOException e) {
				LOG.error("Can not save into the cache.");
			}
			listenerManager.publishOnce(l -> l.audioChanged());
		}
		stream.pause();
		listenerManager.publish(l -> l.audioChanged());
	}

	/**
	 * Stop to play. Basically a mute but we unpatch the line out!
	 * 
	 */
	public synchronized void stop() {
		Assert.notNull(this.documentPlayer);
		if( this.documentPlayer.isPlaying() || this.documentPlayer.isLooping()){
			this.documentPlayer.pause();
			// lineOut.removeListener(this);
			// this.documentPlayer.unpatch(decrackFilter);
			// decrackFilter.unpatch(clickFilter);
			// clickFilter.unpatch(preamplifer);
			preamplifer.unpatch(lineOut);
			LOG.info("PLAYER STOPPED.");
		}
		else {
			LOG.info("Player already stopped!");
		}
		// for (AudioDocumentListener listener : listeners) {
		// listener.audioPaused();
		// }
	}

	// The 2 following methods are for compatibility because
	// the audio player can not be accessed directly.
	public void addAudioPlayerListener(AudioPlayerListener listener) {
		getDocumentPlayer().addPlayerListener(listener);
	}

	public void removeAudioPlayerListener(AudioPlayerListener listener) {
		getDocumentPlayer().removePlayerListener(listener);
	}

	// how many samples we will generate every frame of the sketch (this will
	// impact how quickly the file is written)
	public static final int CHANNELS = 2;

	/**
	 * The code is inspired by
	 * https://github.com/ddf/Minim/blob/master/examples/Advanced/OfflineRendering/OfflineRendering.pde
	 * 
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             in case of an exception.
	 */
	public synchronized void saveTo(String fileName) /* throws IOException */ {
		AudioDocumentPlayer player = getDocumentPlayer();
		if (player.isPlaying()) {
			stop();
		}

		if (!fileName.endsWith(".mp3") && !fileName.endsWith(".wav") && !fileName.endsWith(".aiff")) {
			fileName += ".wav";
		}
		WaveCleaner app = WaveCleaner.getApplication();

		// we will use a SignalSplitter as our Recordable source so that we can
		// create an AudioRecorder for writing to disk.
		SignalSplitter out = new SignalSplitter(new AudioFormat(this.sampleRate, 16, CHANNELS, true, false), this.bufferSize);

		// creates a recorder that will write out to a file
		AudioRecorder recorder = app.createRecorder(out, fileName);

		// create the buffer we will render into
		MultiChannelBuffer buffer = new MultiChannelBuffer(this.bufferSize, CHANNELS);

		// create the summer that will render the audio
		Summer summer = new Summer();
		// make sure it matches our file's sample rate and channel numbers
		summer.setSampleRate(this.sampleRate);
		summer.setChannelCount(CHANNELS);

		preamplifer.patch(summer);

		player.play(1000 * 100);
		recorder.beginRecord();
		int renderCount = (int) (this.sampleRate * player.getStream().getMillisecondLength() / 1000.0) / this.bufferSize + 1;
		boolean stopped = false;
		for (int i = 0; i < renderCount && !(stopped = Thread.interrupted()); i++) {
			switch (CHANNELS) {
			case 1:
				// render a buffer
				summer.generate(buffer.getChannel(0));
				// push the buffer to the recorder via the SignalSplitter
				out.samples(buffer.getChannel(0)); // <>//
				break;

			case 2:
				// same as above, but for stereo audio
				summer.generate(buffer.getChannel(0), buffer.getChannel(1));
				out.samples(buffer.getChannel(0), buffer.getChannel(1));
				break;
			}
		}		
		recorder.endRecord();
		recorder.save();
		
		if( stopped ){
			// Delete the file created
			new File(fileName).delete();
		}
		
		summer.unpatch(preamplifer);
	}

	public synchronized void play(int pos) {
		int ms = (int) (pos * 1000.0 / this.getFormat().getSampleRate());
		AudioDocumentPlayer player = getDocumentPlayer();
		if (player.isPlaying()) {
			LOG.info("MOVED PLAY TO {}", pos);
			player.cue(ms);
			return;
		}

		// this.lineOut.addListener(this);
		preamplifer.patch(lineOut);
		player.rewind();
		player.play();
		player.cue(ms);
		LOG.info("START PLAY (from sample {})", pos);
	}

	/**
	 * Play in loop.
	 * 
	 * @param first
	 * @param last
	 */
	public synchronized void startLoop(int first, int last) {
		int begMs = (int) (first * 1000.0 / this.getFormat().getSampleRate());
		int endMs = (int) (last * 1000.0 / this.getFormat().getSampleRate());
		AudioDocumentPlayer player = getDocumentPlayer();
		player.setLoopPoints(begMs, endMs);
		player.loop();
		// player.cue(begMs);
	}

	public synchronized void endLoop() {
		AudioDocumentPlayer player = getDocumentPlayer();
		if (player.isLooping()) {
			player.play();
		}
	}

	// @Override
	// public void samples(float[] samp) {
	// samples(samp, samp);
	// }
	//
	// @Override
	// public void samples(float[] sampL, float[] sampR) {
	// int sample = (int) (this.documentPlayer.position() / 1_000.0 *
	// this.getFormat().getSampleRate());
	// if (sample > this.getNumberOfSamples() - 200) {
	// /** Stop if we have heard all the file */
	// this.stop();
	// }
	// for (AudioDocumentListener listener : listeners) {
	// listener.audioPlayed(sample);
	// }
	// }

	public void dispose() {
		// When we detach the audio
	}
}
