package model;

import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;

/**
 * The audio format used by the application.
 * 
 * <p>
 * It implements the {@code Singleton} pattern to allow all instances of the
 * environment to get its information.
 * 
 * @author Francesco Del Duchetto
 * 
 */
public final class AudioSettings {

	/**
	 * The only existing entity of this class.
	 */
	private static final AudioSettings AUDIO_SETTINGS = new AudioSettings();

	/**
	 * The audio format used.
	 */
	private AudioFormat audioFormat;
	
	private static final int BIT_RATE = 115200;
	
	private static final int AUDIO_SAMPLE_SIZE_BITS = 16;

	/**
	 * The size of the sample.
	 */
	private static final int SAMPLE_SIZE_BITS = 40;

	/**
	 * The number of samples per second.
	 */
	private static final int SAMPLE_RATE = BIT_RATE / SAMPLE_SIZE_BITS;

	/**
	 * The number of channels used.
	 */
	private static final int CHANNELS = 1;

	/**
	 * Stores true if the samples are signed, false if unsigned.
	 */
	private static final boolean SIGNED = true;

	/**
	 * Stores true if the samples are stored in big endian, false if there are
	 * stored in little endian.
	 */
	private static final boolean BIG_ENDIAN = false;

	/**
	 * The number of frame in a buffer.
	 */
	private static final int BUFFER_FRAMES = 1024;

	/**
	 * Creates a new {@code AudioSettings}.
	 */
	private AudioSettings() {
		audioFormat = new AudioFormat(SAMPLE_RATE, 16, CHANNELS,
				SIGNED, BIG_ENDIAN);
	}

	/**
	 * @return the only instance of this class.
	 */
	public static AudioSettings getAudioSettings() {
		return AUDIO_SETTINGS;
	}

	/**
	 * @return the {@code AudioFormat} that must be used by the lines.
	 */
	public AudioFormat getAudioFormat() {
		return this.audioFormat;
	}

	/**
	 * @return
	 */
	public int getBitRate() {
		return BIT_RATE;
	}

	/**
	 * @return the number of frames in a buffer.
	 */
	public int getBufferFrames() {
		return AudioSettings.BUFFER_FRAMES;
	}

	/**
	 * @return the length of the buffer in bytes.
	 */
	public int getBufferLength() {
		return AudioSettings.BUFFER_FRAMES * AUDIO_SAMPLE_SIZE_BITS / 8;
	}

	/**
	 * @return the sample rate.
	 */
	public int getSampleRate() {
		return AudioSettings.SAMPLE_RATE;
	}

	/**
	 * @return the audio sample size in bits.
	 */
	public int getAudioSampleSizeInBits() {
		return AudioSettings.AUDIO_SAMPLE_SIZE_BITS;
	}
	
	/**
	 * @return the sample size in bits.
	 */
	public int getSampleSizeInBits() {
		return AudioSettings.SAMPLE_SIZE_BITS;
	}


	/**
	 * @return the length of the buffer in shorts.
	 */
	public int getShortBufferLength() {
		return this.getBufferLength() / 2;
	}

	/**
	 * @return the number of channels.
	 */
	public int getNumChannels() {
		return AudioSettings.CHANNELS;
	}

	/**
	 * @return true if the samples are signed, false if they are unsigned.
	 */
	public boolean isSigned() {
		return AudioSettings.SIGNED;
	}

	/**
	 * @return the order of bytes.
	 */
	public ByteOrder getByteOrder() {
		if (AudioSettings.BIG_ENDIAN) {
			return ByteOrder.BIG_ENDIAN;
		} else {
			return ByteOrder.LITTLE_ENDIAN;
		}
	}
}
